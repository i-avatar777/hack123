package com.gravitygroup.avangard.presentation.order_edit.data

import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel

data class TechFieldsOrderUIModel(
    val directories: DirectoryUIModel,
    val nameTechId: String,
    val typeTechId: String
) {

    companion object {

        private const val INT_DEFAULT = 0
        private const val STRING_DEFAULT = ""

        fun empty(): TechFieldsOrderUIModel =
            TechFieldsOrderUIModel(
                DirectoryUIModel.emptyUIModel(),
                STRING_DEFAULT,
                STRING_DEFAULT
            )
    }
}