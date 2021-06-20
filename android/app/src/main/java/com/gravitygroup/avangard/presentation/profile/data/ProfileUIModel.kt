package com.gravitygroup.avangard.presentation.profile.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileUIModel(
    val login: String?,
    val fio: String?,
    val image: String?
) : Parcelable {

    companion object {

        fun empty(): ProfileUIModel =
            ProfileUIModel(
                ProfileUIModel.STRING_DEFAULT,
                ProfileUIModel.STRING_DEFAULT,
                ProfileUIModel.STRING_DEFAULT,
            )

        const val STRING_DEFAULT = ""

    }
}
