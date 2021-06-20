package com.gravitygroup.avangard.interactors.yacht

import com.gravitygroup.avangard.domain.yacht.ListYachtResItem
import retrofit2.http.GET
import retrofit2.http.Query

interface YachtApi {

    @GET("search")
    suspend fun requestList(
        @Query("place_count")
        placeCount: Int,
        @Query("type_rent")
        typeRent: Int,
        @Query("price_from")
        priceFrom: Int,
        @Query("price_to")
        priceTo: Int
    ): List<ListYachtResItem>
}