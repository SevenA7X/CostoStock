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
    private val _radioSeleccionado = MutableStateFlow(RadioBusquedaKm.RADIO_30_KM)
    val radioSeleccionado: StateFlow<RadioBusquedaKm> = _radioSeleccionado.asStateFlow()

    private val _lotesVisibles = MutableStateFlow<List<LoteOferta>>(emptyList())
    val lotesVisibles: StateFlow<List<LoteOferta>> = _lotesVisibles.asStateFlow()
    // Estado para el buscador de texto
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        aplicarFiltros()
    }
    // 2. Base de datos de prueba (Mock) ajustada a sus 5 categorías
    // 2. Base de datos de prueba (Mock) con imágenes optimizadas para Coil
    private val lotesBase = listOf(
        LoteOferta(
            idLote = 1,
            idProveedor = 101,
            nombreComercio = "Bodega Viña Centro",
            nombreProducto = "Pack Bebidas Energéticas (Cerca de vencer)",
            descripcion = "Latas con fecha corta, empaque sellado.",
            categoria = CategoriaProducto.ABARROTES_ENVASADOS,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9LNYvklGy--a9GVKPtk45yNlKH2G-Fpj8RYi59PV_P4g0OsVMN39M26iI&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 10,
            montoLiquidoProveedor = 4000,
            comisionCostoStock = 400,
            precioOfertaFinal = 4400,
            precioNormalSinDescuento = 8000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 600000,
            fechaCaducidadMillis = System.currentTimeMillis() + 86400000,
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
            categoria = CategoriaProducto.BEBIDAS_ALCOHOLICAS,
            urlsFotografiasActuales = listOf("https://http2.mlstatic.com/D_NQ_NP_734077-MLU78178247643_082024-O.webp"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 24,
            montoLiquidoProveedor = 10000,
            comisionCostoStock = 1000,
            precioOfertaFinal = 11000,
            precioNormalSinDescuento = 15000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 3600000,
            fechaCaducidadMillis = System.currentTimeMillis() + 2592000000,
            kgCo2EvitadoPorUnidad = 1.2,
            direccionRetiro = "Av. Brasil 456, Valparaíso",
            referenciaRetiro = "Esquina Bellavista",
            horarioRetiro = "12:00 - 22:00",
            latitudRetiro = -33.0441,
            longitudRetiro = -71.6243,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 3,
            idProveedor = 103,
            nombreComercio = "Frigorífico del Norte",
            nombreProducto = "Bolsa de Arvejas Congeladas 1kg",
            descripcion = "Empaque con daños menores, producto sellado.",
            categoria = CategoriaProducto.CONGELADOS,
            urlsFotografiasActuales = listOf("https://dojiw2m9tvv09.cloudfront.net/85621/product/arvejas-minuto-verde-1-kg6400.jpg"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 15,
            montoLiquidoProveedor = 1000,
            comisionCostoStock = 200,
            precioOfertaFinal = 1200,
            precioNormalSinDescuento = 2500,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 7200000,
            fechaCaducidadMillis = System.currentTimeMillis() + 172800000,
            kgCo2EvitadoPorUnidad = 0.3,
            direccionRetiro = "Calle Valparaíso 789, Viña del Mar",
            referenciaRetiro = "Cámara 2",
            horarioRetiro = "08:00 - 15:00",
            latitudRetiro = -33.0255,
            longitudRetiro = -71.5528,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 4,
            idProveedor = 104,
            nombreComercio = "Heladería Central",
            nombreProducto = "Helado de Vainilla 2L (Envase dañado)",
            descripcion = "Tapa astillada, sello interior intacto.",
            categoria = CategoriaProducto.CONGELADOS,
            urlsFotografiasActuales = listOf("https://walmartcr.vtexassets.com/arquivos/ids/638954/7066_02.jpg?v=638568601439870000"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 8,
            montoLiquidoProveedor = 3000,
            comisionCostoStock = 500,
            precioOfertaFinal = 3500,
            precioNormalSinDescuento = 7000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 86400000,
            fechaCaducidadMillis = System.currentTimeMillis() + 172800000,
            kgCo2EvitadoPorUnidad = 0.8,
            direccionRetiro = "Feria El Belloto, Quilpué",
            referenciaRetiro = "Puesto 45",
            horarioRetiro = "07:00 - 14:00",
            latitudRetiro = -33.0489,
            longitudRetiro = -71.4397,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 5,
            idProveedor = 105,
            nombreComercio = "Farmacia San Juan",
            nombreProducto = "Caja de Ibuprofeno 400mg (Próximo a vencer)",
            descripcion = "Cajas con vencimiento en 2 meses.",
            categoria = CategoriaProducto.FARMACIA,
            urlsFotografiasActuales = listOf("https://profar.cl/media/catalog/product/cache/0ef4825a2b8913160efbbb4d7e84ca00/1/0/102928_1_kngfzyopyljirsym.webp"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 20,
            montoLiquidoProveedor = 1200,
            comisionCostoStock = 300,
            precioOfertaFinal = 1500,
            precioNormalSinDescuento = 3000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 43200000,
            fechaCaducidadMillis = System.currentTimeMillis() + 86400000,
            kgCo2EvitadoPorUnidad = 0.4,
            direccionRetiro = "1 Norte 1234, Viña del Mar",
            referenciaRetiro = "Caja central",
            horarioRetiro = "10:00 - 20:00",
            latitudRetiro = -33.0180,
            longitudRetiro = -71.5450,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 6,
            idProveedor = 106,
            nombreComercio = "Farmacia San Juan",
            nombreProducto = "Vitaminas C Efervescentes (Sobrestock)",
            descripcion = "Excedente de inventario temporada invierno.",
            categoria = CategoriaProducto.FARMACIA,
            urlsFotografiasActuales = listOf("https://www.mixgreen.cl/cdn/shop/files/13181a.jpg?height=533&v=1710258765"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 5,
            montoLiquidoProveedor = 4490,
            comisionCostoStock = 500,
            precioOfertaFinal = 4990,
            precioNormalSinDescuento = 8500,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 86400000,
            fechaCaducidadMillis = System.currentTimeMillis() + 1296000000,
            kgCo2EvitadoPorUnidad = 1.0,
            direccionRetiro = "Av. Libertad 450, Viña del Mar",
            referenciaRetiro = "Local 2",
            horarioRetiro = "09:30 - 18:30",
            latitudRetiro = -33.0150,
            longitudRetiro = -71.5480,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 7,
            idProveedor = 107,
            nombreComercio = "Ferretería El Maestro",
            nombreProducto = "Pack Ampolletas LED (Caja abierta)",
            descripcion = "Empaque dañado durante traslado.",
            categoria = CategoriaProducto.FERRETERIA,
            urlsFotografiasActuales = listOf("https://cdnx.jumpseller.com/electra-s-a/image/71338375/resize/430/573?1767351708"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 30,
            montoLiquidoProveedor = 700,
            comisionCostoStock = 100,
            precioOfertaFinal = 800,
            precioNormalSinDescuento = 2000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 21600000,
            fechaCaducidadMillis = System.currentTimeMillis() + 86400000,
            kgCo2EvitadoPorUnidad = 0.2,
            direccionRetiro = "San Juan 901, Quilpué",
            referenciaRetiro = "Pasillo 4",
            horarioRetiro = "07:00 - 21:00",
            latitudRetiro = -33.0450,
            longitudRetiro = -71.4420,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 8,
            idProveedor = 108,
            nombreComercio = "Ferretería El Maestro",
            nombreProducto = "Pintura Blanca 1 Galón (Tarro abollado)",
            descripcion = "Lata con abolladuras, no afecta el contenido.",
            categoria = CategoriaProducto.FERRETERIA,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSE2rMtWYwT0O9eQs1q-eoH4Yaly3Lf4L59OiSS-Xtktw&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 2,
            montoLiquidoProveedor = 6500,
            comisionCostoStock = 1000,
            precioOfertaFinal = 7500,
            precioNormalSinDescuento = 15000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 10800000,
            fechaCaducidadMillis = System.currentTimeMillis() + 172800000,
            kgCo2EvitadoPorUnidad = 1.5,
            direccionRetiro = "Calle Quinta 230, Viña del Mar",
            referenciaRetiro = "Mostrador principal",
            horarioRetiro = "11:00 - 19:00",
            latitudRetiro = -33.0230,
            longitudRetiro = -71.5540,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 9,
            idProveedor = 109,
            nombreComercio = "Distribuidora Frío",
            nombreProducto = "Hamburguesas de Vacuno Congeladas",
            descripcion = "Lote próximo a fecha de consumo preferente.",
            categoria = CategoriaProducto.CONGELADOS,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFwovpQ_fSryoz4ChkHE_5G9dTbAO1SXR092Fer-gMXA&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 12,
            montoLiquidoProveedor = 2800,
            comisionCostoStock = 400,
            precioOfertaFinal = 3200,
            precioNormalSinDescuento = 6000,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 14400000,
            fechaCaducidadMillis = System.currentTimeMillis() + 86400000,
            kgCo2EvitadoPorUnidad = 2.0,
            direccionRetiro = "Av. Pedro Montt 2000, Valparaíso",
            referenciaRetiro = "Andén 1",
            horarioRetiro = "08:00 - 16:00",
            latitudRetiro = -33.0455,
            longitudRetiro = -71.6150,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 10,
            idProveedor = 110,
            nombreComercio = "Minimarket Los Pinos",
            nombreProducto = "Pack Vienesas Envasadas 1kg",
            descripcion = "Sobrestock en abarrotes refrigerados.",
            categoria = CategoriaProducto.ABARROTES_ENVASADOS,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDdOpgktVZoZgnx1KMxexRNjPnbvBec7-5BrtoVYGHKrLjy9jwx6foRNM&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 18,
            montoLiquidoProveedor = 2400,
            comisionCostoStock = 400,
            precioOfertaFinal = 2800,
            precioNormalSinDescuento = 5500,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 86400000,
            fechaCaducidadMillis = System.currentTimeMillis() + 604800000,
            kgCo2EvitadoPorUnidad = 1.1,
            direccionRetiro = "Ruta 68 Km 1, Valparaíso",
            referenciaRetiro = "Bodega 3",
            horarioRetiro = "09:00 - 17:00",
            latitudRetiro = -33.0550,
            longitudRetiro = -71.6050,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 11,
            idProveedor = 111,
            nombreComercio = "Supermercado Ahorro",
            nombreProducto = "Fideos Espagueti 400g (Caja abollada)",
            descripcion = "Empaque secundario dañado, producto sellado.",
            categoria = CategoriaProducto.ABARROTES_ENVASADOS,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQo2JAyz7ZBlQBGY2N0igR7ZeTd2r-fxAX47-4skZTSCw&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis()- 172_800_000L,
            stockUnidadesDisponible = 50,
            montoLiquidoProveedor = 400,
            comisionCostoStock = 100,
            precioOfertaFinal = 500,
            precioNormalSinDescuento = 900,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 604800000,
            fechaCaducidadMillis = System.currentTimeMillis() + 31536000000,
            kgCo2EvitadoPorUnidad = 0.1,
            direccionRetiro = "Av. Argentina 100, Valparaíso",
            referenciaRetiro = "Servicio al cliente",
            horarioRetiro = "08:30 - 21:00",
            latitudRetiro = -33.0480,
            longitudRetiro = -71.6040,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        ),
        LoteOferta(
            idLote = 12,
            idProveedor = 112,
            nombreComercio = "Supermercado Ahorro",
            nombreProducto = "Salsa de Tomate 200g (Próximo a vencer)",
            descripcion = "Salsa tradicional en sachet.",
            categoria = CategoriaProducto.ABARROTES_ENVASADOS,
            urlsFotografiasActuales = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8xO_fkgmslItmJAvcJuK0DXBu5A8i-D7ZnS7RFFab6g&s=10"),
            fechaCapturaFotoMillis = System.currentTimeMillis(),
            stockUnidadesDisponible = 40,
            montoLiquidoProveedor = 200,
            comisionCostoStock = 100,
            precioOfertaFinal = 300,
            precioNormalSinDescuento = 650,
            fechaHoraFinOfertaMillis = System.currentTimeMillis() + 86400000,
            fechaCaducidadMillis = System.currentTimeMillis() + 259200000,
            kgCo2EvitadoPorUnidad = 0.1,
            direccionRetiro = "Los Pinos 500, Quilpué",
            referenciaRetiro = "Caja 1",
            horarioRetiro = "09:00 - 22:00",
            latitudRetiro = -33.0650,
            longitudRetiro = -71.4350,
            estadoActual = EstadoLote.OFERTA_ACTIVA
        )
    )

    init {
        _lotesVisibles.value = lotesBase
        iniciarMotorActualizacion()
    }

    // 3. Función para cambiar el radio desde la UI
    fun cambiarRadioBusqueda(nuevoRadio: RadioBusquedaKm) {
        _radioSeleccionado.value = nuevoRadio
        aplicarFiltros()
    }

    // 4. Motor de validación de tiempo real
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

        // declarando query antes de llamar a la lista
        val query = _textoBusqueda.value.trim()

        val lotesActualizados = lotesBase.map { lote ->
            var nuevoEstado = lote.estadoActual

            if (tiempoActual >= lote.fechaCaducidadMillis) {
                nuevoEstado = EstadoLote.CADUCADO_BLOQUEADO
            } else if (tiempoActual >= lote.fechaHoraFinOfertaMillis) {
                nuevoEstado = EstadoLote.PRECIO_NORMAL
            }

            lote.copy(estadoActual = nuevoEstado)
        }.filter { lote ->
            // Validación 1: Estado y Stock
            val esVisiblePorEstado = (lote.estadoActual == EstadoLote.OFERTA_ACTIVA || lote.estadoActual == EstadoLote.PRECIO_NORMAL) &&
                    lote.stockUnidadesDisponible > 0

            // Validación 2: Distancia GPS
            val esVisiblePorDistancia = if (latitudUsuario != null && longitudUsuario != null) {
                val distancia = calcularDistanciaKm(
                    latitudUsuario!!, longitudUsuario!!,
                    lote.latitudRetiro, lote.longitudRetiro
                )
                distancia <= _radioSeleccionado.value.kilometros
            } else {
                true // Si aún no hay GPS, mostramos todo por defecto
            }

            // Validación 3: Buscador de texto
            val cumpleBusqueda = if (query.isNotEmpty()) {
                lote.nombreProducto.contains(query, ignoreCase = true) ||
                        lote.nombreComercio.contains(query, ignoreCase = true)
            } else { true }

            esVisiblePorEstado && esVisiblePorDistancia && cumpleBusqueda
        }

        _lotesVisibles.value = lotesActualizados
    }
    // Variables para almacenar la ubicación del teléfono
    private var latitudUsuario: Double? = null
    private var longitudUsuario: Double? = null

    // Función que recibirá las coordenadas desde la UI
    fun actualizarUbicacionUsuario(lat: Double, lon: Double) {
        latitudUsuario = lat
        longitudUsuario = lon
        aplicarFiltros()
    }

    // Fórmula de Haversine para calcular distancia real en kilómetros
    private fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return radioTierra * c
    }
}