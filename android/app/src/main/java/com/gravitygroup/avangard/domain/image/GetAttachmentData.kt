package com.gravitygroup.avangard.domain.image

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetAttachmentData(

	@Json(name="link_foto")
	val linkPhoto: String,

	@Json(name="id_foto")
	val idPhoto: Int
) : Parcelable {

	companion object {

		fun GetAttachmentData.toUIModel(): ImageUIModel =
			ImageUIModel(this.idPhoto, this.linkPhoto)
	}
}
