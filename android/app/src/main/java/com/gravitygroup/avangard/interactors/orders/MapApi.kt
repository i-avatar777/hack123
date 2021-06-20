package com.gravitygroup.avangard.interactors.orders

import com.gravitygroup.avangard.BuildConfig
import com.gravitygroup.avangard.domain.orders.GeoCodeRes
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {

    @GET("1.x/?apikey=${BuildConfig.GEOCODE_MAP_KEY}&format=json")
    suspend fun addressGeocode(@Query("geocode") address: String): GeoCodeRes
}