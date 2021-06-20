package com.gravitygroup.avangard.di

import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.presentation.RootVm
import com.gravitygroup.avangard.presentation.auth.AuthVm
import com.gravitygroup.avangard.presentation.auth.SmsVerificationVm
import com.gravitygroup.avangard.presentation.catalog.CatalogViewModel
import com.gravitygroup.avangard.presentation.dialogs.exit.ConfirmDialogVm
import com.gravitygroup.avangard.presentation.dialogs.order.OrderNotificationVm
import com.gravitygroup.avangard.presentation.filter.FilterCatalogVm
import com.gravitygroup.avangard.presentation.filter.FilterViewModel
import com.gravitygroup.avangard.presentation.map.MapOrdersVm
import com.gravitygroup.avangard.presentation.master_orders.MasterOrdersViewModel
import com.gravitygroup.avangard.presentation.menu.about.AboutAppVm
import com.gravitygroup.avangard.presentation.menu.settings.SettingsVm
import com.gravitygroup.avangard.presentation.order_edit.OrderEditViewModel
import com.gravitygroup.avangard.presentation.order_info.OrderInfoVm
import com.gravitygroup.avangard.presentation.orders.OrdersVm
import com.gravitygroup.avangard.presentation.orders_history.OrdersHistoryVm
import com.gravitygroup.avangard.presentation.photo.PhotoVm
import com.gravitygroup.avangard.presentation.profile.ProfileVm
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    factory { SavedStateHandle() }

    viewModel { RootVm(get(), get(), get(), get()) }

    // Auth
    viewModel { AuthVm(get(), get(), get(), get()) }
    viewModel { SmsVerificationVm(get(), get(), get(), get()) }

    // Orders
    viewModel { OrdersVm(get(), get(), get()) }

    // Map
    viewModel { MapOrdersVm(get(), get(), get()) }

    // Profile
    viewModel { ProfileVm(get(), get(), get(), get()) }

    // Order info
    viewModel { OrderInfoVm(get(), get(), get(), get()) }

    // Photo
    viewModel { PhotoVm(get(), get(), get()) }

    // Order Edit
    viewModel { OrderEditViewModel(get(), get(), get()) }

    // Catalog
    viewModel { CatalogViewModel(get(), get(), get(), get()) }

    // Filter
    viewModel { FilterViewModel(get(), get(), get(), get()) }
    viewModel { FilterCatalogVm(get(), get(), get(), get()) }

    // Master orders
    viewModel { MasterOrdersViewModel(get(), get(), get()) }

    // History orders
    viewModel { OrdersHistoryVm(get(), get(), get(), get()) }

    // Confirm Dialog
    viewModel { ConfirmDialogVm(get(), get(), get()) }

    // Order Notification Dialog
    viewModel { OrderNotificationVm(get(), get(), get()) }

    // Menu
    viewModel { AboutAppVm(get()) }
    viewModel { SettingsVm(get(), get(), get()) }

    viewModel { YachtFilterVm(get(), get(), get())}
}
