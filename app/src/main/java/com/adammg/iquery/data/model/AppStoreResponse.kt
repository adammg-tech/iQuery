package com.adammg.iquery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppStoreResponse(
    @SerialName("resultCount") val resultCount: Int = 0,
    @SerialName("results") val results: List<AppItem> = emptyList()
)
