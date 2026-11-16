package cl.duoc.costostock.network

import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.model.EstadoLote
import cl.duoc.costostock.model.LoteOferta
import java.time.LocalDateTime
import java.time.ZoneId

data class LoteOfertaDto(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precioNormal: Double,
    val precioOferta: Double,
    val stock: Int,
    val latitud: Double,
    val longitud: Double,
    val finOferta: String?,
    val fechaCaducidad: String?,
    val kgCo2Evitado: Double,
    val esAlcohol: Boolean,
    val tiendaNombre: String,
    val activo: Boolean
) {
    fun toDomainModel(): LoteOferta {
        val finOfertaMillis = parseIsoToMillis(finOferta, System.currentTimeMillis() + 3600000)
        val caducidadMillis = parseIsoToMillis(fechaCaducidad, System.currentTimeMillis() + 86400000)
        val ahora = System.currentTimeMillis()

        val estadoCalculado = when {
            !activo || ahora >= caducidadMillis -> EstadoLote.CADUCADO_BLOQUEADO
            stock <= 0 -> EstadoLote.AGOTADO_BLOQUEADO
            ahora >= finOfertaMillis || precioOferta >= precioNormal -> EstadoLote.PRECIO_NORMAL
            else -> EstadoLote.OFERTA_ACTIVA
        }

        val categoriaEnum = when (categoria.uppercase()) {
            "CONGELADOS" -> CategoriaProducto.CONGELADOS
            "FERRETERIA", "FERRETERÍA" -> CategoriaProducto.FERRETERIA
            "FARMACIA" -> CategoriaProducto.FARMACIA
            "BEBIDAS", "BEBIDAS_ALCOHOLICAS" -> if (esAlcohol) CategoriaProducto.BEBIDAS_ALCOHOLICAS else CategoriaProducto.ABARROTES_ENVASADOS
            else -> CategoriaProducto.ABARROTES_ENVASADOS
        }

        val imagenUrl = when (categoriaEnum) {
            CategoriaProducto.BEBIDAS_ALCOHOLICAS -> "https://http2.mlstatic.com/D_NQ_NP_734077-MLU78178247643_082024-O.webp"
            CategoriaProducto.CONGELADOS -> "https://dojiw2m9tvv09.cloudfront.net/85621/product/arvejas-minuto-verde-1-kg6400.jpg"
            CategoriaProducto.FARMACIA -> "https://profar.cl/media/catalog/product/cache/0ef4825a2b8913160efbbb4d7e84ca00/1/0/102928_1_kngfzyopyljirsym.webp"
            CategoriaProducto.FERRETERIA -> "https://cdnx.jumpseller.com/electra-s-a/image/71338375/resize/430/573?1767351708"
            else -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9LNYvklGy--a9GVKPtk45yNlKH2G-Fpj8RYi59PV_P4g0OsVMN39M26iI&s=10"
        }

        val precioFinalInt = precioOferta.toInt()
        val comision = (precioFinalInt * 0.1).toInt()

        return LoteOferta(
            idLote = id ?: 0L,
            idProveedor = 100L + (id ?: 1L),
            nombreComercio = tiendaNombre,
            nombreProducto = nombre,
            descripcion = descripcion,
            categoria = categoriaEnum,
            urlsFotografiasActuales = listOf(imagenUrl),
            fechaCapturaFotoMillis = ahora,
            stockUnidadesDisponible = stock,
            montoLiquidoProveedor = precioFinalInt - comision,
            comisionCostoStock = comision,
            precioOfertaFinal = precioFinalInt,
            precioNormalSinDescuento = precioNormal.toInt(),
            fechaHoraFinOfertaMillis = finOfertaMillis,
            fechaCaducidadMillis = caducidadMillis,
            kgCo2EvitadoPorUnidad = kgCo2Evitado,
            direccionRetiro = "Sucursal $tiendaNombre",
            referenciaRetiro = "Retiro en tienda",
            horarioRetiro = "09:00 - 20:00",
            latitudRetiro = latitud,
            longitudRetiro = longitud,
            estadoActual = estadoCalculado
        )
    }

    private fun parseIsoToMillis(isoString: String?, fallbackMillis: Long): Long {
        if (isoString.isNullOrBlank()) return fallbackMillis
        return try {
            LocalDateTime.parse(isoString)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            fallbackMillis
        }
    }
}