package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.costostock.data.room.ImpactoDao
import cl.duoc.costostock.data.room.VoucherDao
import cl.duoc.costostock.data.room.VoucherEntity
import cl.duoc.costostock.model.CompradorLigero
import cl.duoc.costostock.model.EstadoVoucher
import cl.duoc.costostock.model.ResumenImpactoMetricas
import cl.duoc.costostock.model.VoucherRetiro
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class VouchersViewModel(private val voucherDao: VoucherDao) : ViewModel() {
    val vouchers: StateFlow<List<VoucherRetiro>> = voucherDao.obtenerTodosLosVouchers()
        .map { listaEntities ->
            listaEntities.map { entity ->
                VoucherRetiro(
                    idVoucher = entity.idVoucher,
                    codigoOrdenQR = entity.codigoOrdenQR,
                    idTransaccionPago = "TX-DEFAULT",
                    comprador = CompradorLigero(
                        nombreCompleto = entity.nombreComprador,
                        correoElectronico = "correo@ejemplo.com",
                        telefonoContacto = "+56900000000"
                    ),
                    idLote = 0L,
                    nombreProducto = entity.nombreProducto,
                    cantidadUnidades = 1,
                    subtotalPagado = entity.montoTotalPagado,
                    comisionPagada = 0,
                    montoTotalPagado = entity.montoTotalPagado,
                    idProveedor = 0L,
                    nombreComercio = entity.nombreComercio,
                    direccionExactaRetiro = entity.direccionExactaRetiro,
                    coordenadasGpsRetiro = "0.0,0.0",
                    horarioValidoRetiro = entity.horarioValidoRetiro,
                    kgCo2Evitado = entity.kgCo2Evitado,
                    requiereVerificacionMayorEdad = false,
                    estadoVoucher = EstadoVoucher.PAGADO_LISTO_RETIRO,
                    fechaEmisionMillis = entity.fechaEmisionMillis
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarVoucherExitoso(voucher: VoucherEntity) {
        viewModelScope.launch {
            voucherDao.insertarVoucher(voucher)
        }
    }

    // --- NUEVA FUNCIÓN PARA EL ESCÁNER ---
    fun validarYEntregarVoucher(codigoQR: String, onExito: () -> Unit) {
        viewModelScope.launch {
            val voucherEncontrado = vouchers.value.find { it.codigoOrdenQR == codigoQR }
            if (voucherEncontrado != null) {
                onExito()
            }
        }
    }
}

class ImpactoViewModel(private val voucherDao: VoucherDao) : ViewModel() {
    companion object {
        const val KG_CO2_POR_ARBOL = 21.0
    }

    val metricas: StateFlow<ResumenImpactoMetricas?> = voucherDao.obtenerTodosLosVouchers()
        .map { listaEntities ->
            val totalCo2 = listaEntities.sumOf { it.kgCo2Evitado }
            val totalLotes = listaEntities.size

            ResumenImpactoMetricas(
                totalKgCo2Evitado = totalCo2,
                equivalenciaArbolesPlantados = (totalCo2 / KG_CO2_POR_ARBOL).roundToInt(),
                totalLotesVendidosOComprados = totalLotes,
                montoLiquidoTotalAcumulado = listaEntities.sumOf { it.montoTotalPagado }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

class ModulosViewModelFactory(
    private val voucherDao: VoucherDao,
    private val impactoDao: ImpactoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VouchersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VouchersViewModel(voucherDao) as T
        }
        if (modelClass.isAssignableFrom(ImpactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImpactoViewModel(voucherDao) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}