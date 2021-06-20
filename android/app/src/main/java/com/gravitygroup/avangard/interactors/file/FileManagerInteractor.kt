package com.gravitygroup.avangard.interactors.file

import android.content.Context
import android.net.Uri

interface FileManagerInteractor {

    fun encodeToToBase64(context: Context, uri: Uri): String

    fun encodeListToToBase64(context: Context, uris: List<Uri>): List<String>

}
