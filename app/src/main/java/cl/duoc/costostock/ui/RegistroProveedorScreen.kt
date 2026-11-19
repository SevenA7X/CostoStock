package cl.duoc.costostock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroProveedorScreen(navController: NavController, viewModel: AuthViewModel) {
    var rutEmpresa by remember { mutableStateOf("") }
    var razonSocial by remember { mutableStateOf("") }
    var nombreFantasia by remember { mutableStateOf("") }
    var direccionLocal by remember { mutableStateOf("") }
    var telefonoContacto by remember { mutableStateOf("") }
    var folioPatente by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var errorMensaje by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Comercio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Formulario de Verificación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Ingresa los datos tributarios de tu negocio para activar tu cuenta de proveedor.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // 1. RUT Empresa
            OutlinedTextField(
                value = rutEmpresa,
                onValueChange = {
                    rutEmpresa = it
                    errorMensaje = ""
                },
                label = { Text("RUT Empresa / Comerciante") },
                placeholder = { Text("ej. 76.123.456-K") },
                leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Razón Social
            OutlinedTextField(
                value = razonSocial,
                onValueChange = {
                    razonSocial = it
                    errorMensaje = ""
                },
                label = { Text("Razón Social (Legal)") },
                placeholder = { Text("ej. Comercial El Puerto SpA") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Nombre Fantasía
            OutlinedTextField(
                value = nombreFantasia,
                onValueChange = { nombreFantasia = it },
                label = { Text("Nombre del Local / Fantasía") },
                placeholder = { Text("ej. Distribuidora El Puerto") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Dirección del Local
            OutlinedTextField(
                value = direccionLocal,
                onValueChange = { direccionLocal = it },
                label = { Text("Dirección Física de Retiro") },
                placeholder = { Text("Av. Brasil 456, Valparaíso") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Teléfono
            OutlinedTextField(
                value = telefonoContacto,
                onValueChange = { telefonoContacto = it },
                label = { Text("Teléfono de Contacto") },
                placeholder = { Text("+56 9 1234 5678") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Folio Patente / SII
            OutlinedTextField(
                value = folioPatente,
                onValueChange = { folioPatente = it },
                label = { Text("N° de Patente Comercial o Folio SII") },
                placeholder = { Text("ej. PAT-2024-8890") },
                leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 7. Correo Electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorMensaje = ""
                },
                label = { Text("Correo Electrónico Comercial") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 8. Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    errorMensaje = ""
                },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Envío
            Button(
                onClick = {
                    if (rutEmpresa.isBlank() || razonSocial.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                        errorMensaje = "Por favor completa los campos obligatorios (RUT, Razón Social, Correo y Contraseña)."
                    } else if (contrasena.length < 3) {
                        errorMensaje = "La contraseña debe tener al menos 3 caracteres."
                    } else {
                        // Persiste la cuenta en AuthViewModel
                        viewModel.registrar(razonSocial.trim(), correo.trim(), contrasena)
                        mensajeExito = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Enviar Solicitud de Verificación")
            }

            if (mensajeExito) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "¡Solicitud Registrada con Éxito!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tu negocio ha sido ingresado al sistema. Ya puedes iniciar sesión con tu cuenta de comercio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                navController.navigate("auth_menu") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Volver al Inicio")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}