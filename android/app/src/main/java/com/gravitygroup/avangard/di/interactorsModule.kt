package com.gravitygroup.avangard.di

import com.gravitygroup.avangard.interactors.auth.AuthInteractor
import com.gravitygroup.avangard.interactors.auth.repository.AuthRepository
import com.gravitygroup.avangard.interactors.auth.repository.AuthRepositoryImpl
import com.gravitygroup.avangard.interactors.file.FileManagerInteractor
import com.gravitygroup.avangard.interactors.file.FileManagerInteractorImpl
import com.gravitygroup.avangard.interactors.orders.OrdersInteractor
import com.gravitygroup.avangard.interactors.orders.repository.DirectoriesRepository
import com.gravitygroup.avangard.interactors.orders.repository.LocalDataSource
import com.gravitygroup.avangard.interactors.orders.repository.LocalDataSourceImpl
import com.gravitygroup.avangard.interactors.orders.repository.LocalOrdersRepository
import com.gravitygroup.avangard.interactors.orders.repository.MapGeoCodeRepository
import com.gravitygroup.avangard.interactors.orders.repository.RemoteOrdersRepository
import com.gravitygroup.avangard.interactors.orders.repository.RemoteOrdersRepositoryImpl
import com.gravitygroup.avangard.interactors.orders.repository.ResourceProvider
import com.gravitygroup.avangard.interactors.settings.SettingsInteractor
import com.gravitygroup.avangard.interactors.settings.repository.SettingsRepository
import com.gravitygroup.avangard.interactors.yacht.YachtFilterInteractor
import com.gravitygroup.avangard.interactors.yacht.YachtNetworkRepo
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DIR_CACHE = "directoryCache"
private const val ORDER_CACHE = "orderCache"

val interactorsModule = module {

    // auth
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get()) }
    single { AuthInteractor(get(), get(), get()) }

    // orders
    single { ResourceProvider(get()) }
    single<RemoteOrdersRepository> { RemoteOrdersRepositoryImpl(get(), get(), get(), get()) }
    single<LocalDataSource<String, DirectoryUIModel>> (named(DIR_CACHE)) { LocalDataSourceImpl() }
    single { LocalOrdersRepository() }
    single { DirectoriesRepository(get(), get(named(DIR_CACHE))) }
    single { MapGeoCodeRepository(get()) }

    single<LocalDataSource<String, List<OrderShortUIModel>>> (named(ORDER_CACHE)) { LocalDataSourceImpl() }
    single { OrdersInteractor(get(), get(), get(), get(), get(), get(named(ORDER_CACHE))) }

    single<FileManagerInteractor> { FileManagerInteractorImpl() }

    // settings
    single { SettingsRepository(get(), get(), get(), get(), get(), get()) }
    single { SettingsInteractor(get()) }

    single { YachtNetworkRepo(get()) }
    single { YachtFilterInteractor(get()) }
}
