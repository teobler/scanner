package org.archguard.rule.impl.tbs.rules

import chapi.domain.core.CodeFunction
import org.archguard.rule.core.Severity
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.impl.tbs.TbsRule
import org.archguard.rule.impl.tbs.smellPosition

private const val MAX_ASSERTS = 5

class DuplicateAssertRule : TbsRule() {
    init {
        this.name = "DuplicateAssertTest"
        this.key = this.javaClass.name
        this.description = "has multiple asserts"
        this.severity = Severity.WARN
    }

    override fun visitFunction(function: CodeFunction, index: Int, callback: IssueEmit) {
        if (function.FunctionCalls.isNotEmpty()) {
            val asserts = function.FunctionCalls.filter { isAssert(it) }.toList()
            if (asserts.size >= MAX_ASSERTS) {
                callback(this, function.Position.smellPosition())
            }
        }
    }
}
