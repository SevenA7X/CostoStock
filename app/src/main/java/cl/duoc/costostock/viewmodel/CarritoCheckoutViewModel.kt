package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CarritoCheckoutUiState(
    val idReserva: String = "RES-2026-9912",
    val compradorNombre: String = "",
    val compradorCorreo: String = "",
    val compradorTelefono: String = "",
    val subtotalProductos: Int = 42980,
    val tiempoRestanteSegundos: Int = 600, // RF-12: 10 minutos
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
        iniciarTemporizadorReserva()
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
        _uiState.value = _uiState.value.copy(
            compradorNombre = nuevoNombre,
            errorNombre = error
        )
        validarFormulario()
    }

    fun onCorreoChanged(nuevoCorreo: String) {
        val error = validarCorreo(nuevoCorreo)
        _uiState.value = _uiState.value.copy(
            compradorCorreo = nuevoCorreo,
            errorCorreo = error
        )
        validarFormulario()
    }

    fun onTelefonoChanged(nuevoTelefono: String) {
        val error = validarTelefono(nuevoTelefono)
        _uiState.value = _uiState.value.copy(
            compradorTelefono = nuevoTelefono,
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
            !correo.matches(regexCorreo) -> "Formato de correo inválido."
            else -> null
        }
    }

    private fun validarTelefono(telefono: String): String? {
        val soloNumeros = "^[0-9]+$".toRegex()
        return when {
            telefono.trim().isEmpty() -> "El teléfono es obligatorio."
            !telefono.matches(soloNumeros) -> "El teléfono debe contener solo dígitos numéricos."
            telefono.length < 8 -> "Debe tener al menos 8 dígitos."
            else -> null
        }
    }

    private fun validarFormulario() {
        val state = _uiState.value
        val esValido = state.errorNombre == null && state.compradorNombre.trim().isNotEmpty() &&
                state.errorCorreo == null && validarCorreo(state.compradorCorreo) == null &&
                state.errorTelefono == null && validarTelefono(state.compradorTelefono) == null &&
                !state.reservaExpirada

        _uiState.value = _uiState.value.copy(esFormularioValido = esValido)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}