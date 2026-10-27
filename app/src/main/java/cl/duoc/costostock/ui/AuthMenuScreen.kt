package cl.duoc.costostock.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.duoc.costostock.R

@Composable
fun AuthMenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logotipo Oficial integrado
        Image(
            painter = painterResource(id = R.drawable.logocostostockoficial),
            contentDescription = "Logo Oficial CostoStock",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ocupa el 80% del ancho para mantener proporción
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Texto simplificado para no redundar con el logo
        Text(
            text = "Bienvenid@",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón Principal: Iniciar Sesión
        Button(
            onClick = { navController.navigate("iniciar_sesion") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Secundario: Registro
        OutlinedButton(
            onClick = { navController.navigate("registro") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Crear Cuenta", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Vía Rápida: Invitado apuntando a su ruta correcta "ofertas"
        TextButton(
            onClick = {
                navController.navigate("ofertas") {
                    popUpTo("auth_menu") { inclusive = true }
                }
            }
        ) {
            Text("Entrar como invitado", fontSize = 16.sp)
        }
    }
}