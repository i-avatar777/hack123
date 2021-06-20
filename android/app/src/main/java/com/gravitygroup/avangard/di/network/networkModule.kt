package com.gravitygroup.avangard.di.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.gravitygroup.avangard.BuildConfig
import com.gravitygroup.avangard.core.network.errors.DefaultErrorMapper
import com.gravitygroup.avangard.core.network.errors.ErrorMapper
import com.gravitygroup.avangard.core.network.errors.NetworkErrorBus
import com.gravitygroup.avangard.core.network.interceptors.AuthInterceptor
import com.gravitygroup.avangard.core.network.interceptors.ErrorStatusInterceptor
import com.gravitygroup.avangard.core.network.interceptors.NetworkStatusInterceptor
import com.gravitygroup.avangard.core.utils.DateConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

const val READ_TIMEOUT = 10000L
const val WRITE_TIMEOUT = 10000L
const val CONNECT_TIMEOUT = 30000L
const val AUTH_SMS_PENALTY_TIME = 30000L

const val ORDER_NAME = "order"

val networkModule = module {

    single { provideMoshi() }

    single(named(ORDER_NAME)) {
        provideOkHttpClient(
            get(),
            get(),
            get(named(ORDER_NAME)),
            get(),
            androidContext()
        )
    }

    single(named(ORDER_NAME)) {
        provideRetrofit(
            get(),
            get(named(ORDER_NAME))
        )
    }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.HEADERS
            }
        }
    }

    single { NetworkStatusInterceptor(get()) }

    single(named(ORDER_NAME)) { ErrorStatusInterceptor(get()) }

    single { AuthInterceptor(get()) }

    single<ErrorMapper> { DefaultErrorMapper() }

    single { NetworkErrorBus() }
}

private fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    networkStatusInterceptor: NetworkStatusInterceptor,
    errorStatusInterceptor: ErrorStatusInterceptor,
    authInterceptor: AuthInterceptor,
    context: Context
): OkHttpClient =
    OkHttpClient.Builder().apply {
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        addInterceptor(loggingInterceptor)
        addInterceptor(networkStatusInterceptor)
        addInterceptor(errorStatusInterceptor)
        addInterceptor(authInterceptor)
        addInterceptor(ChuckerInterceptor.Builder(context).build())
    }
        .build()

private fun provideRetrofit(
    moshi: Moshi,
    client: OkHttpClient
): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.BASE_URL)
        .build()
}

private fun provideMoshi(): Moshi =
    Moshi.Builder()
        .add(DateConverter())
        .add(KotlinJsonAdapterFactory())
        .build()

