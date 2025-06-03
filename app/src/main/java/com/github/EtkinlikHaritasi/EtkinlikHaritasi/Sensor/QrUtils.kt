package com.github.EtkinlikHaritasi.EtkinlikHaritasi.Sensor

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

object QrUtils
{
    fun generateQrCode(content: String): Bitmap? {
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 1080, 1080)
        val bitMap = createBitmap(1080, 1080, Bitmap.Config.RGB_565)

        for (x in 0 until 1080) {
            for (y in 0 until 1080) {
                bitMap[x, y] = if (bitMatrix.get(
                        x,
                        y
                    )
                ) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        return bitMap
    }
}
