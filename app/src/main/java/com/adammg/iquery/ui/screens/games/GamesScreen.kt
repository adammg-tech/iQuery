package com.adammg.iquery.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adammg.iquery.data.model.AppGenre
import com.adammg.iquery.data.repository.StorefrontPreferences
import com.adammg.iquery.ui.components.AppListItem
import com.adammg.iquery.ui.components.BrandHeader
import com.adammg.iquery.ui.components.CountrySelectorBottomSheet
import com.adammg.iquery.ui.components.SingleSelectConnectedButtonGroupWithFlow
import com.adammg.iquery.ui.components.TopChartsSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onAppClick: (Long) -> Unit,
    onMoreClick: (String, Boolean) -> Unit,
    viewModel: GamesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCountrySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BrandHeader(
            activeCountry = uiState.activeCountry,
            onCountryClick = { showCountrySheet = true }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Alphabetical Game Categories Connected Button Group
            item {
                Spacer(modifier = Modifier.height(6.dp))
                SingleSelectConnectedButtonGroupWithFlow(
                    categories = AppGenre.DEFAULT_GAMES_CATEGORIES,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            // Show Skeleton loading during tab/category switch
            if (uiState.isLoading) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    TopChartsSkeleton()
                }
            } else {
                // Section 1: Top Free Games (Default 3 items + More button)
                if (uiState.topFreeGames.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        SectionHeader(
                            title = "Top Free Games",
                            onMoreClick = { onMoreClick("top_free", true) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(uiState.topFreeGames.take(3)) { game ->
                        AppListItem(
                            appItem = game,
                            onClick = { onAppClick(game.trackId) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                }

                // Section 2: Top Paid Games (Default 3 items + More button)
                if (uiState.topPaidGames.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(
                            title = "Top Paid Games",
                            onMoreClick = { onMoreClick("top_paid", true) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(uiState.topPaidGames.take(3)) { game ->
                        AppListItem(
                            appItem = game,
                            onClick = { onAppClick(game.trackId) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                }

                // Section 3: New Games (Default 3 items + More button)
                if (uiState.newGames.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(
                            title = "New & Updated Games",
                            onMoreClick = { onMoreClick("new_apps", true) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(uiState.newGames.take(3)) { game ->
                        AppListItem(
                            appItem = game,
                            onClick = { onAppClick(game.trackId) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCountrySheet) {
        CountrySelectorBottomSheet(
            activeCountry = uiState.activeCountry,
            sheetState = sheetState,
            onDismissRequest = { showCountrySheet = false },
            onCountrySelected = { country ->
                StorefrontPreferences.getInstance(context).setCountry(country)
                showCountrySheet = false
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "More",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onMoreClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
