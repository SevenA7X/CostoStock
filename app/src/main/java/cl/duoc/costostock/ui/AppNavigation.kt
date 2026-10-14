package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val title: String, val icon: String) {
    object Ofertas : Screen("ofertas", "Ofertas", "🛒")
    object Mapa : Screen("mapa", "Mapa", "📍")
    object Vouchers : Screen("vouchers", "Vouchers", "🎟️")
    object Impacto : Screen("impacto", "Impacto", "🌱")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Ofertas,
        Screen.Mapa,
        Screen.Vouchers,
        Screen.Impacto
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Text(screen.icon) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        },
        // BOTON FLOTANTE TEMPORAL PARA VISUALIZAR EL CHECKOUT
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("checkout") }
            ) {
                Text("Pagar")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Ofertas.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            // --- 1. DESTINOS PRINCIPALES (Barra Inferior) ---

            composable(Screen.Ofertas.route) {
                CatalogoScreen(
                    viewModel = viewModel()
                )
            }

            composable(Screen.Mapa.route) {
                PantallaModuloPlaceholder("Módulo Mapa y Geolocalización (Próximamente)")
            }

            composable(Screen.Vouchers.route) {
                ValidarVoucherScreen(
                    navController = navController,
                    viewModel = viewModel()
                )
            }

            composable(Screen.Impacto.route) {
                PantallaModuloPlaceholder("Módulo Impacto Ambiental y Ahorro (Próximamente)")
            }

            // --- 2. DESTINOS SECUNDARIOS (Navegación por botones) ---

            composable("checkout") {
                CarritoCheckoutScreen(
                    viewModel = viewModel()
                )
            }

            composable("publicar_lote") {
                PublicarLoteScreen(
                    viewModel = viewModel(),
                    navController = navController
                )
            }

            composable("captura_foto") {
                CapturaFotoLote(
                    foto = null,
                    error = null,
                    onFotoCapturada = { nuevaFoto ->

                    },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun PantallaModuloPlaceholder(titulo: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}