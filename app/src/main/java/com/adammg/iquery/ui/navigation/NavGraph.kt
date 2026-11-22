package com.adammg.iquery.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Games : Screen("games", "Games", Icons.Rounded.SportsEsports)
    object Apps : Screen("apps", "Apps", Icons.Rounded.Apps)
    object Search : Screen("search", "Search", Icons.Rounded.Search)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)
    object Detail : Screen("detail/{trackId}", "App Detail") {
        fun createRoute(trackId: Long) = "detail/$trackId"
    }
    object DeveloperApps : Screen("developer/{artistId}/{developerName}", "Developer Apps") {
        fun createRoute(artistId: Long, developerName: String) = "developer/$artistId/${Uri.encode(developerName)}"
    }
    object SectionApps : Screen("section_apps/{sectionType}/{isGamesOnly}", "Section Apps") {
        fun createRoute(sectionType: String, isGamesOnly: Boolean) = "section_apps/$sectionType/$isGamesOnly"
    }
    object AppReviews : Screen("reviews/{appId}/{appName}", "App Reviews") {
        fun createRoute(appId: Long, appName: String) = "reviews/$appId/${Uri.encode(appName)}"
    }

    companion object {
        val bottomNavItems = listOf(Games, Apps, Search, Settings)
    }
}
