package com.gravitygroup.avangard.domain.image

import android.net.Uri
import android.os.Parcelable
import com.gravitygroup.avangard.core.recycler.RecyclerViewItem
import com.gravitygroup.avangard.presentation.photo.PhotoSpecs
import kotlinx.android.parcel.Parcelize

@Parcelize
class ImageUIModel(
    val id: Int,
    val pathEncoded: String = "",
    val uri: Uri = Uri.EMPTY
) : RecyclerViewItem, Parcelable {

    fun toPhotoSpecs(title: String): PhotoSpecs =
        PhotoSpecs(title, pathEncoded, uri)

    override fun getId(): Int = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageUIModel

        if (id != other.id) return false
        if (uri != other.uri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uri.hashCode()
        return result
    }


}
