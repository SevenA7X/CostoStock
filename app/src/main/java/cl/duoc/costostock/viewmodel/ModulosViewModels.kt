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

class VouchersViewModel(private val voucherDao: VoucherDao) : ViewModel() {
    val vouchers: StateFlow<List<VoucherRetiro>> = voucherDao.obtenerTodosLosVouchers()
        .map { listaEntities ->
            listaEntities.map { entity ->
                // Llenamos TODOS los parámetros exigidos por el contrato
                VoucherRetiro(
                    idVoucher = System.currentTimeMillis(), // Asignación simulada para cumplir contrato
                    codigoOrdenQR = "QR-${entity.nombreProducto.take(3).uppercase()}",
                    idTransaccionPago = "TX-DEFAULT",
                    comprador = CompradorLigero(
                        nombreCompleto = "Comprador Local",
                        correoElectronico = "correo@ejemplo.com",
                        telefonoContacto = "+56900000000"
                    ),
                    idLote = 0L,
                    nombreProducto = entity.nombreProducto,
                    cantidadUnidades = 1,
                    subtotalPagado = 0,
                    comisionPagada = 0,
                    montoTotalPagado = 5000, // Ajustar con el de la DB si existe
                    idProveedor = 0L,
                    nombreComercio = "Comercio Asociado",
                    direccionExactaRetiro = "Dirección referencial",
                    coordenadasGpsRetiro = "0.0,0.0",
                    horarioValidoRetiro = "09:00 - 18:00",
                    kgCo2Evitado = 2.5,
                    requiereVerificacionMayorEdad = false,
                    estadoVoucher = EstadoVoucher.PAGADO_LISTO_RETIRO,
                    fechaEmisionMillis = System.currentTimeMillis()
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarVoucherExitoso(voucher: VoucherEntity) {
        viewModelScope.launch {
            voucherDao.insertarVoucher(voucher)
        }
    }
}

class ImpactoViewModel(private val impactoDao: ImpactoDao) : ViewModel() {
    val metricas: StateFlow<ResumenImpactoMetricas?> = impactoDao.obtenerMetricas()
        .map { entity ->
            entity?.let {
                ResumenImpactoMetricas(
                    totalKgCo2Evitado = it.totalKgCo2Evitado,
                    equivalenciaArbolesPlantados = it.equivalenciaArbolesPlantados,
                    totalLotesVendidosOComprados = it.totalLotesVendidosOComprados,
                    montoLiquidoTotalAcumulado = 0 // Parámetro del contrato
                )
            }
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
            return ImpactoViewModel(impactoDao) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}