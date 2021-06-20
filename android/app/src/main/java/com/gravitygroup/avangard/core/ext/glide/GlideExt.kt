package com.gravitygroup.avangard.core.ext.glide

import android.graphics.Bitmap
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gravitygroup.avangard.core.ext.glide.ImageSource.FileImage
import java.io.File

fun ImageView.networkLoadImage(request: LoadImageRequest) = post {
    val imageSource: ImageSource = request.imageSource

    if (imageSource is ImageSource.None) {
        return@post
    }

    var options = RequestOptions()

    options = applyRoundedCorners(options, request.roundedCorners)
    options = applyResize(options, request.resizeType, this)

    if (request.placeholder != NO_RESOURCE) {
        options = options.placeholder(request.placeholder)
    }
    if (request.errorImage != NO_RESOURCE) {
        options = options.error(request.errorImage)
    }

    options = options.diskCacheStrategy(request.cacheStrategy)

    (imageSource as? FileImage)?.filePath?.also { imageSource ->
        var requestBuilder = Glide
            .with(this)
            .run {
                if (imageSource.contains("http")) {
                    load(imageSource)
                } else {
                    load(Base64.decode(imageSource, Base64.DEFAULT))
                }
            }
            .apply(options)
        requestBuilder = when {
            request.transformations.size > 1 ->
                requestBuilder.transform(MultiTransformation(request.transformations))
            request.transformations.isNotEmpty() -> requestBuilder.transform(request.transformations.first())
            else -> requestBuilder
        }
        requestBuilder
            .into(this)
    }
}

fun ImageView.loadImage(request: LoadImageRequest) = post {
    val imageSource: ImageSource = request.imageSource

    if (imageSource is ImageSource.None) {
        return@post
    }

    var options = RequestOptions()

    options = applyRoundedCorners(options, request.roundedCorners)
    options = applyResize(options, request.resizeType, this)

    if (request.placeholder != NO_RESOURCE) {
        options = options.placeholder(request.placeholder)
    }
    if (request.errorImage != NO_RESOURCE) {
        options = options.error(request.errorImage)
    }

    options = options.diskCacheStrategy(request.cacheStrategy)

    var requestBuilder =
        Glide.with(this)
            .asBitmap()
            .apply {
                applyImageSource(request.imageSource, this)
            }
            .apply(options)

    requestBuilder = when {
        request.transformations.size > 1 ->
            requestBuilder.transform(MultiTransformation(request.transformations))
        request.transformations.isNotEmpty() -> requestBuilder.transform(request.transformations.first())
        else -> requestBuilder
    }
    requestBuilder
        .into(this)
}

fun ImageView.networkLoadImage(filePath: String, builder: LoadImageRequest.() -> Unit = {}) {
    val request = LoadImageRequest()
    request.builder()
    filePath.takeIf { it.isNotEmpty() }
        ?.also {
            request.fileImage(it)
        }
    networkLoadImage(request)
}

fun ImageView.loadFile(filePath: String, builder: LoadImageRequest.() -> Unit = {}) {
    val request = LoadImageRequest()
    request.builder()
    request.fileImage(filePath)
    loadImage(request)
}

fun applyResize(
    options: RequestOptions,
    resize: ResizeType,
    source: ImageView
): RequestOptions = when (resize) {
    is ResizeType.Resize -> options.override(resize.width, resize.height)
    is ResizeType.Auto -> {
        if (source.width != 0 && source.height != 0) {
            options.override(source.width, source.height)
        } else {
            options
        }
    }
    else -> options
}

fun applyRoundedCorners(
    options: RequestOptions,
    corners: RoundedCornersType
): RequestOptions = when (corners) {
    is RoundedCornersType.Circle -> options.circleCrop()
    is RoundedCornersType.EqualCorners -> options.transform(RoundedCorners(corners.radius))
    else -> options
}

private fun applyImageSource(
    imageSource: ImageSource,
    requestBuilder: RequestBuilder<Bitmap>
) {
    when (imageSource) {
        is ImageSource.RemoteImage -> requestBuilder.load(imageSource.url)
        is ImageSource.ResourceImage -> requestBuilder.load(imageSource.resourceId)
        is FileImage -> requestBuilder.load(File(imageSource.filePath))
        else -> requestBuilder.load("")
    }
}
