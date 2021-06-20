package com.gravitygroup.avangard.presentation.filter.data

import android.os.Parcelable
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.orders.data.OrderShortUIModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrdersFilterData(
    val dateStart: Long? = null,
    val dateEnd: Long? = null,
    val orderNumber: String? = null,
    val customerPhone: String? = null,
    val typesTech: List<String>? = null,
    val namesTech: List<String>? = null,
) : Parcelable {

    fun match(order: OrderShortUIModel): Boolean {
        val orderDate = order.dateTimeCreate
        return (dateStart == null || dateStart <= orderDate)
                && (dateEnd == null || dateEnd >= orderDate)
                && (orderNumber == null || order.num.contains(orderNumber, true))
                && (customerPhone == null || customerPhone.equals(order.tel, true))
                && (typesTech == null || typesTech.find { it.equals(order.typeTech, true) } != null)
                && (namesTech == null || namesTech.find { it.equals(order.nameTech, true) } != null)
    }

    fun matchFull(
        order: OrderFullUIModel,
        nameTech: String,
        typeTech: String
    ): Boolean {
        val orderDate = order.dateTimeCreate
        return (dateStart == null || dateStart <= orderDate)
                && (dateEnd == null || dateEnd >= orderDate)
                && (orderNumber == null || order.num.contains(orderNumber, true))
                && (customerPhone == null || customerPhone.equals(order.tel, true))
                && (typesTech == null || typesTech.find { it.equals(typeTech, true) } != null)
                && (namesTech == null || namesTech.find { it.equals(nameTech, true) } != null)
    }

    fun getAssigned() = mutableListOf<FilterTypeUIModel>().apply {
        if (dateStart != null || dateEnd != null) add(FilterTypeUIModel.Date)
        if (orderNumber != null) add(FilterTypeUIModel.Number)
        if (customerPhone != null) add(FilterTypeUIModel.CustomerPhone)
        typesTech?.forEach { title ->
            add(FilterTypeUIModel.Type(title))
        }
        namesTech?.forEach { title ->
            add(FilterTypeUIModel.Name(title))
        }
    }
}