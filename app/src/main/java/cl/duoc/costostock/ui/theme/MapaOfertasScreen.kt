package cl.duoc.costostock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.model.RadioBusquedaKm
import cl.duoc.costostock.viewmodel.CatalogoViewModel

@Composable
fun MapaOfertasScreen(viewModel: CatalogoViewModel, navController: NavController) {

    val lotes by viewModel.lotesVisibles.collectAsState()
    val radioSeleccionado by viewModel.radioSeleccionado.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- SECCIÓN NUEVA: Filtro de distancia ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Radio de búsqueda:",
                style = MaterialTheme.typography.titleSmall
            )

            Box {
                OutlinedButton(onClick = { menuExpandido = true }) {
                    // Muestra el valor actual formateado (Ej: "RADIO 15 KM")
                    Text(radioSeleccionado.name.replace("_", " "))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    // Itera sobre las opciones del Enum
                    RadioBusquedaKm.entries.forEach { radio ->
                        DropdownMenuItem(
                            text = { Text(radio.name.replace("_", " ")) },
                            onClick = {
                                viewModel.cambiarRadioBusqueda(radio)
                                menuExpandido = false
                            }
                        )
                    }
                }
            }
        }

        // --- SECCIÓN 1: Contenedor del Mapa (Marcador visual) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Icono de Mapa",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Simulación de Mapa Interactivo",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Mostrando ${lotes.size} puntos de retiro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // --- SECCIÓN 2: Lista inferior de ubicaciones ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            items(lotes) { lote ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { navController.navigate("detalle_oferta/${lote.idLote}") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Punto",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = lote.nombreComercio, style = MaterialTheme.typography.titleSmall)
                            Text(text = lote.direccionRetiro, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Lat: ${lote.latitudRetiro} | Lon: ${lote.longitudRetiro}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}