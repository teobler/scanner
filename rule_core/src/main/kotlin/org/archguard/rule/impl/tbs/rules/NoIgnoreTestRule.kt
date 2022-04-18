package org.archguard.rule.impl.tbs.rules

import chapi.domain.core.CodeAnnotation
import chapi.domain.core.CodeFunction
import org.archguard.rule.core.Severity
import org.archguard.rule.core.SmellEmit
import org.archguard.rule.impl.tbs.TbsRule
import org.archguard.rule.impl.tbs.smellPosition

class NoIgnoreTestRule : TbsRule() {
    init {
        this.name = "NoIgnoreTest"
        this.key = this.javaClass.name
        this.description = "The test is ignore or disabled"
        this.severity = Severity.WARN
    }

    override fun visitAnnotation(function: CodeFunction, annotation: CodeAnnotation, index: Int, callback: SmellEmit) {
        if (annotation.Name == "Ignore" || annotation.Name == "Disabled") {
            callback(this, function.Position.smellPosition())
        }
    }
}