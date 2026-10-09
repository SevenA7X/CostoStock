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
import cl.duoc.costostock.viewmodel.CarritoCheckoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoCheckoutScreen(
    viewModel: CarritoCheckoutViewModel = viewModel(),
    onPagoExitoso: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var procesandoPago by remember { mutableStateOf(false) }
    var pagoCompletado by remember { mutableStateOf(false) }

    val subtotal = state.reserva?.total ?: 0
    val comisionServicio = (subtotal * 0.05).toInt() // 5% de comisión transparente
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
                        text = if (state.reservaExpirada) "¡Tiempo de reserva agotado!" else "Tiempo restante de reserva:",
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
                    Divider()

                    state.reserva?.items?.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.nombreProducto} x${item.cantidad}")
                            Text("$${item.subtotal}")
                        }
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

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL A PAGAR", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$${totalPagar}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text("Datos del Comprador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.comprador.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre Completo") },
                isError = state.errorNombre != null,
                supportingText = {
                    state.errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorNombre == null && state.comprador.nombre.isNotEmpty()) "✓" else "👤"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            OutlinedTextField(
                value = state.comprador.correo,
                onValueChange = { viewModel.onCorreoChanged(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.errorCorreo != null,
                supportingText = {
                    state.errorCorreo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorCorreo == null && state.comprador.correo.isNotEmpty()) "✓" else "✉"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            OutlinedTextField(
                value = state.comprador.telefono,
                onValueChange = { viewModel.onTelefonoChanged(it) },
                label = { Text("Teléfono de Contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = state.errorTelefono != null,
                supportingText = {
                    state.errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val icono = if (state.errorTelefono == null && state.comprador.telefono.isNotEmpty()) "✓" else "📞"
                    Text(icono, fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !procesandoPago && !pagoCompletado
            )

            Spacer(modifier = Modifier.height(8.dp))

            val colorBotonAnimado by animateColorAsState(
                targetValue = when {
                    pagoCompletado -> Color(0xFF2E7D32) // Verde éxito
                    procesandoPago -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                },
                animationSpec = tween(durationMillis = 500)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        procesandoPago = true
                        delay(2000L) // Simulación del procesamiento del pago
                        procesandoPago = false
                        pagoCompletado = true
                        Toast.makeText(contexto, "¡Pago simulado con éxito!", Toast.LENGTH_LONG).show()
                        delay(1000L)
                        onPagoExitoso()
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
                        Text("PAGAR SIMULACIÓN ($${totalPagar})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.reservaExpirada,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "La reserva ha expirado. Por favor reinicie su carrito de compras.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}