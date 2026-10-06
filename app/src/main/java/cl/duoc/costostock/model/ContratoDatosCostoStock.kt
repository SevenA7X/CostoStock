package cl.duoc.costostock.model


// A. ENUMERACIONES OFICIALES (DOMINIO DE DATOS)


// RF-04 y RF-15: Categorías permitidas (Excluye estrictamente alimentos frescos)
enum class CategoriaProducto(val requiereVerificacionEdad: Boolean) {
    ABARROTES_ENVASADOS(false),
    CONGELADOS(false),
    FERRETERIA(false),
    FARMACIA(true),
    BEBIDAS_ALCOHOLICAS(true)
}

// RF-10 y RF-11: Estados del ciclo de vida del lote
enum class EstadoLote {
    OFERTA_ACTIVA,
    PRECIO_NORMAL,
    AGOTADO_BLOQUEADO,
    CADUCADO_BLOQUEADO,
    RETIRADO_MANUAL
}

// RF-08: Radios de filtrado GPS permitidos (5, 15 y 30 km)
enum class RadioBusquedaKm(val kilometros: Int) {
    RADIO_5_KM(5),
    RADIO_15_KM(15),
    RADIO_30_KM(30)
}

// RF-14 y RF-16: Estados del Voucher QR de retiro (Pick-up)
enum class EstadoVoucher {
    PAGADO_LISTO_RETIRO,
    ENTREGADO,
    EXPIRADO_CANCELADO
}

// RF-18: Tipos de alerta comercial Push
enum class TipoNotificacionPush {
    OFERTA_CERCANA_GPS,
    CONTADOR_POR_VENCER
}

// ============================================================================
// B. MODELOS DE DATOS PUROS (CONTRATO ENTRE CAPAS)
// ============================================================================

// 1. Proveedor Formalizado (RF-03, RF-04, RF-07)
data class Proveedor(
    val idProveedor: Long,
    val razonSocial: String,
    val rutComercial: String,
    val correo: String,
    val validadoPorAdminWeb: Boolean,
    val direccionSucursal: String,
    val referenciaLocal: String,
    val latitud: Double,
    val longitud: Double,
    val horarioAtencion: String
)

// 2. Lote / Oferta Publicada (RF-04, RF-05, RF-06, RF-08, RF-09, RF-10, RF-11)
data class LoteOferta(
    val idLote: Long,
    val idProveedor: Long,
    val nombreComercio: String,
    val nombreProducto: String,
    val descripcion: String,
    val categoria: CategoriaProducto,
    val urlsFotografiasActuales: List<String>,
    val fechaCapturaFotoMillis: Long,
    val stockUnidadesDisponible: Int,
    val montoLiquidoProveedor: Int,
    val comisionCostoStock: Int,
    val precioOfertaFinal: Int,
    val precioNormalSinDescuento: Int,
    val fechaHoraFinOfertaMillis: Long,
    val fechaCaducidadMillis: Long,
    val kgCo2EvitadoPorUnidad: Double,
    val direccionRetiro: String,
    val referenciaRetiro: String,
    val horarioRetiro: String,
    val latitudRetiro: Double,
    val longitudRetiro: Double,
    val estadoActual: EstadoLote = EstadoLote.OFERTA_ACTIVA
)

// 3. Registro Ligero del Comprador en Checkout (RF-01, RF-02)
data class CompradorLigero(
    val nombreCompleto: String,
    val correoElectronico: String,
    val telefonoContacto: String
)

// 4. Reserva Temporal en Carrito (RF-12)
data class ReservaCarrito(
    val idReserva: String,
    val idLote: Long,
    val nombreProducto: String,
    val nombreComercio: String,
    val urlFotoMiniatura: String,
    val direccionRetiro: String,
    val horarioRetiro: String,
    val cantidadUnidades: Int,
    val subtotalMontoLiquido: Int,
    val comisionCostoStockTotal: Int,
    val totalAPagar: Int,
    val kgCo2TotalEstimado: Double,
    val requiereVerificacionEdad: Boolean,
    val timestampInicioReservaMillis: Long,
    val timestampExpiracionMillis: Long
)

// 5. Voucher Digital de Retiro e Impacto (RF-13, RF-14, RF-15, RF-16, RF-17)
data class VoucherRetiro(
    val idVoucher: Long,
    val codigoOrdenQR: String,
    val idTransaccionPago: String,
    val comprador: CompradorLigero,
    val idLote: Long,
    val nombreProducto: String,
    val cantidadUnidades: Int,
    val subtotalPagado: Int,
    val comisionPagada: Int,
    val montoTotalPagado: Int,
    val idProveedor: Long,
    val nombreComercio: String,
    val direccionExactaRetiro: String,
    val coordenadasGpsRetiro: String,
    val horarioValidoRetiro: String,
    val kgCo2Evitado: Double,
    val requiereVerificacionMayorEdad: Boolean,
    val estadoVoucher: EstadoVoucher = EstadoVoucher.PAGADO_LISTO_RETIRO,
    val fechaEmisionMillis: Long,
    val fechaEntregaConfirmadaMillis: Long? = null
)

// 6. Métricas de Ventas e Impacto Ambiental (RF-07 y RF-17)
data class ResumenImpactoMetricas(
    val totalKgCo2Evitado: Double,
    val equivalenciaArbolesPlantados: Int,
    val totalLotesVendidosOComprados: Int,
    val montoLiquidoTotalAcumulado: Int = 0
)

// 7. Notificaciones Push Comerciales (RF-18)
data class AlertaNotificacion(
    val idNotificacion: Long,
    val idLoteAsociado: Long,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoNotificacionPush,
    val timestampEnvioMillis: Long
)