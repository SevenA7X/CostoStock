package cl.duoc.costostock.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // Simulación de base de datos local (Correo -> Contraseña)
    private val usuariosRegistrados = mutableMapOf<String, String>()

    init {
        // Usuario administrador por defecto para pruebas
        usuariosRegistrados["test@duocuc.cl"] = "123456"
    }

    /**
     * Registra un usuario con correo y contraseña.
     */
    fun registrarUsuario(email: String, contrasena: String): Boolean {
        val correoNormalizado = email.trim().lowercase()

        if (usuariosRegistrados.containsKey(correoNormalizado)) {
            return false // El usuario ya existe
        }

        usuariosRegistrados[correoNormalizado] = contrasena
        return true // Registro exitoso
    }

    /**
     * Sobrecarga con 2 parámetros para llamadas a 'registrar'.
     */
    fun registrar(email: String, contrasena: String): Boolean {
        return registrarUsuario(email, contrasena)
    }

    /**
     * Sobrecarga con 3 parámetros (Nombre/Razón Social, Correo, Contraseña) para llamadas desde registro.
     */
    fun registrar(nombreORazonSocial: String, email: String, contrasena: String): Boolean {
        return registrarUsuario(email, contrasena)
    }

    /**
     * Valida las credenciales ingresadas contra los registros existentes.
     */
    fun login(email: String, contrasena: String): Boolean {
        val correoNormalizado = email.trim().lowercase()
        val passwordGuardada = usuariosRegistrados[correoNormalizado]

        return passwordGuardada != null && passwordGuardada == contrasena
    }
}