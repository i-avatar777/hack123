package com.gravitygroup.avangard.di

import com.gravitygroup.avangard.di.network.mapNetworkModule
import com.gravitygroup.avangard.di.network.networkModule
import com.gravitygroup.avangard.di.network.yachtNetworkModule
import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    sharedModule,
    presentationModule,
    interactorsModule,
    apiModule,
    networkModule,
    mapNetworkModule,
    yachtNetworkModule
)