package com.gravitygroup.avangard.di

import com.gravitygroup.avangard.utils.NetworkMonitor
import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.dispatchers.DispatchersProviderImpl
import com.gravitygroup.avangard.core.local.PreferenceManager
import com.gravitygroup.avangard.core.local.SecurityPrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedModule = module {

    single { PreferenceManager(androidContext(), get()) }

    single { SecurityPrefManager(androidContext()) }

    single { NetworkMonitor(androidContext()) }

    single<DispatchersProvider> { DispatchersProviderImpl() }
}