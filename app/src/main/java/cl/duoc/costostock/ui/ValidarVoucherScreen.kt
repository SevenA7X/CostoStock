package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.costostock.model.VoucherRetiro
import cl.duoc.costostock.viewmodel.ProveedorViewModel
import cl.duoc.costostock.viewmodel.ResultadoVoucher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidarVoucherScreen(
    navController: NavController,
    viewModel: ProveedorViewModel = viewModel()) {
    val resultado by viewModel.resultadoVoucher.collectAsState()
    val metricas by viewModel.metricas.collectAsState()
    var codigo by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Validar voucher") }) }) { padding ->
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código QR del voucher") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { viewModel.validarCodigoQR(codigo) },
                modifier = Modifier.fillMaxWidth())
            { Text("Validar código") }

            AnimatedContent(targetState = resultado, label = "resultadoVoucher") { r ->
                when (r) {
                    is ResultadoVoucher.SinValidar -> {}
                    is ResultadoVoucher.Invalido -> MensajeError(r.motivo)
                    is ResultadoVoucher.Valido -> TarjetaVoucher(
                        voucher = r.voucher,
                        onConfirmar = { viewModel.confirmarEntrega() })
                    is ResultadoVoucher.Entregado -> EntregaConfirmada(
                        voucher = r.voucher,
                        onOtro = {
                            codigo = ""
                            viewModel.reiniciarValidacion() }) } }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tus métricas", fontWeight = FontWeight.Bold)
                    Text("Entregas confirmadas: ${metricas.totalLotesVendidosOComprados}")
                    Text("CO2 evitado: ${"%.1f".format(metricas.totalKgCo2Evitado)} kg")
                    Text("Equivale a ${metricas.equivalenciaArbolesPlantados} árboles al año") } }

            TextButton(onClick = { navController.popBackStack() }) { Text("Volver") } } } }
@Composable
private fun TarjetaVoucher(voucher: VoucherRetiro, onConfirmar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Voucher válido", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Text("Producto: ${voucher.nombreProducto} x${voucher.cantidadUnidades}")
            Text("Comprador: ${voucher.comprador.nombreCompleto}")
            Text("Total pagado: $${voucher.montoTotalPagado}")
            if (voucher.requiereVerificacionMayorEdad) {
                Text(
                    "Producto +18: verifique la cédula de identidad antes de entregar",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold) }
            Button(onClick = onConfirmar, modifier = Modifier.fillMaxWidth()) {
                Text("Confirmar entrega") } } } }
@Composable
private fun EntregaConfirmada(voucher: VoucherRetiro, onOtro: () -> Unit) {
    val escala = remember { Animatable(0f) }
    LaunchedEffect(Unit) { escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    Column( modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Filled.CheckCircle,
            contentDescription = "Entrega confirmada",
            tint = Color(0xFF2E7D32),
            modifier = Modifier
                .size(96.dp)
                .scale(escala.value))
        Text("¡Entrega confirmada!", fontWeight = FontWeight.Bold)
        Text("${voucher.nombreProducto} entregado a ${voucher.comprador.nombreCompleto}")
        OutlinedButton(onClick = onOtro) { Text("Validar otro voucher") }
    }
}