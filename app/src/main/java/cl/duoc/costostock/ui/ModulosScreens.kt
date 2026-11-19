package cl.duoc.costostock.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.costostock.model.VoucherRetiro
import cl.duoc.costostock.viewmodel.ImpactoViewModel
import cl.duoc.costostock.viewmodel.VouchersViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(viewModel: VouchersViewModel, navController: NavController) {
    val vouchers by viewModel.vouchers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Vouchers", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate("validar_voucher") }) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Validar Voucher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (vouchers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no tienes vouchers de compra.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                items(vouchers) { voucher ->
                    VoucherTicket(voucher = voucher)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun VoucherTicket(voucher: VoucherRetiro) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. HEADER: ESTADO Y FECHA
            Text(
                text = "LISTO PARA RETIRO",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Emitido: ${formatearFecha(voucher.fechaEmisionMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. SECCIÓN DEL QR
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                ImagenQRReal(
                    codigo = voucher.codigoOrdenQR,
                    modifier = Modifier.size(140.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = voucher.codigoOrdenQR,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 3. INSTRUCCIONES DE RETIRO
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Retirar en:", color = Color.Gray, fontSize = 12.sp)
                Text(text = voucher.nombreComercio, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(text = voucher.direccionExactaRetiro, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                }
                Text(
                    text = "Horario: ${voucher.horarioValidoRetiro}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE65100),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // 4. DATOS DEL CLIENTE
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Datos del Comprador:", color = Color.Gray, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(
                        text = voucher.comprador.nombreCompleto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // 5. RESUMEN DE LA ORDEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(0.65f)) {
                    Text(text = "Producto:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = "${voucher.nombreProducto} (x${voucher.cantidadUnidades})",
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(0.35f)
                ) {
                    Text(
                        text = "Total Pagado:",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "$${voucher.montoTotalPagado}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 6. FOOTER: IMPACTO AMBIENTAL Y ALERTAS
            if (voucher.requiereVerificacionMayorEdad) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "+18", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Text(
                        text = " Requiere presentar Cédula de Identidad (+18)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌱 Con esta compra evitaste ${voucher.kgCo2Evitado} kg de CO2",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(viewModel: ImpactoViewModel) {
    val metricas by viewModel.metricas.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Impacto Ambiental", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tu contribución al planeta 🌍", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("💨 Total CO2 Evitado: ${metricas?.totalKgCo2Evitado ?: "0.0"} kg", style = MaterialTheme.typography.bodyLarge)
                    Text("🌳 Árboles Equivalentes: ${metricas?.equivalenciaArbolesPlantados ?: 0}", style = MaterialTheme.typography.bodyLarge)
                    Text("📦 Lotes Salvados: ${metricas?.totalLotesVendidosOComprados ?: 0}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private fun formatearFecha(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}