package org.archguard.rule.impl.tbs.rules

import chapi.domain.core.CodeCall
import chapi.domain.core.CodeFunction
import org.archguard.rule.core.Severity
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.impl.tbs.TbsRule
import org.archguard.rule.impl.tbs.smellPosition

class UnknownTestRule : TbsRule() {
    private var hasAssert: Boolean = false

    init {
        this.name = "UnknownTest"
        this.key = this.javaClass.name
        this.description = "don't have assert"
        this.severity = Severity.WARN
    }

    override fun visitFunctionCall(function: CodeFunction, codeCall: CodeCall, index: Int, callback: IssueEmit) {
        if(isAssert(codeCall)) {
            this.hasAssert = true
        }
    }

    override fun beforeVisitFunction(function: CodeFunction, callback: IssueEmit) {
        this.hasAssert = false
    }

    override fun afterVisitFunction(function: CodeFunction, callback: IssueEmit) {
        if(!this.hasAssert) {
            callback(this, function.Position.smellPosition())
        }
    }
}
