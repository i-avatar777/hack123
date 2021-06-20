package com.gravitygroup.avangard.presentation.order_info

import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.OrderFullUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel

data class CachedOrderUIModel(
    val orderFullUIModel: OrderFullUIModel,
    val typeTech : TypeTechUIModel = TypeTechUIModel.empty(),
    val nameTech: NameTechUIModel = NameTechUIModel.empty(),
)