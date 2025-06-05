package com.github.EtkinlikHaritasi.EtkinlikHaritasi.utils
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
object FileUtils {
    fun saveBitmapAsPng(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QRCode")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return uri?.let {
            val outputStream = contentResolver.openOutputStream(it)
            val success = outputStream?.let { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            } ?: false
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(it, contentValues, null, null)
            success
        } ?: false
    }
}