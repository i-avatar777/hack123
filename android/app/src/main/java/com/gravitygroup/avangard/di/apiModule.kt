package com.gravitygroup.avangard.di

import com.gravitygroup.avangard.di.network.MAP_NAME
import com.gravitygroup.avangard.di.network.ORDER_NAME
import com.gravitygroup.avangard.di.network.YACHT_NAME
import com.gravitygroup.avangard.interactors.auth.api.AuthApi
import com.gravitygroup.avangard.interactors.orders.MapApi
import com.gravitygroup.avangard.interactors.orders.OrdersApi
import com.gravitygroup.avangard.interactors.settings.SettingsApi
import com.gravitygroup.avangard.interactors.yacht.YachtApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {

    single { get<Retrofit>(named(ORDER_NAME)).create(AuthApi::class.java) }

    single { get<Retrofit>(named(ORDER_NAME)).create(OrdersApi::class.java) }

    single { get<Retrofit>(named(ORDER_NAME)).create(SettingsApi::class.java) }

    single { get<Retrofit>(named(MAP_NAME)).create(MapApi::class.java) }

    single { get<Retrofit>(named(YACHT_NAME)).create(YachtApi::class.java)}
}
