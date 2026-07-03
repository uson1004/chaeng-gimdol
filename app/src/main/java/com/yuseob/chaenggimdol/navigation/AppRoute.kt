package com.yuseob.chaenggimdol.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Items : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data class Session(
        val sessionId: Long,
    ) : AppRoute

    @Serializable
    data class Complete(
        val sessionId: Long,
    ) : AppRoute
}
