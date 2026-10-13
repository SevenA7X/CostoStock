package cl.duoc.costostock.viewmodel


import androidx.lifecycle.ViewModel
import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.model.EstadoLote
import cl.duoc.costostock.model.LoteOferta
import cl.duoc.costostock.model.RadioBusquedaKm
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CatalogoViewModel : ViewModel() {


// 1. Estados que leerá la interfaz (UI)

    private val _radioSeleccionado = MutableStateFlow(RadioBusquedaKm.RADIO_15_KM)

    val radioSeleccionado: StateFlow<RadioBusquedaKm> = _radioSeleccionado.asStateFlow()


    private val _lotesVisibles = MutableStateFlow<List<LoteOferta>>(emptyList())

    val lotesVisibles: StateFlow<List<LoteOferta>> = _lotesVisibles.asStateFlow()


// 2. Base de datos de prueba (Mock) con 2 productos para trabajar sin bloqueos

    private val lotesBase = listOf(

        LoteOferta(

            idLote = 1,

            idProveedor = 101,

            nombreComercio = "Bodega Viña Centro",

            nombreProducto = "Pack Bebidas Energéticas (Cerca de vencer)",

            descripcion = "Latas con fecha corta, empaque sellado.",

            categoria = CategoriaProducto.ABARROTES_ENVASADOS,

            urlsFotografiasActuales = emptyList(),

            fechaCapturaFotoMillis = System.currentTimeMillis(),

            stockUnidadesDisponible = 10,

            montoLiquidoProveedor = 4000,

            comisionCostoStock = 400,

            precioOfertaFinal = 4400,

            precioNormalSinDescuento = 8000,

            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 600000, // Termina en 10 minutos

            fechaCaducidadMillis = System.currentTimeMillis() + 86400000, // Caduca en 1 día

            kgCo2EvitadoPorUnidad = 0.5,

            direccionRetiro = "Álvarez 123, Viña del Mar",

            referenciaRetiro = "Local 4",

            horarioRetiro = "09:00 - 18:00",

            latitudRetiro = -33.0245,

            longitudRetiro = -71.5518,

            estadoActual = EstadoLote.OFERTA_ACTIVA

        ),

        LoteOferta(

            idLote = 2,

            idProveedor = 102,

            nombreComercio = "Licorería El Puerto",

            nombreProducto = "Cervezas Artesanales (Excedente)",

            descripcion = "Cambio de etiqueta de la marca.",

            categoria = CategoriaProducto.BEBIDAS_ALCOHOLICAS, // Para probar el aviso +18

            urlsFotografiasActuales = emptyList(),

            fechaCapturaFotoMillis = System.currentTimeMillis(),

            stockUnidadesDisponible = 24,

            montoLiquidoProveedor = 10000,

            comisionCostoStock = 1000,

            precioOfertaFinal = 11000,

            precioNormalSinDescuento = 15000,

            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 3600000, // Termina en 1 hora

            fechaCaducidadMillis = System.currentTimeMillis() + 2592000000, // Caduca en 30 días

            kgCo2EvitadoPorUnidad = 1.2,

            direccionRetiro = "Av. Brasil 456, Valparaíso",

            referenciaRetiro = "Esquina Bellavista",

            horarioRetiro = "12:00 - 22:00",

            latitudRetiro = -33.0441,

            longitudRetiro = -71.6243,

            estadoActual = EstadoLote.OFERTA_ACTIVA

        )

    )


    init {

        _lotesVisibles.value = lotesBase

        iniciarMotorActualizacion()

    }

// 3. Función para cambiar el radio desde la UI (RF-08)

    fun cambiarRadioBusqueda(nuevoRadio: RadioBusquedaKm) {

        _radioSeleccionado.value = nuevoRadio

        aplicarFiltros()

    }


// 4. Motor de validación de tiempo real (RF-10 y RF-11)

    private fun iniciarMotorActualizacion() {

        viewModelScope.launch {
            while (true) {
                aplicarFiltros()
                delay(1000) // Se ejecuta cada 1 segundo para los contadores
            }
        }

    }


    private fun aplicarFiltros() {

        val tiempoActual = System.currentTimeMillis()


        val lotesActualizados = lotesBase.map { lote ->

            var nuevoEstado = lote.estadoActual


// RF-11: Si superó la fecha de caducidad física, se bloquea y oculta

            if (tiempoActual >= lote.fechaCaducidadMillis) {

                nuevoEstado = EstadoLote.CADUCADO_BLOQUEADO

            }

// RF-10: Si superó el tiempo de la oferta, revierte a precio normal

            else if (tiempoActual >= lote.fechaHoraFinOfertaMillis) {

                nuevoEstado = EstadoLote.PRECIO_NORMAL

            }


// Actualizamos el objeto con su estado verificado

            lote.copy(estadoActual = nuevoEstado)

        }.filter { lote ->

// Filtro visual: Solo mostrar si está activo o en precio normal, y con stock

            (lote.estadoActual == EstadoLote.OFERTA_ACTIVA || lote.estadoActual == EstadoLote.PRECIO_NORMAL) &&

                    lote.stockUnidadesDisponible > 0

// Nota: El descarte de distancia por GPS lo integraremos en el Issue #3

        }



        _lotesVisibles.value = lotesActualizados

    }

}