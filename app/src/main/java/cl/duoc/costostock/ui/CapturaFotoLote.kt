package cl.duoc.costostock.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


@Composable
fun CapturaFotoLote(
    foto: Bitmap?,
    error: String?,
    onFotoCapturada: (Bitmap) -> Unit,
    modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mensajePermiso by remember { mutableStateOf<String?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    )
    { bitmap: Bitmap? ->
        if (bitmap != null) {
            mensajePermiso = null
            onFotoCapturada(bitmap)
        }
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    )
    { concedido ->
        if (concedido) {
            camaraLauncher.launch(null)
        } else {
            mensajePermiso = "Necesitamos el permiso de cámara para fotografiar el lote"
        }
    }

    fun abrirCamara() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) camaraLauncher.launch(null)
        else permisoLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { abrirCamara() }, modifier = Modifier.fillMaxWidth()) {
            Text(if (foto == null) "Tomar foto del lote" else "Volver a tomar foto")
        }

        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = "Previsualización de la foto actual del lote",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        mensajePermiso?.let { MensajeError(it) }
        error?.let { MensajeError(it) }
    }
}

@Composable
internal fun MensajeError(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

