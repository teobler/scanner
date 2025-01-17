package org.archguard.rule.impl.container.rules

import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.RuleContext
import org.archguard.rule.core.Severity
import org.archguard.rule.impl.container.ContainerRule
import org.archguard.rule.impl.container.model.ContainerResource

private val HAS_UPPERCASE_RULE = ".*[A-Z].*".toRegex()
private val PARAMETER_IN_URL = "\\{[a-zA-Z?:]+\\}".toRegex()

class NotUppercaseRule: ContainerRule() {
    init {
        this.name = "NotUppercaseRule"
        this.key = this.javaClass.name
        this.description = "url should not end with crud (like /create)"
        this.severity = Severity.WARN
    }

    override fun visitResource(resource: ContainerResource, context: RuleContext, callback: IssueEmit) {
        val split = resource.sourceUrl.split("/")
        split.forEach {
            if(HAS_UPPERCASE_RULE.matches(it) && !PARAMETER_IN_URL.matches(it)) {
                callback(this, IssuePosition())
            }
        }
    }
}