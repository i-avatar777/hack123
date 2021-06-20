package com.gravitygroup.avangard.core.ext.glide

fun LoadImageRequest.fileImage(path: String): LoadImageRequest =
        apply {
            imageSource = ImageSource.FileImage(path)
        }