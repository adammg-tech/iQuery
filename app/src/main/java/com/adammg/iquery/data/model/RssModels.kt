package com.adammg.iquery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- 1. Apple Marketing Tools RSS Feed (v2: top-free, top-paid) ---

@Serializable
data class MarketingRssFeedResponse(
    @SerialName("feed") val feed: MarketingRssFeed? = null
)

@Serializable
data class MarketingRssFeed(
    @SerialName("results") val results: List<MarketingRssAppItem>? = emptyList()
)

@Serializable
data class MarketingRssAppItem(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("artistName") val artistName: String? = null,
    @SerialName("artworkUrl100") val artworkUrl100: String? = null,
    @SerialName("genres") val genres: List<MarketingRssGenre>? = emptyList(),
    @SerialName("url") val url: String? = null
) {
    fun toAppItem(isFree: Boolean): AppItem {
        val trackIdLong = id?.toLongOrNull() ?: 0L
        val genreName = genres?.firstOrNull()?.name ?: if (isFree) "Free App" else "Paid App"
        return AppItem(
            trackId = trackIdLong,
            trackName = name ?: "",
            artistName = artistName ?: "",
            artworkUrl100 = artworkUrl100,
            artworkUrl512 = artworkUrl100?.replace("100x100bb", "512x512bb"),
            formattedPrice = if (isFree) "Free" else "Paid",
            price = if (isFree) 0.0 else 0.99,
            trackViewUrl = url,
            primaryGenreName = genreName
        )
    }
}

@Serializable
data class MarketingRssGenre(
    @SerialName("name") val name: String? = null
)

// --- 2. iTunes Classic RSS Feed (newapplications) ---

@Serializable
data class ClassicRssFeedResponse(
    @SerialName("feed") val feed: ClassicRssFeed? = null
)

@Serializable
data class ClassicRssFeed(
    @SerialName("entry") val entry: List<ClassicRssAppItem>? = emptyList()
)

@Serializable
data class ClassicRssAppItem(
    @SerialName("id") val id: ClassicRssId? = null,
    @SerialName("im:name") val name: RSSLabel? = null,
    @SerialName("im:artist") val artist: RSSLabel? = null,
    @SerialName("im:image") val image: List<RSSLabel>? = emptyList(),
    @SerialName("category") val category: ClassicRssCategory? = null,
    @SerialName("im:price") val price: ClassicRssPrice? = null
) {
    fun toAppItem(): AppItem {
        val trackIdLong = id?.attributes?.imId?.toLongOrNull() ?: 0L
        val artwork = image?.lastOrNull()?.label ?: image?.firstOrNull()?.label
        val categoryName = category?.attributes?.label ?: "App"
        val priceStr = price?.attributes?.amount?.let { if (it == "0.00000") "Free" else "$${it}" } ?: "Free"

        return AppItem(
            trackId = trackIdLong,
            trackName = name?.label ?: "",
            artistName = artist?.label ?: "",
            artworkUrl100 = artwork,
            artworkUrl512 = artwork?.replace("100x100bb", "512x512bb"),
            formattedPrice = priceStr,
            primaryGenreName = categoryName
        )
    }
}

@Serializable
data class ClassicRssId(
    @SerialName("attributes") val attributes: ClassicRssIdAttributes? = null
)

@Serializable
data class ClassicRssIdAttributes(
    @SerialName("im:id") val imId: String? = null
)

@Serializable
data class ClassicRssCategory(
    @SerialName("attributes") val attributes: ClassicRssCategoryAttributes? = null
)

@Serializable
data class ClassicRssCategoryAttributes(
    @SerialName("label") val label: String? = null
)

@Serializable
data class ClassicRssPrice(
    @SerialName("attributes") val attributes: ClassicRssPriceAttributes? = null
)

@Serializable
data class ClassicRssPriceAttributes(
    @SerialName("amount") val amount: String? = null
)
