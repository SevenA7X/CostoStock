package cl.duoc.costostock.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.costostock.viewmodel.CarritoCheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoCheckoutScreen(
    viewModel: CarritoCheckoutViewModel = viewModel(),
    onConfirmarReserva: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val contexto = LocalContext.current

    val minutos = state.tiempoRestanteSegundos / 60
    val segundos = state.tiempoRestanteSegundos % 60
    val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reserva y Checkout", fontWeight = FontWeight.Bold) }
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
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.reservaExpirada) "¡Tiempo de reserva expirado!" else "Tiempo de reserva restante:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = tiempoFormateado,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.reservaExpirada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text("Datos del Comprador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                isError = state.errorNombre != null,
                supportingText = { state.errorNombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.errorCorreo != null,
                supportingText = { state.errorCorreo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono de contacto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = state.errorTelefono != null,
                supportingText = { state.errorTelefono?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Toast.makeText(contexto, "Reserva confirmada con éxito", Toast.LENGTH_SHORT).show()
                    onConfirmarReserva()
                },
                enabled = state.esValido && !state.reservaExpirada,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }
        }
    }
}