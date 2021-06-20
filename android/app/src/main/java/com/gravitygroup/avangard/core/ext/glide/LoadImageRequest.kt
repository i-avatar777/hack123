package com.gravitygroup.avangard.core.ext.glide

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

const val NO_RESOURCE = -1

class LoadImageRequest {

    @DrawableRes
    var placeholder: Int = NO_RESOURCE

    @DrawableRes
    var errorImage: Int = NO_RESOURCE

    var imageSource: ImageSource = ImageSource.None
    var roundedCorners: RoundedCornersType = RoundedCornersType.None
    var resizeType: ResizeType = ResizeType.Auto
    var cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC
    val transformations: MutableList<Transformation<Bitmap>> = mutableListOf()

    fun addTransformation(transformation: Transformation<Bitmap>) {
        transformations.add(transformation)
    }
}

sealed class RoundedCornersType {

    object None : RoundedCornersType()
    object Circle : RoundedCornersType()
    data class EqualCorners(val radius: Int) : RoundedCornersType()
}

sealed class ResizeType {

    object None : ResizeType()
    object Auto : ResizeType()
    data class Resize(val width: Int, val height: Int) : ResizeType()
}

sealed class ImageSource {

    data class RemoteImage(val url: String) : ImageSource()

    data class ResourceImage(@DrawableRes val resourceId: Int) : ImageSource()

    data class FileImage(val filePath: String) : ImageSource()

    object None : ImageSource()
}
