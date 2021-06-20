package com.gravitygroup.avangard.presentation.order_info.data

import android.os.Parcelable
import com.gravitygroup.avangard.domain.orders.NameTechItem
import com.gravitygroup.avangard.domain.orders.TypeTechItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectoryUIModel(
    val typeTech: List<TypeTechUIModel>,
    val nameTech: List<NameTechUIModel>
) : Parcelable {

    companion object {

        private const val STRING_EMPTY = ""


        fun emptyUIModel(): DirectoryUIModel = DirectoryUIModel(
            emptyList(),
            emptyList()
        )

        fun List<NameTechUIModel>.findName(idName: String): String =
            this.find { it.idName == idName }?.nameSpec ?: STRING_EMPTY

        fun List<NameTechItem>.findNameItem(idName: String): String =
            this.find{ it.idName == idName }?.nameSpec ?: STRING_EMPTY

        fun List<TypeTechUIModel>.findType(idTech: String): String =
            this.find { it.idTech == idTech }?.techSpec ?: STRING_EMPTY

        fun List<TypeTechItem>.findTypeItem(idTech: String): String =
            this.find{ it.idType == idTech }?.typeSpec ?: STRING_EMPTY
    }
}

@Parcelize
data class NameTechUIModel(
    val nameSpec: String,
    val idName: String
) : Parcelable {

    companion object {

        fun empty(): NameTechUIModel =
            NameTechUIModel(
                TypeTechUIModel.STRING_DEFAULT,
                TypeTechUIModel.STRING_DEFAULT
            )

        fun NameTechUIModel.isEmpty(): Boolean = this.nameSpec == TypeTechUIModel.STRING_DEFAULT &&
                this.idName == TypeTechUIModel.STRING_DEFAULT
    }
}

@Parcelize
data class TypeTechUIModel(
    val techSpec: String,
    val idTech: String
) : Parcelable {

    companion object {

        const val INT_DEFAULT = 0

        const val STRING_DEFAULT = ""

        fun empty(): TypeTechUIModel =
            TypeTechUIModel(
                STRING_DEFAULT,
                STRING_DEFAULT
            )

        fun TypeTechUIModel.isEmpty(): Boolean = this.techSpec == STRING_DEFAULT &&
                this.idTech == STRING_DEFAULT
    }
}
