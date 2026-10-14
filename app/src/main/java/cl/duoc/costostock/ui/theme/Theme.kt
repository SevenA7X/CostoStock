package cl.duoc.costostock.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1. Restauramos un tema oscuro básico para evitar errores de compilación
private val DarkColorScheme = darkColorScheme(
    primary = NaranjaPrincipal,
    secondary = VerdeExito,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// 2. Su esquema de colores claros corporativos
private val LightColorScheme = lightColorScheme(
    primary = NaranjaPrincipal,
    secondary = VerdeExito,
    background = FondoPastel,
    surface = FondoTarjeta,
    surfaceVariant = FondoTarjeta,
    onPrimary = Color.White,
    onBackground = GrisTextoSecundario,
    onSurface = GrisTextoSecundario
)

@Composable
fun CostostockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 3. Mantenemos el color dinámico desactivado para forzar su diseño
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}