package com.gravitygroup.avangard.presentation.order_info.data

data class EditingOrderUIModel(
    val editingOrderType: EditingOrderType,
    val orderFullUIModel: OrderFullUIModel,
    val typeTech : TypeTechUIModel = TypeTechUIModel.empty(),
    val nameTech: NameTechUIModel = NameTechUIModel.empty(),
)