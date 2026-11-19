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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.costostock.data.room.CostoStockDataBase
import cl.duoc.costostock.viewmodel.AuthViewModel
import cl.duoc.costostock.viewmodel.CarritoCheckoutViewModel
import cl.duoc.costostock.viewmodel.CatalogoViewModel
import cl.duoc.costostock.viewmodel.ImpactoViewModel
import cl.duoc.costostock.viewmodel.ModulosViewModelFactory
import cl.duoc.costostock.viewmodel.ProveedorViewModel
import cl.duoc.costostock.viewmodel.VouchersViewModel

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
    val authViewModel: AuthViewModel = viewModel()

    val items = listOf(Screen.Ofertas, Screen.Mapa, Screen.Vouchers, Screen.Impacto)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mostrarBarraInferior = currentRoute in items.map { it.route }

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
                                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController = navController) }
            composable("auth_menu") { AuthMenuScreen(navController = navController) }
            composable("iniciar_sesion") { LoginScreen(navController = navController, viewModel = authViewModel) }
            composable("registro") { RegistroScreen(navController = navController, viewModel = authViewModel) }
            composable("registro_proveedor") { RegistroProveedorScreen(navController = navController, viewModel = authViewModel) }
            composable("panel_proveedor") { PanelProveedorScreen(navController = navController) }

            composable(Screen.Ofertas.route) {
                CatalogoScreen(viewModel = viewModel<CatalogoViewModel>(), navController = navController)
            }
            composable(Screen.Mapa.route) {
                MapaOfertasScreen(viewModel = viewModel<CatalogoViewModel>(), navController = navController)
            }
            composable(Screen.Vouchers.route) {
                VouchersScreen(viewModel = viewModel<VouchersViewModel>(factory = viewModelFactory), navController = navController)
            }
            composable(Screen.Impacto.route) {
                ImpactoScreen(viewModel = viewModel<ImpactoViewModel>(factory = viewModelFactory))
            }
            composable("checkout") {
                CarritoCheckoutScreen(viewModel = viewModel<CarritoCheckoutViewModel>(), navController = navController)
            }

            composable("publicar_lote") {
                val proveedorViewModel: ProveedorViewModel = viewModel()
                PublicarLoteScreen(navController = navController, viewModel = proveedorViewModel)
            }

            // AQUI ESTABA EL OTRO ERROR (de la foto): Requiere VouchersViewModel, no ProveedorViewModel.
            composable("validar_voucher") {
                ValidarVoucherScreen(navController = navController, viewModel = viewModel<VouchersViewModel>(factory = viewModelFactory))
            }

            composable(
                route = "detalle_oferta/{loteId}",
                arguments = listOf(navArgument("loteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val loteId = backStackEntry.arguments?.getLong("loteId") ?: 0L
                DetalleOfertaScreen(
                    viewModel = viewModel<CatalogoViewModel>(),
                    idLoteSeleccionado = loteId,
                    navController = navController
                )
            }
        }
    }
}