package org.archguard.scanner.sourcecode.database

import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable
import org.archguard.ident.mysql.MysqlIdentApp
import org.slf4j.LoggerFactory

@Serializable
data class SqlRecord(
    val Package: String = "",
    val ClassName: String = "",
    val FunctionName: String = "",
    val Tables: List<String> = listOf(),
    val Sql: List<String> = listOf()
)

class MysqlAnalyser {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun analysisByNode(node: CodeDataStruct, workspace: String): MutableList<SqlRecord> {
        val logs: MutableList<SqlRecord> = mutableListOf()
        // by annotation: identify
        node.Functions.forEach { function ->
            val sqls: MutableList<String> = mutableListOf()
            val tables: MutableSet<String> = mutableSetOf()

            function.Annotations.forEach {
                if (it.Name == "SqlQuery" && it.KeyValues.isNotEmpty()) {
                    val originSql = it.KeyValues[0].Value
                    val pureValue = sqlify(originSql)
                    if (MysqlIdentApp.analysis(pureValue) != null) {
                        tables += MysqlIdentApp.analysis(pureValue)!!.tableNames
                    } else {
                        logger.warn("error for ${node.NodeName}.${function.Name} origin:$originSql \nnew:$pureValue")
                    }

                    sqls += pureValue
                }
            }

            function.FunctionCalls.forEach {
                val callMethodName = it.FunctionName.split(".").last()
                if (callMethodName == "createQuery" && it.Parameters.isNotEmpty()) {
                    val originSql = it.Parameters[0].TypeValue
                    val pureValue = sqlify(originSql)
                    if (MysqlIdentApp.analysis(pureValue) != null) {
                        tables += MysqlIdentApp.analysis(pureValue)!!.tableNames
                    } else {
                        logger.warn("error for ${node.NodeName}.${function.Name} origin:$originSql\nnew:$pureValue")
                    }

                    sqls += pureValue
                }
            }

            if (sqls.size > 0) {
                logs += SqlRecord(
                    Package = node.Package,
                    ClassName = node.NodeName,
                    FunctionName = function.Name,
                    Tables = tables.toList(),
                    Sql = sqls
                )
            }
        }

        // by call: identify by jdbi

        return logs
    }

    fun sqlify(value: String): String {
        var text = handleRawString(value)
        text = removeBeginEndQuotes(text)
        text = removeVariableInLine(text)
        text = removeKotlinVariable(text)
        text = removeNextLine(text)
        text = removeJdbiValueBind(text)
        text = removePlus(text)
        text = processIn(text)
        return text
    }

    private val RAW_STRING_REGEX = "\"\"\"(((.*?)|\n)+)\"\"\"".toRegex()
    private fun handleRawString(text: String): String {
        val rawString = RAW_STRING_REGEX.find(text)
        if (rawString != null) {
            return rawString.groups[1]!!.value
        }

        return text
    }

    // some text: "\"+orderSqlPiece+\""
    private val VARIABLE_IN_LINE = "(\"\\\\\"\\+[a-zA-Z_]+\\+\"\\\\\")".toRegex()
    private fun removeVariableInLine(text: String): String {
        val find = VARIABLE_IN_LINE.find(text)
        if (find != null) {
            return text.replace(VARIABLE_IN_LINE, "*")
        }

        return text
    }

    private val IN_REGEX = "in\\s+\\((\\s+)?<([a-zA-Z]+)>(\\s+)?\\)".toRegex()
    private fun processIn(text: String): String {
        val find = IN_REGEX.find(text)
        if (find != null) {
            return text.replace(IN_REGEX, "in (:${find.groups[2]!!.value})")
        }

        return text
    }


    // example: `where system_id=:systemId ` => `where system_id=''`
    private val JDBI_VALUE_BIND = ":([a-zA-Z_]+)".toRegex()
    private fun removeJdbiValueBind(text: String): String {
        val find = JDBI_VALUE_BIND.find(text)
        if (find != null) {
            return text.replace(JDBI_VALUE_BIND, "''")
        }

        return text
    }

    private val KOTLIN_VARIABLE = "\\\$([a-zA-Z_]+)".toRegex()
    private fun removeKotlinVariable(text: String): String {
        val find = KOTLIN_VARIABLE.find(text)
        if (find != null) {
            return text.replace(KOTLIN_VARIABLE, "")
        }

        return text
    }

    private fun removeNextLine(text: String) = text.replace("\n", "")
    private fun removePlus(text: String) = text.replace("\"+\"", "")
    private fun removeBeginEndQuotes(value: String) = value.removeSuffix("\"").removePrefix("\"")
}