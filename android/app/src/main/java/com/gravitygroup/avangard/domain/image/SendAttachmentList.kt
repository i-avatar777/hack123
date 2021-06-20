package com.gravitygroup.avangard.domain.image

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SendAttachmentList(
    @Json(name = "data")
    val array: List<SendAttachmentData>
) : Parcelable
