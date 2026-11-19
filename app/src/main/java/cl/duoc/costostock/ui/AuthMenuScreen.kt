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
        // Logotipo Oficial
        Image(
            painter = painterResource(id = R.drawable.logocostostockoficial),
            contentDescription = "Logo Oficial CostoStock",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Bienvenid@",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 1. Iniciar Sesión
        Button(
            onClick = { navController.navigate("iniciar_sesion") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Crear Cuenta (Comprador)
        OutlinedButton(
            onClick = { navController.navigate("registro") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Crear Cuenta", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Registro Exclusivo Proveedor / Comercio
        TextButton(
            onClick = { navController.navigate("registro_proveedor") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Tienes un negocio? Regístrate como Comercio", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Modo Invitado
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