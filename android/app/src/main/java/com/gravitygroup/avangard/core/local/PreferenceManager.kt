package com.gravitygroup.avangard.core.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Secure
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.gravitygroup.avangard.core.delegates.PrefDelegate
import com.gravitygroup.avangard.core.delegates.PrefLiveObjDelegate
import com.gravitygroup.avangard.core.delegates.PrefObjDelegate
import com.gravitygroup.avangard.presentation.filter.data.FilterTypeUIModel
import com.gravitygroup.avangard.presentation.filter.data.OrdersFilterData
import com.squareup.moshi.Moshi
import java.util.UUID

class PreferenceManager(
    context: Context,
    moshi: Moshi
) : PrefManager {

    @SuppressLint("HardwareIds")
    private val androidId = Secure.getString(
        context.contentResolver,
        Secure.ANDROID_ID
    )

    var fio by PrefDelegate(PrefManager.STRING_DEFAULT)

    var deviceToken by PrefDelegate(androidId ?: generateDeviceToken())
    var pushToken by PrefDelegate(PrefManager.STRING_DEFAULT)
    var lang by PrefDelegate("RU")
    var authSmsTimeout by PrefDelegate(LONG_DEFAULT)

    var timeNotification by PrefDelegate(LONG_DEFAULT)

    override val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    var ordersFilter: OrdersFilterData? by PrefObjDelegate(moshi.adapter(OrdersFilterData::class.java))
    val ordersFilterLive: LiveData<OrdersFilterData?> by lazy {
        val liveData by PrefLiveObjDelegate<OrdersFilterData?>(
            "ordersFilter", moshi.adapter(OrdersFilterData::class.java), preferences
        )
        liveData
    }

    private fun generateDeviceToken() = UUID.randomUUID().toString().substring(0, 32)

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun resetFilterItem(type: FilterTypeUIModel) {
        ordersFilter = when (type) {
            is FilterTypeUIModel.Date -> ordersFilter?.copy(dateStart = null, dateEnd = null)
            is FilterTypeUIModel.Type -> {
                val types = ordersFilter?.typesTech?.filter { it != type.title }
                val typesTech = if (types?.isNotEmpty() == true) {
                    types
                } else {
                    null
                }
                ordersFilter?.copy(typesTech = typesTech)
            }
            is FilterTypeUIModel.Name -> {
                val names = ordersFilter?.namesTech?.filter { it != type.title }
                val namesTech = if (names?.isNotEmpty() == true) {
                    names
                } else {
                    null
                }
                ordersFilter?.copy(namesTech = namesTech)
            }
            FilterTypeUIModel.Number -> ordersFilter?.copy(orderNumber = null)
            FilterTypeUIModel.CustomerPhone -> ordersFilter?.copy(customerPhone = null)
            FilterTypeUIModel.None -> ordersFilter
        }
    }

    companion object {

        private const val LONG_DEFAULT = 0L
    }
}
