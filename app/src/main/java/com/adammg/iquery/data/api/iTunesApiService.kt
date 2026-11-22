package com.adammg.iquery.data.api

import com.adammg.iquery.data.model.AppStoreResponse
import com.adammg.iquery.data.model.ClassicRssFeedResponse
import com.adammg.iquery.data.model.ReviewFeedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface iTunesApiService {

    @GET("search")
    suspend fun searchApps(
        @Query("term") term: String,
        @Query("country") country: String,
        @Query("entity") entity: String = "software",
        @Query("limit") limit: Int = 50
    ): AppStoreResponse

    @GET("search")
    suspend fun searchAppsByDeveloper(
        @Query("term") term: String,
        @Query("country") country: String,
        @Query("entity") entity: String = "software",
        @Query("attribute") attribute: String = "softwareDeveloper",
        @Query("limit") limit: Int = 50
    ): AppStoreResponse

    @GET("lookup")
    suspend fun lookupAppsByArtist(
        @Query("id") artistId: Long,
        @Query("country") country: String,
        @Query("entity") entity: String = "software",
        @Query("limit") limit: Int = 200
    ): AppStoreResponse

    @GET("lookup")
    suspend fun lookupApp(
        @Query("id") id: Long,
        @Query("country") country: String
    ): AppStoreResponse

    @GET("lookup")
    suspend fun lookupAppsBatch(
        @Query("id") ids: String,
        @Query("country") country: String
    ): AppStoreResponse

    @GET("{country}/rss/customerreviews/page={page}/id={id}/sortby={sort}/json")
    suspend fun getCustomerReviews(
        @Path("country") country: String,
        @Path("id") appId: Long,
        @Path("page") page: Int = 1,
        @Path("sort") sort: String = "mostrecent"
    ): ReviewFeedResponse

    @GET("{country}/rss/{chart}/limit={limit}/json")
    suspend fun getRssChartOverall(
        @Path("country") country: String,
        @Path("chart") chart: String,
        @Path("limit") limit: Int = 100
    ): ClassicRssFeedResponse

    @GET("{country}/rss/{chart}/limit={limit}/genre={genreId}/json")
    suspend fun getRssChartByGenre(
        @Path("country") country: String,
        @Path("chart") chart: String,
        @Path("limit") limit: Int = 100,
        @Path("genreId") genreId: Long
    ): ClassicRssFeedResponse

    @GET("{country}/rss/newapplications/limit=50/json")
    suspend fun getNewApps(
        @Path("country") country: String
    ): ClassicRssFeedResponse
}
