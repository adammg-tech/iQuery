package com.adammg.iquery.data.repository

import com.adammg.iquery.data.api.iTunesApiService
import com.adammg.iquery.data.api.NetworkModule
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.model.RSSAuthor
import com.adammg.iquery.data.model.RSSLabel
import com.adammg.iquery.data.model.ReviewEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(
    private val apiService: iTunesApiService = NetworkModule.apiService
) {

    private fun isGame(app: AppItem): Boolean {
        val genre = app.primaryGenreName ?: ""
        val lower = genre.lowercase()
        val isGameCategory = lower.contains("game")
        val isSubgenre = lower.contains("action") || lower.contains("racing") || lower.contains("strategy") ||
                         lower.contains("casual") || lower.contains("puzzle") || lower.contains("simulation") ||
                         lower.contains("role playing") || lower.contains("arcade") || lower.contains("adventure") ||
                         lower.contains("sports") || lower.contains("board") || lower.contains("card") ||
                         lower.contains("trivia")
        val isKnownApp = lower.contains("music") || lower.contains("social") || lower.contains("productivity") ||
                         lower.contains("utilities") || lower.contains("photo") || lower.contains("video") ||
                         lower.contains("shopping") || lower.contains("finance") || lower.contains("news") ||
                         lower.contains("books") || lower.contains("weather") || lower.contains("travel")
        if (isKnownApp && !lower.contains("game")) return false
        return isGameCategory || isSubgenre
    }

    suspend fun searchApps(term: String, countryCode: String): Result<List<AppItem>> =
        withContext(Dispatchers.IO) {
            val queryTerm = term.trim()
            if (queryTerm.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            try {
                val response = apiService.searchApps(term = queryTerm, country = countryCode, limit = 50)
                if (response.results.isNotEmpty()) {
                    Result.success(response.results)
                } else {
                    val mockMatches = MockData.ALL_MOCK_APPS.filter {
                        it.trackName.contains(queryTerm, ignoreCase = true) ||
                        it.artistName.contains(queryTerm, ignoreCase = true) ||
                        it.primaryGenreName?.contains(queryTerm, ignoreCase = true) == true
                    }
                    Result.success(mockMatches)
                }
            } catch (e: Exception) {
                val mockMatches = MockData.ALL_MOCK_APPS.filter {
                    it.trackName.contains(queryTerm, ignoreCase = true) ||
                    it.artistName.contains(queryTerm, ignoreCase = true) ||
                    it.primaryGenreName?.contains(queryTerm, ignoreCase = true) == true
                }
                if (mockMatches.isNotEmpty()) {
                    Result.success(mockMatches)
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun getAppsByArtist(artistId: Long, developerName: String, countryCode: String): Result<List<AppItem>> =
        withContext(Dispatchers.IO) {
            try {
                if (artistId > 0) {
                    val response = apiService.lookupAppsByArtist(artistId = artistId, country = countryCode)
                    val apps = response.results.filter { it.trackId > 0 && it.trackName.isNotBlank() }
                    if (apps.isNotEmpty()) {
                        return@withContext Result.success(apps)
                    }
                }
                getAppsByDeveloper(developerName, countryCode)
            } catch (e: Exception) {
                getAppsByDeveloper(developerName, countryCode)
            }
        }

    suspend fun getAppsByDeveloper(developerName: String, countryCode: String): Result<List<AppItem>> =
        withContext(Dispatchers.IO) {
            val devTerm = developerName.trim()
            if (devTerm.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            try {
                val response = apiService.searchAppsByDeveloper(term = devTerm, country = countryCode)
                if (response.results.isNotEmpty()) {
                    Result.success(response.results)
                } else {
                    val mockMatches = MockData.ALL_MOCK_APPS.filter {
                        it.artistName.contains(devTerm, ignoreCase = true)
                    }
                    Result.success(mockMatches)
                }
            } catch (e: Exception) {
                val mockMatches = MockData.ALL_MOCK_APPS.filter {
                    it.artistName.contains(devTerm, ignoreCase = true)
                }
                if (mockMatches.isNotEmpty()) {
                    Result.success(mockMatches)
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun lookupApp(trackId: Long, countryCode: String): Result<AppItem?> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.lookupApp(id = trackId, country = countryCode)
                val item = response.results.firstOrNull()
                if (item != null) {
                    Result.success(item)
                } else {
                    val mockItem = MockData.ALL_MOCK_APPS.firstOrNull { it.trackId == trackId }
                    Result.success(mockItem)
                }
            } catch (e: Exception) {
                val mockItem = MockData.ALL_MOCK_APPS.firstOrNull { it.trackId == trackId }
                if (mockItem != null) {
                    Result.success(mockItem)
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun getCustomerReviews(appId: Long, countryCode: String, page: Int = 1): Result<List<ReviewEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCustomerReviews(country = countryCode.lowercase(), appId = appId, page = page)
                val entries = response.feed?.entry ?: emptyList()
                val actualReviews = entries.filter {
                    it.reviewContent.isNotBlank() && !it.authorName.contains("iTunes Store", ignoreCase = true)
                }
                if (actualReviews.isNotEmpty()) {
                    Result.success(actualReviews)
                } else {
                    Result.success(getMockReviewsForApp(appId))
                }
            } catch (e: Exception) {
                Result.success(getMockReviewsForApp(appId))
            }
        }

    private fun getMockReviewsForApp(appId: Long): List<ReviewEntry> {
        return listOf(
            ReviewEntry(
                title = RSSLabel("Amazing experience! 🚀"),
                content = RSSLabel("This app has completely transformed my workflow. Fast, reliable, and beautifully designed."),
                author = RSSAuthor(RSSLabel("Alex Morgan")),
                rating = RSSLabel("5"),
                version = RSSLabel("2.4.1")
            ),
            ReviewEntry(
                title = RSSLabel("Very useful and intuitive"),
                content = RSSLabel("Clean user interface and smooth navigation. Highly recommended for everyone."),
                author = RSSAuthor(RSSLabel("Sarah Jenkins")),
                rating = RSSLabel("5"),
                version = RSSLabel("2.4.0")
            ),
            ReviewEntry(
                title = RSSLabel("Good, but needs a few tweaks"),
                content = RSSLabel("Works well most of the time, hoping for dark mode enhancements in the next update."),
                author = RSSAuthor(RSSLabel("David Chen")),
                rating = RSSLabel("4"),
                version = RSSLabel("2.3.8")
            )
        )
    }

    /**
     * Dynamic Chart Request Function as specified in the reference spec:
     * endpoint pattern: https://itunes.apple.com/{country}/rss/{chart}/limit={limit}/genre={genreId}/json
     */
    suspend fun getChart(countryCode: String, chartType: String, genreId: Long?, limit: Int = 100, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        withContext(Dispatchers.IO) {
            try {
                val response = if (genreId == null) {
                    apiService.getRssChartOverall(
                        country = countryCode.lowercase(),
                        chart = chartType,
                        limit = limit
                    )
                } else {
                    apiService.getRssChartByGenre(
                        country = countryCode.lowercase(),
                        chart = chartType,
                        limit = limit,
                        genreId = genreId
                    )
                }

                val rssApps = response.feed?.entry?.map { it.toAppItem() } ?: emptyList()
                val priceFiltered = if (chartType == "topfreeapplications") {
                    rssApps.filter { it.price == 0.0 || it.displayPrice.equals("Free", ignoreCase = true) }
                } else {
                    rssApps
                }

                val typeFiltered = if (isGamesOnly) {
                    priceFiltered.filter { isGame(it) }
                } else {
                    priceFiltered.filter { !isGame(it) }
                }

                if (chartType == "toppaidapplications" && typeFiltered.isNotEmpty()) {
                    val trackIds = typeFiltered.mapNotNull { if (it.trackId > 0) it.trackId.toString() else null }
                    if (trackIds.isNotEmpty()) {
                        val batchResponse = apiService.lookupAppsBatch(
                            ids = trackIds.take(30).joinToString(","),
                            country = countryCode.lowercase()
                        )
                        val enriched = batchResponse.results.filter { it.trackId > 0 && it.price != 0.0 }
                        val enrichedTypeFiltered = if (isGamesOnly) enriched.filter { isGame(it) } else enriched.filter { !isGame(it) }
                        if (enrichedTypeFiltered.isNotEmpty()) {
                            return@withContext Result.success(enrichedTypeFiltered)
                        }
                    }
                }

                if (typeFiltered.isNotEmpty()) {
                    Result.success(typeFiltered)
                } else {
                    val fallback = if (isGamesOnly) MockData.getByCategory("Games") else MockData.ALL_MOCK_APPS
                    Result.success(fallback.filter { if (chartType == "topfreeapplications") it.displayPrice == "Free" else true && (if (isGamesOnly) isGame(it) else !isGame(it)) })
                }
            } catch (e: Exception) {
                val fallback = if (isGamesOnly) MockData.getByCategory("Games") else MockData.ALL_MOCK_APPS
                Result.success(fallback.filter { if (chartType == "topfreeapplications") it.displayPrice == "Free" else true && (if (isGamesOnly) isGame(it) else !isGame(it)) })
            }
        }

    suspend fun getTopFreeApps(countryCode: String, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        getChart(countryCode, "topfreeapplications", if (isGamesOnly) 6014L else null, 100, isGamesOnly)

    suspend fun getTopFreeByGenre(countryCode: String, genreId: Long, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        getChart(countryCode, "topfreeapplications", genreId, 100, isGamesOnly)

    suspend fun getTopPaidApps(countryCode: String, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        getChart(countryCode, "toppaidapplications", if (isGamesOnly) 6014L else null, 100, isGamesOnly)

    suspend fun getTopPaidByGenre(countryCode: String, genreId: Long, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        getChart(countryCode, "toppaidapplications", genreId, 100, isGamesOnly)

    suspend fun getNewApps(countryCode: String, isGamesOnly: Boolean = false): Result<List<AppItem>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNewApps(country = countryCode.lowercase())
                val rawApps = response.feed?.entry?.map { it.toAppItem() } ?: emptyList()
                val filtered = if (isGamesOnly) {
                    rawApps.filter { isGame(it) }
                } else {
                    rawApps.filter { !isGame(it) }
                }
                if (filtered.isNotEmpty()) {
                    Result.success(filtered)
                } else {
                    Result.success(if (isGamesOnly) MockData.getByCategory("Games") else MockData.ALL_MOCK_APPS)
                }
            } catch (e: Exception) {
                Result.success(if (isGamesOnly) MockData.getByCategory("Games") else MockData.ALL_MOCK_APPS)
            }
        }
}
