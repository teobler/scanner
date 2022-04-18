package org.archguard.rule.impl.tbs

import org.archguard.rule.core.RuleSet
import org.archguard.rule.core.RuleSetProvider
import org.archguard.rule.core.RuleType
import org.archguard.rule.impl.CasingRule
import org.archguard.rule.impl.tbs.rules.DuplicateAssertRule
import org.archguard.rule.impl.tbs.rules.EmptyTestRule
import org.archguard.rule.impl.tbs.rules.NoIgnoreTestRule
import org.archguard.rule.impl.tbs.rules.RedundantAssertionRule
import org.archguard.rule.impl.tbs.rules.RedundantPrintRule
import org.archguard.rule.impl.tbs.rules.SleepyTestRule
import org.archguard.rule.impl.tbs.rules.UnknownTestRule

/*
 * Low level provider
 */
class TestSmellProvider: RuleSetProvider {
    override fun get(): RuleSet {
        return RuleSet(
            RuleType.CHANGE_SMELL,
            "normal",
            CasingRule(),
            EmptyTestRule(),
            NoIgnoreTestRule(),
            SleepyTestRule(),
            RedundantPrintRule(),
            RedundantAssertionRule(),
            UnknownTestRule(),
            DuplicateAssertRule(),
        )
    }
}