package cl.duoc.costostock.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun ImagenQRReal(codigo: String, modifier: Modifier = Modifier) {
    val bitmap = generarBitmapQR(codigo)
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Código QR",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

private fun generarBitmapQR(texto: String): Bitmap? {
    if (texto.isEmpty()) return null
    return try {
        val size = 512 // Resolución del QR
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            texto,
            BarcodeFormat.QR_CODE,
            size,
            size
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Pinta negro (QR) o blanco (Fondo)
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}