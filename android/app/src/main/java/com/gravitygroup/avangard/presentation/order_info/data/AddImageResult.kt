package com.gravitygroup.avangard.presentation.order_info.data

import android.net.Uri
import java.io.Serializable

sealed class AddImageResult : Serializable {

    object None : AddImageResult(), Serializable
    data class FileSelected(val data: List<Pair<String, Uri>>) : AddImageResult(), Serializable
}
