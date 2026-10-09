package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemReservaSemilla(
    val id: String,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Int
) {
    val subtotal: Int get() = cantidad * precioUnitario
}

data class ReservaCarritoSemilla(
    val idReserva: String,
    val items: List<ItemReservaSemilla>
) {
    val total: Int get() = items.sumOf { it.subtotal }
}

data class CompradorLigero(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = ""
)

data class CarritoCheckoutUiState(
    val reserva: ReservaCarritoSemilla? = null,
    val comprador: CompradorLigero = CompradorLigero(),
    val tiempoRestanteSegundos: Int = 600,
    val reservaExpirada: Boolean = false,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorTelefono: String? = null,
    val esFormularioValido: Boolean = false
)


class CarritoCheckoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoCheckoutUiState())
    val uiState: StateFlow<CarritoCheckoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        cargarDatosSemillaReserva()
        iniciarTemporizadorReserva()
    }

    private fun cargarDatosSemillaReserva() {
        val reservaSemilla = ReservaCarritoSemilla(
            idReserva = "RES-2026-9912",
            items = listOf(
                ItemReservaSemilla("1", "Polera Algodón Premium", 2, 12990),
                ItemReservaSemilla("2", "Jeans Denim Slim Fit", 1, 29990)
            )
        )
        _uiState.value = _uiState.value.copy(reserva = reservaSemilla)
    }

    private fun iniciarTemporizadorReserva() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.tiempoRestanteSegundos > 0) {
                delay(1000L)
                val nuevoTiempo = _uiState.value.tiempoRestanteSegundos - 1
                _uiState.value = _uiState.value.copy(
                    tiempoRestanteSegundos = nuevoTiempo,
                    reservaExpirada = nuevoTiempo <= 0
                )
                if (nuevoTiempo <= 0) {
                    validarFormulario()
                    break
                }
            }
        }
    }


    fun onNombreChanged(nuevoNombre: String) {
        val error = validarNombre(nuevoNombre)
        val nuevoComprador = _uiState.value.comprador.copy(nombre = nuevoNombre)
        _uiState.value = _uiState.value.copy(
            comprador = nuevoComprador,
            errorNombre = error
        )
        validarFormulario()
    }

    fun onCorreoChanged(nuevoCorreo: String) {
        val error = validarCorreo(nuevoCorreo)
        val nuevoComprador = _uiState.value.comprador.copy(correo = nuevoCorreo)
        _uiState.value = _uiState.value.copy(
            comprador = nuevoComprador,
            errorCorreo = error
        )
        validarFormulario()
    }

    fun onTelefonoChanged(nuevoTelefono: String) {
        val error = validarTelefono(nuevoTelefono)
        val nuevoComprador = _uiState.value.comprador.copy(telefono = nuevoTelefono)
        _uiState.value = _uiState.value.copy(
            comprador = nuevoComprador,
            errorTelefono = error
        )
        validarFormulario()
    }

    private fun validarNombre(nombre: String): String? {
        return if (nombre.trim().isEmpty()) "El nombre es obligatorio." else null
    }

    private fun validarCorreo(correo: String): String? {
        val regexCorreo = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return when {
            correo.trim().isEmpty() -> "El correo es obligatorio."
            !correo.matches(regexCorreo) -> "Ingrese un formato de correo válido (ej. usuario@dominio.com)."
            else -> null
        }
    }

    private fun validarTelefono(telefono: String): String? {
        val soloNumeros = "^[0-9]+$".toRegex()
        return when {
            telefono.trim().isEmpty() -> "El teléfono es obligatorio."
            !telefono.matches(soloNumeros) -> "El teléfono debe contener solo dígitos numéricos."
            telefono.length < 8 -> "El teléfono debe tener al menos 8 dígitos."
            else -> null
        }
    }

    private fun validarFormulario() {
        val state = _uiState.value
        val esValido = state.errorNombre == null && state.nombreValidado(state.comprador.nombre) &&
                state.errorCorreo == null && state.correoValidado(state.comprador.correo) &&
                state.errorTelefono == null && state.telefonoValidado(state.comprador.telefono) &&
                !state.reservaExpirada

        _uiState.value = _uiState.value.copy(esFormularioValido = esValido)
    }

    private fun CarritoCheckoutUiState.nombreValidado(v: String) = v.trim().isNotEmpty()
    private fun CarritoCheckoutUiState.correoValidado(v: String) = validarCorreo(v) == null
    private fun CarritoCheckoutUiState.telefonoValidado(v: String) = validarTelefono(v) == null

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}