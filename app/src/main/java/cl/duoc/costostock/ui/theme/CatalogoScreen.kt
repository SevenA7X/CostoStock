package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.costostock.ui.theme.FondoTarjeta
import cl.duoc.costostock.viewmodel.CatalogoViewModel

@Composable
fun CatalogoScreen(viewModel: CatalogoViewModel) {
    val lotes by viewModel.lotesVisibles.collectAsState()
    var permisosConcedidos by remember { mutableStateOf(false) }

    // Estado para controlar qué vista mostrar (Lista o Mapa)
    var mostrarMapa by remember { mutableStateOf(false) }

    ManejadorPermisosGPS(
        onPermisoConcedido = { permisosConcedidos = true },
        onPermisoDenegado = { permisosConcedidos = false }
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Catálogo de Ofertas", style = MaterialTheme.typography.headlineMedium)
            // Botón para alternar vistas
            Button(onClick = { mostrarMapa = !mostrarMapa }) {
                Text(text = if (mostrarMapa) "Ver Lista" else "Ver Mapa")
            }
        }

        if (!permisosConcedidos) {
            Text(
                text = "Se requiere acceso a la ubicación para calcular la distancia.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transición animada (AnimatedContent)
        AnimatedContent(
            targetState = mostrarMapa,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "TransicionListaMapa"
        ) { isMap ->
            if (isMap) {
                // Muestra la pantalla del mapa
                MapaOfertasScreen(viewModel)
            } else {
                // Muestra la lista original
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(lotes) { lote ->
                        Card(shape = RoundedCornerShape(16.dp),
                            colors = cardColors(containerColor = FondoTarjeta),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = lote.nombreProducto, style = MaterialTheme.typography.titleMedium)
                                Text(text = lote.nombreComercio, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "Precio Oferta: $${lote.precioOfertaFinal}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Stock: ${lote.stockUnidadesDisponible} unidades",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}