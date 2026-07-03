package com.yuseob.chaenggimdol

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test

class MainActivityLaunchTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchesIntoOnboardingOrHome() {
        val onboarding = compose
            .onAllNodesWithText("자주 챙기는 물건을 골라봐요")
            .fetchSemanticsNodes()
        val home = compose
            .onAllNodesWithText(
                "오늘도 놓치는 것 없이",
                substring = true,
            )
            .fetchSemanticsNodes()

        check(onboarding.isNotEmpty() || home.isNotEmpty())
    }
}
