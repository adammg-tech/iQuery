package com.adammg.iquery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Serializable
data class AppItem(
    @SerialName("trackId") val trackId: Long = 0L,
    @SerialName("artistId") val artistId: Long? = null,
    @SerialName("trackName") val trackName: String = "",
    @SerialName("artistName") val artistName: String = "",
    @SerialName("supportedDevices") val supportedDevices: List<String> = emptyList(),
    @SerialName("artworkUrl512") val artworkUrl512: String? = null,
    @SerialName("artworkUrl100") val artworkUrl100: String? = null,
    @SerialName("artworkUrl60") val artworkUrl60: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("screenshotUrls") val screenshotUrls: List<String> = emptyList(),
    @SerialName("ipadScreenshotUrls") val ipadScreenshotUrls: List<String> = emptyList(),
    @SerialName("averageUserRating") val averageUserRating: Double? = null,
    @SerialName("userRatingCount") val userRatingCount: Long? = null,
    @SerialName("formattedPrice") val formattedPrice: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("trackViewUrl") val trackViewUrl: String? = null,
    @SerialName("version") val version: String? = null,
    @SerialName("currentVersionReleaseDate") val currentVersionReleaseDate: String? = null,
    @SerialName("primaryGenreName") val primaryGenreName: String? = null,
    @SerialName("contentAdvisoryRating") val contentAdvisoryRating: String? = null,
    @SerialName("sellerName") val sellerName: String? = null,
    @SerialName("fileSizeBytes") val fileSizeBytes: String? = null,
    @SerialName("genres") val genres: List<String> = emptyList(),
    @SerialName("releaseNotes") val releaseNotes: String? = null
) {
    val bestArtworkUrl: String
        get() = artworkUrl512?.takeIf { it.isNotBlank() }
            ?: artworkUrl100?.takeIf { it.isNotBlank() }
            ?: artworkUrl60?.takeIf { it.isNotBlank() }
            ?: ""

    val allScreenshots: List<String>
        get() = (screenshotUrls + ipadScreenshotUrls)
            .distinct()
            .filter { it.isNotBlank() }

    val displayPrice: String
        get() {
            if (price == 0.0) return "Free"
            if (!formattedPrice.isNullOrBlank()) {
                val clean = formattedPrice.trim()
                if (clean == "$0.00" || clean == "0.00" || clean == "0.0" || clean.equals("Free", ignoreCase = true)) {
                    return "Free"
                }
                return clean
            }
            return "Free"
        }

    val formattedRating: String
        get() {
            val rating = averageUserRating ?: 0.0
            return String.format(Locale.US, "%.1f", rating)
        }

    val formattedRatingCount: String
        get() {
            val count = userRatingCount ?: 0L
            return when {
                count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000.0)
                count > 0 -> count.toString()
                else -> "No ratings"
            }
        }

    val formattedReleaseDate: String
        get() {
            val rawDate = currentVersionReleaseDate ?: return "N/A"
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date: Date = inputFormat.parse(rawDate) ?: return rawDate
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                outputFormat.format(date)
            } catch (e: Exception) {
                rawDate.take(10)
            }
        }

    val formattedFileSize: String
        get() {
            val bytesStr = fileSizeBytes ?: return "N/A"
            val bytes = bytesStr.toLongOrNull() ?: return "N/A"
            val mb = bytes / (1024.0 * 1024.0)
            return if (mb >= 1024) {
                String.format(Locale.US, "%.1f GB", mb / 1024.0)
            } else {
                String.format(Locale.US, "%.0f MB", mb)
            }
        }
}
