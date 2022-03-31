package org.archguard.ident.mysql

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MysqlIdentAppTest {
    @Test
    internal fun identForSqlQuery() {
        val relation =
            MysqlIdentApp().analysis("select source_package as sourcePackage, source_class as sourceClass, source_method as sourceMethod, target_url as targetUrl, target_http_method as targetHttpMethod, system_id as systemId from container_demand where system_id = :systemId")

        assertEquals("container_demand", relation.tableNames.joinToString(","))
    }

    @Test
    internal fun identForCreateQuery() {
        val relation =
            MysqlIdentApp().analysis("select a.clzname aClz, b.clzname bClz from code_method a, code_method b, code_ref_method_callees mc where a.id = mc.a and b.id = mc.b and a.module='${'$'}module' and b.module='${'$'}module' and a.system_id='${'$'}systemId' and b.system_id='${'$'}systemId' and mc.system_id='${'$'}systemId'")

        assertEquals("code_method,code_ref_method_callees", relation.tableNames.joinToString(","))
    }
}