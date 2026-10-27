package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.viewmodel.CatalogoViewModel
import coil.compose.AsyncImage

@Composable
fun DetalleOfertaScreen(viewModel: CatalogoViewModel, idLoteSeleccionado: Long, navController: NavController) {

    // 1. Observamos la lista reactiva de lotes.
    val lotes by viewModel.lotesVisibles.collectAsState()

    // 2. Buscamos el lote específico que coincide con el ID seleccionado.
    val lote = lotes.find { it.idLote == idLoteSeleccionado }

    // 3. Validación de seguridad.
    if (lote == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "La oferta ya no está disponible.")
        }
        return
    }

    // 4. Estructura visual de los detalles.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- INICIO IMAGEN DESTACADA ---
        AsyncImage(
            model = lote.urlsFotografiasActuales.firstOrNull(),
            contentDescription = "Fotografía de ${lote.nombreProducto}",
            contentScale = ContentScale.Crop, // Ajuste perfecto sin deformar
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Altura fija de cabecera
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEEEEE))
        )

        Spacer(modifier = Modifier.height(16.dp))
        // --- FIN IMAGEN DESTACADA ---

        // Tarjeta principal con la cabecera de la información.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = lote.nombreProducto,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lote.nombreComercio,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = lote.descripcion, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Fila para mostrar y comparar el precio de oferta vs el normal.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Precio Oferta",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "$${lote.precioOfertaFinal}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF2E7D32) // Verde oscuro para destacar el precio final.
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Precio Normal",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "$${lote.precioNormalSinDescuento}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough // Tachado agregado
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Datos adicionales y métricas de impacto ambiental.
        Text(text = "Detalles Adicionales", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Stock disponible: ${lote.stockUnidadesDisponible} unidades")
        Text(text = "Impacto ambiental: Evitas ${lote.kgCo2EvitadoPorUnidad} kg de CO2")
        Text(text = "Horario de retiro: ${lote.horarioRetiro}")

        // 6. Validación de categoría con animación (AnimatedVisibility)
        AnimatedVisibility(visible = lote.categoria == CategoriaProducto.BEBIDAS_ALCOHOLICAS) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia de Edad",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Producto exclusivo para mayores de 18 años.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia el fondo de la pantalla.

        // 7. Botón de acción principal.
        Button(
            onClick = { navController.navigate("checkout") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Reservar Oferta", style = MaterialTheme.typography.titleMedium)
        }
    }
}