package com.gravitygroup.avangard.di.network

import com.gravitygroup.avangard.core.network.interceptors.MapErrorInterceptor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

const val YACHT_NAME = "yacht_api"

val yachtNetworkModule = module {

    single(named(YACHT_NAME)) {
        provideRetrofit(
            get(),
            get(named(YACHT_NAME))
        )
    }

    single(named(YACHT_NAME)) {
        provideOkHttpClient(
            get(),
            get(named(YACHT_NAME))
        )
    }

    single(named(YACHT_NAME)) { MapErrorInterceptor(get()) }
}

private fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    mapErrorInterceptor: MapErrorInterceptor,
): OkHttpClient =
    OkHttpClient.Builder().apply {
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        addInterceptor(loggingInterceptor)
        addInterceptor(mapErrorInterceptor)
    }
        .build()

private fun provideRetrofit(
    moshi: Moshi,
    client: OkHttpClient
): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("http://yaht-sharing.qualitylive.su/api/")
        .build()
}