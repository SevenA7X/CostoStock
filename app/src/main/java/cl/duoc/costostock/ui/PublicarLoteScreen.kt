package cl.duoc.costostock.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.viewmodel.CampoLote
import cl.duoc.costostock.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarLoteScreen(
    navController: NavController,
    viewModel: ProveedorViewModel
) {
    val state by viewModel.publicarState.collectAsState()

    // Estado para expandir o contraer el menú de categorías
    var categoriaExpanded by remember { mutableStateOf(false) }

    // Lanzador para abrir la cámara y capturar la foto (devuelve un Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.onFotoCapturada(bitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Lote", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Panel"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.nombreProducto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre del producto") },
                isError = state.errores.containsKey(CampoLote.NOMBRE),
                supportingText = { state.errores[CampoLote.NOMBRE]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción") },
                isError = state.errores.containsKey(CampoLote.DESCRIPCION),
                supportingText = { state.errores[CampoLote.DESCRIPCION]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            // MENÚ DESPLEGABLE DE CATEGORÍAS REAL
            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.categoria?.name?.replace("_", " ") ?: "Seleccione una...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = state.errores.containsKey(CampoLote.CATEGORIA),
                    supportingText = { state.errores[CampoLote.CATEGORIA]?.let { Text(it) } }
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    CategoriaProducto.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.name.replace("_", " ")) },
                            onClick = {
                                viewModel.onCategoriaChange(categoria)
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.stockTexto,
                onValueChange = { viewModel.onStockChange(it) },
                label = { Text("Stock (unidades)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.errores.containsKey(CampoLote.STOCK),
                supportingText = { state.errores[CampoLote.STOCK]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.montoLiquidoTexto,
                onValueChange = { viewModel.onMontoChange(it) },
                label = { Text("Monto líquido por unidad (CLP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.errores.containsKey(CampoLote.MONTO),
                supportingText = { state.errores[CampoLote.MONTO]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.precioNormalTexto,
                onValueChange = { viewModel.onPrecioNormalChange(it) },
                label = { Text("Precio normal sin descuento (CLP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.errores.containsKey(CampoLote.PRECIO_NORMAL),
                supportingText = { state.errores[CampoLote.PRECIO_NORMAL]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.caducidadTexto,
                onValueChange = { viewModel.onCaducidadChange(it) },
                label = { Text("Fecha de caducidad (dd/mm/aaaa)") },
                isError = state.errores.containsKey(CampoLote.CADUCIDAD),
                supportingText = { state.errores[CampoLote.CADUCIDAD]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = state.vigenciaHorasTexto,
                onValueChange = { viewModel.onVigenciaChange(it) },
                label = { Text("Vigencia de la promoción (horas)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.errores.containsKey(CampoLote.VIGENCIA),
                supportingText = { state.errores[CampoLote.VIGENCIA]?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN FOTO CONECTADO A LA CÁMARA
            OutlinedButton(
                onClick = { cameraLauncher.launch(null) }, // Abre la cámara
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                // Cambia el texto si ya se capturó una foto
                Text(if (state.fotoCapturada != null) "Foto capturada. ¿Cambiar?" else "Tomar Foto")
            }
            if (state.errores.containsKey(CampoLote.FOTO)) {
                Text(
                    text = state.errores[CampoLote.FOTO]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen de precios",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tú recibes (líquido)")
                        Text("$${if(state.montoLiquidoTexto.isEmpty()) "0" else state.montoLiquidoTexto}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Comisión CostoStock (10%)")
                        Text("$${state.comision}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Precio final de oferta", fontWeight = FontWeight.Bold)
                        Text("$${state.precioOfertaFinal}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.publicarLote() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Publicar Lote", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (state.publicado) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Lote publicado con éxito!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = { viewModel.reiniciarFormulario() }) {
                                Text("Publicar otro")
                            }
                            TextButton(onClick = { navController.navigate("validar_voucher") }) {
                                Text("Validar voucher")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { navController.navigate("panel_proveedor") { popUpTo("panel_proveedor") { inclusive = true } } },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Volver al Panel de Comercio")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}