package cl.duoc.costostock.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.viewmodel.ImpactoViewModel
import cl.duoc.costostock.viewmodel.VouchersViewModel

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
                Text("Aún no tienes vouchers de compra.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                items(vouchers) { voucher ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(voucher.nombreProducto, fontWeight = FontWeight.Bold)
                            Text("Comprador: ${voucher.comprador.nombreCompleto}")
                            Text("Comercio: ${voucher.nombreComercio}")
                            Text("Retiro: ${voucher.horarioValidoRetiro}")
                            Text("Total pagado: $${voucher.montoTotalPagado}")
                        }
                    }
                }
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Total CO2 Evitado: ${metricas?.totalKgCo2Evitado ?: "0.0"} kg")
            Text("Árboles Equivalentes: ${metricas?.equivalenciaArbolesPlantados ?: 0}")
            Text("Lotes Salvados: ${metricas?.totalLotesVendidosOComprados ?: 0}")
        }
    }
}