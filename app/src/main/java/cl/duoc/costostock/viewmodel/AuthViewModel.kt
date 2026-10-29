package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // Simulación de base de datos local (Correo -> Contraseña)
    private val usuariosRegistrados = mutableMapOf<String, String>()

    init {
        // Usuario administrador por defecto para agilizar sus pruebas
        usuariosRegistrados["test@duocuc.cl"] = "123456"
    }

    /**
     * Intenta registrar un nuevo usuario en el sistema.
     * @return true si el registro es exitoso, false si el correo ya estaba registrado.
     */
    fun registrarUsuario(email: String, contrasena: String): Boolean {
        val correoNormalizado = email.trim().lowercase()

        if (usuariosRegistrados.containsKey(correoNormalizado)) {
            return false // Rechaza el registro: El usuario ya existe
        }

        usuariosRegistrados[correoNormalizado] = contrasena
        return true // Registro exitoso
    }

    /**
     * Valida las credenciales ingresadas contra los registros existentes.
     * @return true si el correo existe y la contraseña coincide.
     */
    fun login(email: String, contrasena: String): Boolean {
        val correoNormalizado = email.trim().lowercase()
        val passwordGuardada = usuariosRegistrados[correoNormalizado]

        return passwordGuardada != null && passwordGuardada == contrasena
    }
}