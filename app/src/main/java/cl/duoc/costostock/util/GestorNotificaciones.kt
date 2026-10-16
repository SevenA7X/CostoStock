package cl.duoc.costostock.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import cl.duoc.costostock.model.AlertaNotificacion

class GestorNotificaciones(private val context: Context) {
    private val channelId = "costostock_vouchers_channel"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        crearCanalNotificacion()
    }
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vouchers y Compras",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de confirmación de compra y vouchers generados"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun mostrarNotificacion(alerta: AlertaNotificacion) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(alerta.titulo) // Usamos el título del contrato
            .setContentText(alerta.mensaje) // Usamos el mensaje del contrato
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alerta.idNotificacion.toInt(), notification)
    }
}