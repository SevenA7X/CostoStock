package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.costostock.data.room.CostoStockDataBase
import cl.duoc.costostock.viewmodel.ImpactoViewModel
import cl.duoc.costostock.viewmodel.ModulosViewModelFactory
import cl.duoc.costostock.viewmodel.VouchersViewModel
import cl.duoc.costostock.viewmodel.AuthViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String, val title: String, val icon: String) {
    object Ofertas : Screen("ofertas", "Ofertas", "🛒")
    object Mapa : Screen("mapa", "Mapa", "📍")
    object Vouchers : Screen("vouchers", "Vouchers", "🎟️")
    object Impacto : Screen("impacto", "Impacto", "🌱")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { CostoStockDataBase.getDatabase(context) }
    val viewModelFactory = remember { ModulosViewModelFactory(database.voucherDao(), database.impactoDao()) }

    // Instancia compartida del ViewModel de Autenticación
    val authViewModel: AuthViewModel = viewModel()

    val items = listOf(
        Screen.Ofertas,
        Screen.Mapa,
        Screen.Vouchers,
        Screen.Impacto
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Se añaden las rutas de inicio de sesión y registro para ocultar la barra inferior
    val rutasSinBarraInferior = listOf("splash", "auth_menu", "iniciar_sesion", "registro")
    val mostrarBarraInferior = currentRoute !in rutasSinBarraInferior

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
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
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable(
                route = "splash",
                exitTransition = { fadeOut(animationSpec = tween(800)) }
            ) {
                SplashScreen(navController = navController)
            }

            composable(
                route = "auth_menu",
                enterTransition = { fadeIn(animationSpec = tween(800)) }
            ) {
                AuthMenuScreen(navController = navController)
            }

            // --- Nuevas Rutas de Autenticación ---
            composable("iniciar_sesion") {
                LoginScreen(navController = navController, viewModel = authViewModel)
            }

            composable("registro") {
                RegistroScreen(navController = navController, viewModel = authViewModel)
            }
            // -------------------------------------

            composable(Screen.Ofertas.route) {
                CatalogoScreen(viewModel = viewModel(), navController=navController)
            }

            composable(Screen.Mapa.route) {
                MapaOfertasScreen(viewModel = viewModel(), navController = navController)
            }

            composable(Screen.Vouchers.route) {
                val vouchersViewModel: VouchersViewModel = viewModel(factory = viewModelFactory)
                VouchersScreen(viewModel = vouchersViewModel, navController = navController)
            }

            composable(Screen.Impacto.route) {
                val impactoViewModel: ImpactoViewModel = viewModel(factory = viewModelFactory)
                ImpactoScreen(viewModel = impactoViewModel)
            }

            composable("checkout") {
                CarritoCheckoutScreen(viewModel = viewModel(), navController = navController)
            }

            composable("publicar_lote") {
                PublicarLoteScreen(viewModel = viewModel(), navController = navController)
            }

            composable("validar_voucher") {
                ValidarVoucherScreen(viewModel = viewModel(), navController = navController)
            }

            composable(
                route = "detalle_oferta/{loteId}",
                arguments = listOf(navArgument("loteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val loteId = backStackEntry.arguments?.getLong("loteId") ?: 0L
                DetalleOfertaScreen(
                    viewModel = viewModel(),
                    idLoteSeleccionado = loteId,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun PantallaModuloPlaceholder(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}