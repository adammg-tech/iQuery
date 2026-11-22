package com.adammg.iquery.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppGenre(
    val name: String,
    val genreId: Long?,
    val isGameSubgenre: Boolean = false
) {
    companion object {
        // Apps Categories (Section 2 of Spec)
        val DEFAULT_APPS_CATEGORIES = listOf(
            "All Apps",
            "Business",
            "Weather",
            "Utilities",
            "Travel",
            "Sports",
            "Social Networking",
            "Reference",
            "Productivity",
            "Photo & Video",
            "News",
            "Navigation",
            "Music",
            "Lifestyle",
            "Health & Fitness",
            "Finance",
            "Entertainment",
            "Education",
            "Books",
            "Medical",
            "Food & Drink",
            "Shopping",
            "Developer Tools",
            "Graphics & Design"
        )

        // Games Categories (Section 2 of Spec)
        val DEFAULT_GAMES_CATEGORIES = listOf(
            "All Games",
            "Action",
            "Adventure",
            "Casual",
            "Board",
            "Card",
            "Casino",
            "Dice",
            "Educational",
            "Family",
            "Kids",
            "Music",
            "Puzzle",
            "Racing",
            "Role Playing",
            "Simulation",
            "Sports",
            "Strategy",
            "Trivia",
            "Word"
        )

        // Exact Genre IDs (Section 3 of Spec)
        private val GENRE_ID_MAP = mapOf(
            "Business" to 6000L,
            "Weather" to 6001L,
            "Utilities" to 6002L,
            "Travel" to 6003L,
            "Sports" to 6004L, // Main category Sports
            "Social Networking" to 6005L,
            "Reference" to 6006L,
            "Productivity" to 6007L,
            "Photo & Video" to 6008L,
            "News" to 6009L,
            "Navigation" to 6010L,
            "Music" to 6011L, // Main category Music
            "Lifestyle" to 6012L,
            "Health & Fitness" to 6013L,
            "Games" to 6014L,
            "Finance" to 6015L,
            "Entertainment" to 6016L,
            "Education" to 6017L,
            "Books" to 6018L,
            "Medical" to 6020L,
            "Food & Drink" to 6023L,
            "Shopping" to 6024L,
            "Developer Tools" to 6026L,
            "Graphics & Design" to 6027L,

            // Games Subcategories
            "Action" to 7001L,
            "Adventure" to 7002L,
            "Casual" to 7003L,
            "Board" to 7004L,
            "Card" to 7005L,
            "Casino" to 7006L,
            "Dice" to 7007L,
            "Educational" to 7008L,
            "Family" to 7009L,
            "Kids" to 7010L,
            "GameMusic" to 7011L,
            "Puzzle" to 7012L,
            "Racing" to 7013L,
            "Role Playing" to 7014L,
            "Simulation" to 7015L,
            "GameSports" to 7016L,
            "Strategy" to 7017L,
            "Trivia" to 7018L,
            "Word" to 7019L
        )

        fun findGenreIdByName(name: String, isGamesOnly: Boolean): Long? {
            if (name.equals("All Apps", ignoreCase = true)) return null
            if (name.equals("All Games", ignoreCase = true)) return 6014L
            return when {
                isGamesOnly && name.equals("Sports", ignoreCase = true) -> 7016L
                isGamesOnly && name.equals("Music", ignoreCase = true) -> 7011L
                !isGamesOnly && name.equals("Sports", ignoreCase = true) -> 6004L
                !isGamesOnly && name.equals("Music", ignoreCase = true) -> 6011L
                else -> GENRE_ID_MAP.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value
            }
        }
    }
}
