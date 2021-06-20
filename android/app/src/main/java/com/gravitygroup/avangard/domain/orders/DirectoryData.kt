package com.gravitygroup.avangard.domain.orders

import android.os.Parcelable
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import com.gravitygroup.avangard.presentation.order_info.data.NameTechUIModel
import com.gravitygroup.avangard.presentation.order_info.data.TypeTechUIModel
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectoryData(
    @Json(name = "typetech")
    val typeTech: List<TypeTechItem>,
    @Json(name = "nametech")
    val nameTech: List<NameTechItem>
) : Parcelable

@Parcelize
data class NameTechItem(
    @Json(name = "name_specif")
    val nameSpec: String,
    @Json(name = "id_name")
    val idName: String
) : Parcelable

@Parcelize
data class TypeTechItem(
    @Json(name = "tech_specif")
    val typeSpec: String,
    @Json(name = "id_tech")
    val idType: String
) : Parcelable {

    companion object {

        fun emptyDirectoryData(): DirectoryData = DirectoryData(
            emptyList(),
            emptyList()
        )

        fun DirectoryData.toUIModel(): DirectoryUIModel =
            DirectoryUIModel(
                nameTech =  this.nameTech.map {
                    NameTechUIModel(it.nameSpec, it.idName)
                },
                typeTech = this.typeTech.map {
                    TypeTechUIModel(it.typeSpec, it.idType)
                }
            )
    }
}
