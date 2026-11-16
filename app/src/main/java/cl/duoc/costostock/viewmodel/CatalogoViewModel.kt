package cl.duoc.costostock.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.costostock.model.EstadoLote
import cl.duoc.costostock.model.LoteOferta
import cl.duoc.costostock.model.RadioBusquedaKm
import cl.duoc.costostock.network.RetrofitClient
import cl.duoc.costostock.network.TransaccionesApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val apiService: TransaccionesApiService = RetrofitClient.transaccionesApi,
    private val iniciarCicloTiempoReal: Boolean = true
) : ViewModel() {

    private val _radioSeleccionado = MutableStateFlow(RadioBusquedaKm.RADIO_30_KM)
    val radioSeleccionado: StateFlow<RadioBusquedaKm> = _radioSeleccionado.asStateFlow()

    private val _lotesVisibles = MutableStateFlow<List<LoteOferta>>(emptyList())
    val lotesVisibles: StateFlow<List<LoteOferta>> = _lotesVisibles.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorRed = MutableStateFlow<String?>(null)
    val errorRed: StateFlow<String?> = _errorRed.asStateFlow()

    private var lotesDesdeServidor: List<LoteOferta> = emptyList()
    private var latitudUsuario: Double? = null
    private var longitudUsuario: Double? = null

    init {
        cargarCatalogoDesdeServidor()
        if (iniciarCicloTiempoReal) {
            iniciarMotorActualizacion()
        }
    }

    fun cargarCatalogoDesdeServidor() {
        viewModelScope.launch {
            _cargando.value = true
            _errorRed.value = null
            try {
                val radioKm = _radioSeleccionado.value.kilometros.toDouble()
                val respuestaDto = apiService.obtenerCatalogo(
                    lat = latitudUsuario,
                    lon = longitudUsuario,
                    radioKm = radioKm
                )
                lotesDesdeServidor = respuestaDto.map { it.toDomainModel() }
                aplicarFiltrosLocalesYContadores()
            } catch (e: Exception) {
                try {
                    Log.e("CatalogoViewModel", "Error conectando a MS-Transacciones: ${e.message}", e)
                } catch (_: RuntimeException) {
                    // Evita fallo en JVM local cuando android.util.Log no está mockeado
                }
                _errorRed.value = "Sin conexión con el servidor de catálogo"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        aplicarFiltrosLocalesYContadores()
    }

    fun cambiarRadioBusqueda(nuevoRadio: RadioBusquedaKm) {
        _radioSeleccionado.value = nuevoRadio
        cargarCatalogoDesdeServidor()
    }

    fun actualizarUbicacionUsuario(lat: Double, lon: Double) {
        latitudUsuario = lat
        longitudUsuario = lon
        cargarCatalogoDesdeServidor()
    }

    private fun iniciarMotorActualizacion() {
        viewModelScope.launch {
            while (true) {
                aplicarFiltrosLocalesYContadores()
                delay(1000)
            }
        }
    }

    fun aplicarFiltrosLocalesYContadores() {
        val tiempoActual = System.currentTimeMillis()
        val query = _textoBusqueda.value.trim()

        val lotesActualizados = lotesDesdeServidor.map { lote ->
            var nuevoEstado = lote.estadoActual
            var precioActualizado = lote.precioOfertaFinal

            if (tiempoActual >= lote.fechaCaducidadMillis) {
                nuevoEstado = EstadoLote.CADUCADO_BLOQUEADO
            } else if (tiempoActual >= lote.fechaHoraFinOfertaMillis) {
                nuevoEstado = EstadoLote.PRECIO_NORMAL
                precioActualizado = lote.precioNormalSinDescuento
            }

            lote.copy(
                estadoActual = nuevoEstado,
                precioOfertaFinal = precioActualizado
            )
        }.filter { lote ->
            val esVisiblePorEstado = (lote.estadoActual == EstadoLote.OFERTA_ACTIVA || lote.estadoActual == EstadoLote.PRECIO_NORMAL) &&
                    lote.stockUnidadesDisponible > 0

            val cumpleBusqueda = if (query.isNotEmpty()) {
                lote.nombreProducto.contains(query, ignoreCase = true) ||
                        lote.nombreComercio.contains(query, ignoreCase = true)
            } else {
                true
            }

            esVisiblePorEstado && cumpleBusqueda
        }

        _lotesVisibles.value = lotesActualizados
    }
}