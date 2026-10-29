package cl.duoc.costostock.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.viewmodel.FactoresImpacto
import cl.duoc.costostock.viewmodel.ImpactoViewModel
import cl.duoc.costostock.viewmodel.VouchersViewModel
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

private val LOCALE_CL: Locale = Locale.forLanguageTag("es-CL")

private fun formato(valor: Double, decimales: Int = 1): String =
    String.format(LOCALE_CL, "%." + decimales + "f", valor)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(viewModel: ImpactoViewModel) {
    val estado by viewModel.estado.collectAsState()

    val kgAnimado by animateFloatAsState(
        targetValue = estado.totalKgCo2Evitado.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "kgCo2Animado"
    )
    val progresoAnimado by animateFloatAsState(
        targetValue = estado.progresoSiguienteArbol,
        animationSpec = tween(durationMillis = 800),
        label = "progresoArbolAnimado"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Impacto Ambiental", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("CO2 evitado", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formato(kgAnimado.toDouble()) + " kg",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Estimación según tus compras", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nivel: " + estado.nivel.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Te faltan " + formato(estado.kgParaSiguienteArbol) + " kg de CO2 para completar tu próximo árbol",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Árboles equivalentes", formato(estado.arbolesEquivalentes, 2), Modifier.weight(1f))
                TarjetaMetrica("Km en auto evitados", formato(estado.kmAutoEquivalentes, 1), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Lotes salvados", estado.lotesSalvados.toString(), Modifier.weight(1f))
                TarjetaMetrica("Comercios apoyados", estado.comerciosApoyados.toString(), Modifier.weight(1f))
            }

            if (estado.lotesSalvados == 0) {
                Text(
                    text = "Aún no tienes impacto registrado. Reserva tu primera oferta para empezar a sumar.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Valores estimados. Equivalencias referenciales: 1 árbol = " +
                        FactoresImpacto.KG_CO2_POR_ARBOL.toInt() + " kg de CO2 al año; 1 km en auto = " +
                        formato(FactoresImpacto.KG_CO2_POR_KM_AUTO, 2) + " kg de CO2.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TarjetaMetrica(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}