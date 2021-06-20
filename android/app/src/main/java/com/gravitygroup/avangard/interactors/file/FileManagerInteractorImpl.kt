package com.gravitygroup.avangard.interactors.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.io.*

class FileManagerInteractorImpl : FileManagerInteractor {

    override fun encodeToToBase64(context: Context, uri: Uri): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64Output ->
                context.contentResolver.openInputStream(uri).use { inputStream ->
                    inputStream?.let {
                        val copyFile = copyToTempFile(it)
                        val normalizedBitmap = normalize(copyFile.absolutePath)
                        if (normalizedBitmap != null) {
                            val compressedBytes = compressBitmapToByteArray(normalizedBitmap)
                            base64Output.write(compressedBytes)
                        }
                        copyFile.delete()
                    }
                }
            }
            return@use outputStream.toString()
        }
    }

    override fun encodeListToToBase64(context: Context, uris: List<Uri>): List<String> {
        return uris.map { uri ->
            encodeToToBase64(context, uri)
        }
    }

    private fun copyToTempFile(inputStream: InputStream): File {
        val copy = File.createTempFile("temp", ".jpg")
        FileOutputStream(copy).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        Timber.d("copyToTempFile size: ${copy.length()}")
        return copy
    }

    private fun normalize(path: String, scale: Boolean = true): Bitmap? {
        val ei: ExifInterface
        try {
            ei = ExifInterface(path)
            Timber.d("normalize exif ok")
        } catch (e: IOException) {
            return loadBitmap(Uri.parse(path), scale)
        }

        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
        )
        Timber.d("normalize orientation $orientation")

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(path, 90f, scale)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(path, 180f, scale)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(path, 270f, scale)
            else -> loadBitmap(Uri.parse(path), scale)
        }
    }

    private fun rotateImage(path: String, angle: Float, scale: Boolean = true): Bitmap {
        val options = BitmapFactory.Options()
        with(options) {
            if (scale) {
                inSampleSize = 2
            }
            inMutable = true
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        val source = BitmapFactory.decodeFile(path, options)
        val matrix = Matrix()
        matrix.postRotate(angle)
        val output = Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
        source.recycle()
        Timber.d("rotateImage result size: ${output.width} x ${output.height}")
        return output
    }

    private fun loadBitmap(uri: Uri, scale: Boolean = true): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        if (scale) {
            options.inSampleSize = 2
        }
        val bitmap = try {
            BitmapFactory.decodeFile(uri.encodedPath, options)
        } catch (e: Exception) {
            null
        }
        Timber.d("loadBitmap bitmap: $bitmap")
        return bitmap
    }

    private fun compressBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val result = ByteArrayOutputStream()
        var quality = COMPRESS_QUALITY
        bitmap.compress(JPEG, quality, result)
        if (result.toByteArray().count() <= MAX_IMAGE_SIZE) {
            result.toByteArray()
        }
        while (result.toByteArray().count() > MAX_IMAGE_SIZE) {
            result.reset()
            quality -= 5
            if (quality < 0) {
                break
            }
            bitmap.compress(JPEG, quality, result)
        }
        Timber.d("compressBitmapToByteArray result size: ${result.size()}")
        return result.toByteArray()
    }

    companion object {

        private const val COMPRESS_QUALITY = 80
        private const val MAX_IMAGE_SIZE = 1024 * 700
    }
}
