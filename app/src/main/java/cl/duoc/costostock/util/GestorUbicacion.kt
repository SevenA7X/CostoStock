package cl.duoc.costostock.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

class GestorUbicacion(context: Context) {

    // Cliente oficial de Google Play Services para obtener la ubicación
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual(onUbicacionObtenida: (Location?) -> Unit) {
        // Nota: La validación de permisos se realizará desde la Interfaz de Usuario (UI)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { ubicacion: Location? ->
                onUbicacionObtenida(ubicacion)
            }
            .addOnFailureListener {
                onUbicacionObtenida(null)
            }
    }
}