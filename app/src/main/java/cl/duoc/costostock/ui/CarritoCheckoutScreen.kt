package cl.duoc.costostock.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.costostock.data.room.CostoStockDataBase
import cl.duoc.costostock.data.room.VoucherEntity
import cl.duoc.costostock.model.AlertaNotificacion
import cl.duoc.costostock.model.TipoNotificacionPush
import cl.duoc.costostock.util.GestorNotificaciones
import cl.duoc.costostock.viewmodel.CarritoCheckoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoCheckoutScreen(
    viewModel: CarritoCheckoutViewModel = viewModel(),
    navController: NavController,
    onPagoExitoso: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //Instancio el gestor de notificaciones aqui arriba
    val gestorNotificaciones = remember { GestorNotificaciones(contexto) }

    // Instancia de la base de datos Room y su DAO
    val db = remember { CostoStockDataBase.getDatabase(contexto) }
    val voucherDao = remember { db.voucherDao() }

    var procesandoPago by remember { mutableStateOf(false) }
    var pagoCompletado by remember { mutableStateOf(false) }

    val subtotal = state.subtotalProductos
    val comisionServicio = (subtotal * 0.05).toInt() // 5% transparente
    val totalPagar = subtotal + comisionServicio

    val minutos = state.tiempoRestanteSegundos / 60
    val segundos = state.tiempoRestanteSegundos % 60
    val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout - Reserva y Pago", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.reservaExpirada) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.reservaExpirada) "¡Tiempo de reserva agotado!" else "Tiempo de reserva bloqueado:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = tiempoFormateado,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.reservaExpirada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Resumen de Compra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reserva ID")
                        Text(state.idReserva, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal Productos")
                        Text("$${subtotal}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Comisión por Servicio (5%)", color = MaterialTheme.colorScheme.secondary)
                        Text("$${comisionServicio}", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL A PAGAR", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$${totalPagar}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text("Datos del Comprador Ligero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.compradorNombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre Completo") },
                isError = state.errorNombre != null,
                supportingText = {
                    state.errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorNombre == null && state.compradorNombre.isNotEmpty()) "✓" else "👤"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            OutlinedTextField(
                value = state.compradorCorreo,
                onValueChange = { viewModel.onCorreoChanged(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.errorCorreo != null,
                supportingText = {
                    state.errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorCorreo == null && state.compradorCorreo.isNotEmpty()) "✓" else "✉"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            OutlinedTextField(
                value = state.compradorTelefono,
                onValueChange = { viewModel.onTelefonoChanged(it) },
                label = { Text("Teléfono de Contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = state.errorTelefono != null,
                supportingText = {
                    state.errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorTelefono == null && state.compradorTelefono.isNotEmpty()) "✓" else "📞"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            Spacer(modifier = Modifier.height(8.dp))

            val colorBotonAnimado by animateColorAsState(
                targetValue = when {
                    pagoCompletado -> Color(0xFF2E7D32)
                    procesandoPago -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                },
                animationSpec = tween(durationMillis = 500),
                label = "colorBotonPago"
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        procesandoPago = true
                        delay(2000L)
                        procesandoPago = false
                        pagoCompletado = true

                        // Creación e inserción del voucher en SQLite
                        val nuevoVoucher = VoucherEntity(
                            codigoOrdenQR = "QR-${System.currentTimeMillis()}",
                            nombreProducto = "Pack de Productos CostoStock",
                            nombreComercio = "Comercio Asociado",
                            direccionExactaRetiro = "Dirección del local",
                            horarioValidoRetiro = "Horario de retiro",
                            estadoVoucher = "VÁLIDO",
                            kgCo2Evitado = 0.5,
                            fechaEmisionMillis = System.currentTimeMillis()
                        )
                        voucherDao.insertarVoucher(nuevoVoucher)

                        Toast.makeText(contexto, "¡Reserva y Pago exitoso!", Toast.LENGTH_LONG).show()

                        // Armamos el objeto oficial del contrato
                        val alertaOficial = AlertaNotificacion(
                            idNotificacion = System.currentTimeMillis(),
                            idLoteAsociado = 0L, // 0L por defecto o el ID del lote real si lo tienes en el state
                            titulo = "¡Compra Exitosa en CostoStock!",
                            mensaje = "Tu voucher está listo y ayudaste a reducir emisiones de CO2. 🌱",
                            tipo = TipoNotificacionPush.OFERTA_CERCANA_GPS, // Usamos un enum oficial
                            timestampEnvioMillis = System.currentTimeMillis()
                        )

                        // Disparamos la notificación pasando el objeto completo
                        gestorNotificaciones.mostrarNotificacion(alertaOficial)

                        delay(1000L)
                        onPagoExitoso()

                        // LÍNEAS NUEVAS: Navegación a vouchers y limpieza de historial
                        navController.navigate("vouchers") {
                            popUpTo("ofertas") { inclusive = false }
                        }
                    }
                },
                enabled = state.esFormularioValido && !state.reservaExpirada && !procesandoPago && !pagoCompletado,
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonAnimado),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (procesandoPago) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text("Procesando pago con pasarela...", color = Color.White)
                        }
                    } else if (pagoCompletado) {
                        Text("¡Pago Exitoso! ✓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text("PAGAR ($${totalPagar})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.reservaExpirada,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "La reserva ha expirado. Favor volver al catálogo.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}