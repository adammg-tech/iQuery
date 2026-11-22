package com.adammg.iquery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewFeedResponse(
    @SerialName("feed") val feed: ReviewFeed? = null
)

@Serializable
data class ReviewFeed(
    @SerialName("entry") val entry: List<ReviewEntry>? = emptyList()
)

@Serializable
data class ReviewEntry(
    @SerialName("id") val id: RSSLabel? = null,
    @SerialName("title") val title: RSSLabel? = null,
    @SerialName("content") val content: RSSLabel? = null,
    @SerialName("author") val author: RSSAuthor? = null,
    @SerialName("im:rating") val rating: RSSLabel? = null,
    @SerialName("im:version") val version: RSSLabel? = null
) {
    val reviewTitle: String
        get() = title?.label ?: ""

    val reviewContent: String
        get() = content?.label ?: ""

    val authorName: String
        get() = author?.name?.label ?: "App Store User"

    val ratingStars: Int
        get() = rating?.label?.toIntOrNull() ?: 5

    val appVersion: String
        get() = version?.label ?: ""
}

@Serializable
data class RSSLabel(
    @SerialName("label") val label: String? = null
)

@Serializable
data class RSSAuthor(
    @SerialName("name") val name: RSSLabel? = null
)
