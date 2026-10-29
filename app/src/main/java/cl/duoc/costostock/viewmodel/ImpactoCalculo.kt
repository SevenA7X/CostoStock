package cl.duoc.costostock.viewmodel

import cl.duoc.costostock.data.room.VoucherEntity
import cl.duoc.costostock.model.EstadoVoucher

object FactoresImpacto {
    const val KG_CO2_POR_ARBOL = 21.0

    const val KG_CO2_POR_KM_AUTO = 0.25
}
enum class NivelImpacto(val titulo: String, val desdeKg: Double) {
    SEMILLA("Semilla", 0.0),
    BROTE("Brote", 5.0),
    ARBOL("Árbol", FactoresImpacto.KG_CO2_POR_ARBOL),
    BOSQUE("Bosque", FactoresImpacto.KG_CO2_POR_ARBOL * 5);

    companion object {
        fun paraKg(kg: Double): NivelImpacto = entries.lastOrNull { kg >= it.desdeKg } ?: SEMILLA
    }
}

data class ImpactoUiState(
    val totalKgCo2Evitado: Double = 0.0,
    val arbolesEquivalentes: Double = 0.0,
    val kmAutoEquivalentes: Double = 0.0,
    val lotesSalvados: Int = 0,
    val comerciosApoyados: Int = 0,
    val totalPagado: Int = 0,
    val nivel: NivelImpacto = NivelImpacto.SEMILLA,
    val progresoSiguienteArbol: Float = 0f,
    val kgParaSiguienteArbol: Double = FactoresImpacto.KG_CO2_POR_ARBOL
)

fun calcularImpacto(vouchers: List<VoucherEntity>): ImpactoUiState {
    val validos = vouchers.filter { it.estadoVoucher != EstadoVoucher.EXPIRADO_CANCELADO.name }
    val kg = validos.sumOf { it.kgCo2Evitado }
    val kgEnCicloActual = kg % FactoresImpacto.KG_CO2_POR_ARBOL

    return ImpactoUiState(
        totalKgCo2Evitado = kg,
        arbolesEquivalentes = kg / FactoresImpacto.KG_CO2_POR_ARBOL,
        kmAutoEquivalentes = kg / FactoresImpacto.KG_CO2_POR_KM_AUTO,
        lotesSalvados = validos.size,
        comerciosApoyados = validos.map { it.nombreComercio }.distinct().size,
        totalPagado = validos.sumOf { it.montoTotalPagado },
        nivel = NivelImpacto.paraKg(kg),
        progresoSiguienteArbol = (kgEnCicloActual / FactoresImpacto.KG_CO2_POR_ARBOL).toFloat(),
        kgParaSiguienteArbol = FactoresImpacto.KG_CO2_POR_ARBOL - kgEnCicloActual
    )
}