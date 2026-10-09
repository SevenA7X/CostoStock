package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.costostock.model.CompradorLigero
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CarritoState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val tiempoRestanteSegundos: Int = 600,
    val reservaExpirada: Boolean = false,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorTelefono: String? = null,
    val esValido: Boolean = false
)

class CarritoCheckoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoState())
    val uiState: StateFlow<CarritoState> = _uiState.asStateFlow()

    init {
        iniciarTemporizadorReserva()
    }

    private fun iniciarTemporizadorReserva() {
        viewModelScope.launch {
            while (_uiState.value.tiempoRestanteSegundos > 0) {
                delay(1000L)
                _uiState.value = _uiState.value.copy(
                    tiempoRestanteSegundos = _uiState.value.tiempoRestanteSegundos - 1
                )
            }
            _uiState.value = _uiState.value.copy(reservaExpirada = true)
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        val error = if (nuevoNombre.isBlank()) "El nombre es obligatorio" else null
        _uiState.value = _uiState.value.copy(nombre = nuevoNombre, errorNombre = error)
        validarFormulario()
    }

    fun onCorreoChange(nuevoCorreo: String) {
        val error = if (!nuevoCorreo.contains("@") || !nuevoCorreo.contains(".")) "Correo inválido" else null
        _uiState.value = _uiState.value.copy(correo = nuevoCorreo, errorCorreo = error)
        validarFormulario()
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        val error = if (nuevoTelefono.length < 8) "Ingrese un teléfono válido" else null
        _uiState.value = _uiState.value.copy(telefono = nuevoTelefono, errorTelefono = error)
        validarFormulario()
    }

    private fun validarFormulario() {
        val state = _uiState.value
        val esValido = state.nombre.isNotBlank() &&
                state.correo.contains("@") &&
                state.telefono.length >= 8 &&
                !state.reservaExpirada

        _uiState.value = _uiState.value.copy(esValido = esValido)
    }

    fun obtenerDatosComprador(): Map<String, String> {
        val state = _uiState.value
        return mapOf(
            "nombre" to state.nombre,
            "correo" to state.correo,
            "telefono" to state.telefono
        )
    }
}