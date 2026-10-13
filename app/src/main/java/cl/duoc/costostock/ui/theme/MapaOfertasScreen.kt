package cl.duoc.costostock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.costostock.viewmodel.CatalogoViewModel

// Esta pantalla representa la vista de mapa donde se ubican físicamente los lotes.
@Composable
fun MapaOfertasScreen(viewModel: CatalogoViewModel) {

    // Obtenemos la lista reactiva de lotes directamente desde el ViewModel.
    val lotes by viewModel.lotesVisibles.collectAsState()

    // Column principal que ocupa toda la pantalla.
    Column(modifier = Modifier.fillMaxSize()) {

        // --- SECCIÓN 1: Contenedor del Mapa (Marcador visual) ---
        // Al no tener aún la API Key de Google Maps, usamos una caja (Box) que simula el área del mapa.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // weight(1f) hace que esta caja ocupe la mitad superior de la pantalla.
                .background(Color(0xFFE0E0E0)), // Color gris claro para simular el fondo de un mapa.
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
        // Esta lista muestra las coordenadas y direcciones de los productos visibles.
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa la otra mitad inferior de la pantalla.
                .padding(16.dp)
        ) {
            items(lotes) { lote ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono pequeño al lado de cada dirección
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Punto",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        // Textos con la dirección y el nombre del comercio
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