package cl.duoc.costostock.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun ManejadorPermisosGPS(
    onPermisoConcedido: () -> Unit,
    onPermisoDenegado: () -> Unit
) {
    val contexto = LocalContext.current

    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val ubicacionPrecisaConcedida = permisos[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val ubicacionAproximadaConcedida = permisos[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (ubicacionPrecisaConcedida || ubicacionAproximadaConcedida) {
            onPermisoConcedido()
        } else {
            onPermisoDenegado()
        }
    }

    LaunchedEffect(Unit) {
        val permisoPreciso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoPreciso || permisoAproximado) {
            onPermisoConcedido()
        } else {
            launcherPermisos.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}