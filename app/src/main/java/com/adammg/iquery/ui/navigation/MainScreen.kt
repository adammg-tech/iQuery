package com.adammg.iquery.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adammg.iquery.data.repository.SettingsPreferences
import com.adammg.iquery.ui.screens.apps.AppsScreen
import com.adammg.iquery.ui.screens.detail.AppDetailsScreen
import com.adammg.iquery.ui.screens.developer.DeveloperAppsScreen
import com.adammg.iquery.ui.screens.games.GamesScreen
import com.adammg.iquery.ui.screens.reviews.ReviewsScreen
import com.adammg.iquery.ui.screens.search.SearchScreen
import com.adammg.iquery.ui.screens.section.SectionAppsScreen
import com.adammg.iquery.ui.screens.settings.SettingsScreen
import com.adammg.iquery.ui.theme.ElectricViolet
import com.adammg.iquery.ui.theme.IQueryTheme

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }

    var isToolbarVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -5f) {
                    isToolbarVisible = false
                } else if (available.y > 5f) {
                    isToolbarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val context = LocalContext.current
    val settingsPreferences = remember { SettingsPreferences.getInstance(context) }
    val isDynamicColor by settingsPreferences.isDynamicColor.collectAsState(initial = false)

    IQueryTheme(dynamicColor = isDynamicColor) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Games.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(Screen.Games.route) {
                        GamesScreen(
                            onAppClick = { trackId ->
                                navController.navigate(Screen.Detail.createRoute(trackId))
                            },
                            onMoreClick = { sectionType, isGamesOnly ->
                                navController.navigate(Screen.SectionApps.createRoute(sectionType, isGamesOnly))
                            }
                        )
                    }

                    composable(Screen.Apps.route) {
                        AppsScreen(
                            onAppClick = { trackId ->
                                navController.navigate(Screen.Detail.createRoute(trackId))
                            },
                            onMoreClick = { sectionType, isGamesOnly ->
                                navController.navigate(Screen.SectionApps.createRoute(sectionType, isGamesOnly))
                            }
                        )
                    }

                    composable(Screen.Search.route) {
                        SearchScreen(
                            onAppClick = { trackId ->
                                navController.navigate(Screen.Detail.createRoute(trackId))
                            }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen()
                    }

                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(
                            navArgument("trackId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L
                        AppDetailsScreen(
                            trackId = trackId,
                            onBackClick = { navController.popBackStack() },
                            onDeveloperClick = { artistId, developerName ->
                                navController.navigate(Screen.DeveloperApps.createRoute(artistId, developerName))
                            },
                            onReviewsMoreClick = { appId, appName ->
                                navController.navigate(Screen.AppReviews.createRoute(appId, appName))
                            }
                        )
                    }

                    composable(
                        route = Screen.DeveloperApps.route,
                        arguments = listOf(
                            navArgument("artistId") { type = NavType.LongType },
                            navArgument("developerName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val artistId = backStackEntry.arguments?.getLong("artistId") ?: 0L
                        val developerName = backStackEntry.arguments?.getString("developerName") ?: ""
                        DeveloperAppsScreen(
                            artistId = artistId,
                            developerName = developerName,
                            onBackClick = { navController.popBackStack() },
                            onAppClick = { trackId ->
                                navController.navigate(Screen.Detail.createRoute(trackId))
                            }
                        )
                    }

                    composable(
                        route = Screen.SectionApps.route,
                        arguments = listOf(
                            navArgument("sectionType") { type = NavType.StringType },
                            navArgument("isGamesOnly") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        val sectionType = backStackEntry.arguments?.getString("sectionType") ?: "top_free"
                        val isGamesOnly = backStackEntry.arguments?.getBoolean("isGamesOnly") ?: false
                        SectionAppsScreen(
                            sectionType = sectionType,
                            isGamesOnly = isGamesOnly,
                            onBackClick = { navController.popBackStack() },
                            onAppClick = { trackId ->
                                navController.navigate(Screen.Detail.createRoute(trackId))
                            }
                        )
                    }

                    composable(
                        route = Screen.AppReviews.route,
                        arguments = listOf(
                            navArgument("appId") { type = NavType.LongType },
                            navArgument("appName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val appId = backStackEntry.arguments?.getLong("appId") ?: 0L
                        val appName = backStackEntry.arguments?.getString("appName") ?: ""
                        ReviewsScreen(
                            appId = appId,
                            appName = appName,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }

            // Floating pill toolbar overlaid at the bottom with always-visible labels
            if (shouldShowBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = isToolbarVisible,
                        enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(36.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            tonalElevation = 8.dp,
                            shadowElevation = 12.dp,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Screen.bottomNavItems.forEach { screen ->
                                    val isSelected = currentRoute == screen.route
                                    val backgroundColor = if (isSelected) ElectricViolet else Color.Transparent
                                    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                                    Surface(
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(28.dp),
                                        color = backgroundColor,
                                        modifier = Modifier.height(44.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            screen.icon?.let { icon ->
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = screen.title,
                                                    tint = contentColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = screen.title,
                                                color = contentColor,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
