package cl.duoc.costostock.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import cl.duoc.costostock.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var iniciarAnimacion by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "AnimacionAlpha"
    )

    LaunchedEffect(key1 = true) {
        iniciarAnimacion = true
        delay(2000)
        navController.navigate("auth_menu") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Código hexadecimal aproximado al fondo crema de su logo.
            // Si hace su logo transparente (.png), puede cambiar esto a MaterialTheme.colorScheme.background
            .background(Color(0xFFF6EAE0)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logocostostockoficial),
            contentDescription = "Logo Oficial CostoStock",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.85f) // El logo ocupará el 85% del ancho de la pantalla
                .alpha(alphaAnim)
        )
    }
}