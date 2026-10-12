package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.viewmodel.CampoLote
import cl.duoc.costostock.viewmodel.ProveedorViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarLoteScreen(
    navController: NavController,
    viewModel: ProveedorViewModel = viewModel()) {
    val state by viewModel.publicarState.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Publicar lote") }) }) { padding ->
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoTexto(
                etiqueta = "Nombre del producto",
                valor = state.nombreProducto,
                onCambio = viewModel::onNombreChange,
                error = state.errores[CampoLote.NOMBRE])

            CampoTexto(
            etiqueta = "Descripción",
            valor = state.descripcion,
            onCambio = viewModel::onDescripcionChange,
            error = state.errores[CampoLote.DESCRIPCION],
            unaLinea = false)

            SelectorCategoria(
                seleccionada = state.categoria,
                onSeleccion = viewModel::onCategoriaChange,
                error = state.errores[CampoLote.CATEGORIA])

            CampoTexto(
                etiqueta = "Stock (unidades)",
                valor = state.stockTexto,
                onCambio = viewModel::onStockChange,
                error = state.errores[CampoLote.STOCK],
                numerico = true )

            CampoTexto(
                etiqueta = "Monto líquido por unidad (CLP)",
                valor = state.montoLiquidoTexto,
                onCambio = viewModel::onMontoChange,
                error = state.errores[CampoLote.MONTO],
                numerico = true)

            CampoTexto(
                etiqueta = "Precio normal sin descuento (CLP)",
                valor = state.precioNormalTexto,
                onCambio = viewModel::onPrecioNormalChange,
                error = state.errores[CampoLote.PRECIO_NORMAL],
                numerico = true)

            CampoTexto(
                etiqueta = "Fecha de caducidad (dd/mm/aaaa)",
                valor = state.caducidadTexto,
                onCambio = viewModel::onCaducidadChange,
                error = state.errores[CampoLote.CADUCIDAD])

            CampoTexto(
                etiqueta = "Vigencia de la promoción (horas)",
                valor = state.vigenciaHorasTexto,
                onCambio = viewModel::onVigenciaChange,
                error = state.errores[CampoLote.VIGENCIA],
                numerico = true)

            CapturaFotoLote(
                foto = state.fotoCapturada,
                error = state.errores[CampoLote.FOTO],
                onFotoCapturada = viewModel::onFotoCapturada
            )

            AnimatedVisibility(
                visible = state.precioOfertaFinal > 0,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()) { Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Resumen de precios", fontWeight = FontWeight.Bold)
                        FilaPrecio("Tú recibes (líquido)", state.montoLiquidoTexto.toIntOrNull() ?: 0)
                        FilaPrecio("Comisión CostoStock (10%)", state.comision)
                        FilaPrecio("Precio final de oferta", state.precioOfertaFinal, destacado = true) } } }

            Button(
                onClick = { viewModel.publicarLote() },
                modifier = Modifier.fillMaxWidth()) { Text("Publicar lote") }

            AnimatedVisibility(visible = state.publicado, enter = fadeIn() + expandVertically()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(
                            Icons.Filled.CheckCircle,
                        contentDescription = "Lote publicado",
                        tint = Color(0xFF2E7D32))
                        Text("¡Lote publicado con éxito!", fontWeight = FontWeight.Bold)
                        Row {
                            TextButton(onClick = { viewModel.reiniciarFormulario() }) { Text("Publicar otro") }
                            TextButton(onClick = { navController.navigate("validar_voucher") }) {
                                Text("Validar voucher") } } } } } } } }

@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    error: String?,
    numerico: Boolean = false,
    unaLinea: Boolean = true) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        singleLine = unaLinea,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numerico) KeyboardType.Number else KeyboardType.Text
        ),
        trailingIcon = {
            if (error != null) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        modifier = Modifier.fillMaxWidth() ) }

@Composable
private fun SelectorCategoria(
    seleccionada: CategoriaProducto?,
    onSeleccion: (CategoriaProducto) -> Unit,
    error: String?) {
    var expandido by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box {
            OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) {
                Text(seleccionada?.let { etiquetaCategoria(it) } ?: "Seleccione una categoría")
            }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                CategoriaProducto.entries.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(etiquetaCategoria(categoria)) },
                        onClick = {
                            onSeleccion(categoria)
                            expandido = false }) } } }
        error?.let { MensajeError(it) } } }
@Composable
private fun FilaPrecio(etiqueta: String, valor: Int, destacado: Boolean = false) {
    val animado by animateIntAsState(targetValue = valor, animationSpec = tween(500), label = "precio")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal)
        Text(formatoPesos(animado), fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal)
    } }
private fun etiquetaCategoria(c: CategoriaProducto): String {
    val base = c.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
    return if (c.requiereVerificacionEdad) "$base (+18)" else base }
private fun formatoPesos(n: Int): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,d", n)