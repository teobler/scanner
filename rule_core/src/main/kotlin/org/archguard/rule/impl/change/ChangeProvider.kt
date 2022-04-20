package org.archguard.rule.impl.change

import org.archguard.rule.core.RuleSet
import org.archguard.rule.core.RuleSetProvider
import org.archguard.rule.core.RuleType
import org.archguard.rule.impl.common.CasingRule

class ChangeProvider : RuleSetProvider {
    override fun get(): RuleSet {
        return RuleSet(
            RuleType.CHANGE_SMELL,
            "normal",
            CasingRule()
        )
    }
}