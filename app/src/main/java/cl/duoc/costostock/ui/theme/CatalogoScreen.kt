package cl.duoc.costostock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.costostock.viewmodel.CatalogoViewModel


@Composable
fun CatalogoScreen(viewModel: CatalogoViewModel) {

    // collectAsState() "escucha" los cambios en el flujo de lotes del ViewModel.
    // Si el motor de tiempo del ViewModel cambia un estado (ej. de Activo a Caducado), la vista se redibuja automáticamente.
    val lotes by viewModel.lotesVisibles.collectAsState()

    // remember y mutableStateOf guardan el estado de los permisos en la memoria de esta pantalla.
    // Inicia en "false" y cambiará cuando el usuario responda al cuadro de diálogo del GPS.
    var permisosConcedidos by remember { mutableStateOf(false) }

    // Invocación del componente nativo de la Tarea #3.
    // No tiene interfaz visual propia, solo ejecuta la lógica de pedir los permisos y nos devuelve el resultado.
    ManejadorPermisosGPS(
        onPermisoConcedido = { permisosConcedidos = true },
        onPermisoDenegado = { permisosConcedidos = false }
    )

    // Column organiza los elementos de la interfaz de arriba hacia abajo (verticalmente).
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Título principal de la pantalla
        Text(text = "Catálogo de Ofertas", style = MaterialTheme.typography.headlineMedium)

        // Si los permisos fueron denegados, mostramos un mensaje de advertencia en color rojo (error).
        if (!permisosConcedidos) {
            Text(
                text = "Se requiere acceso a la ubicación para calcular la distancia.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Spacer crea un espacio en blanco para separar el título de la lista de productos.
        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn es el equivalente moderno y optimizado a RecyclerView.
        // Solo renderiza en memoria los elementos que son visibles actualmente en la pantalla.
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // items() recorre la lista de "lotes" que nos entregó el ViewModel.
            items(lotes) { lote ->

                // Card crea una tarjeta visual (un recuadro) para cada producto de la lista.
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

                    // Dentro de la tarjeta, organizamos los textos verticalmente.
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