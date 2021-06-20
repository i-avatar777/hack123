package com.gravitygroup.avangard.presentation.catalog.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatalogSpecs(
    val sourceOpen: CatalogSourceOpen,
    val itemType: CatalogItemType
): Parcelable
