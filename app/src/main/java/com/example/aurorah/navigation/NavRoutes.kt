package com.example.aurorah.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AurorahRoute : NavKey {

    @Serializable
    data object Home : AurorahRoute

    @Serializable
    data object About : AurorahRoute

    @Serializable
    data object Volunteer : AurorahRoute

    @Serializable
    data object RequestAssistance : AurorahRoute

    @Serializable
    data object Donate : AurorahRoute

    @Serializable
    data object ImpactNews : AurorahRoute

    @Serializable
    data object Login : AurorahRoute

    @Serializable
    data object Register : AurorahRoute

    @Serializable
    data object Settings : AurorahRoute

    @Serializable
    data object AdminDashboard : AurorahRoute
}
