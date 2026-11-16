package cl.duoc.costostock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import cl.duoc.costostock.network.LoteOfertaDto
import cl.duoc.costostock.network.TransaccionesApiService
import cl.duoc.costostock.ui.CatalogoScreen
import cl.duoc.costostock.viewmodel.CatalogoViewModel
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class CatalogoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun catalogoScreen_renderizaListaDeProductosCorrectamente() {
        // Fake de la API para renderizado determinista en el emulador
        val fakeApiService = object : TransaccionesApiService {
            override suspend fun obtenerCatalogo(
                lat: Double?,
                lon: Double?,
                radioKm: Double
            ): List<LoteOfertaDto> {
                return listOf(
                    LoteOfertaDto(
                        id = 10L,
                        nombre = "Pack Galletas Integrales UI",
                        descripcion = "Caja sellada próxima a vencer",
                        categoria = "ABARROTES_ENVASADOS",
                        precioNormal = 3000.0,
                        precioOferta = 1500.0,
                        stock = 12,
                        latitud = -33.0245,
                        longitud = -71.5518,
                        finOferta = LocalDateTime.now().plusHours(3).toString(),
                        fechaCaducidad = LocalDateTime.now().plusDays(10).toString(),
                        kgCo2Evitado = 0.8,
                        esAlcohol = false,
                        tiendaNombre = "Almacén Viña",
                        activo = true
                    )
                )
            }
        }

        val viewModel = CatalogoViewModel(
            apiService = fakeApiService,
            iniciarCicloTiempoReal = false
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            CatalogoScreen(viewModel = viewModel, navController = navController)
        }

        // Validamos que los elementos principales y la tarjeta del producto se dibujen en pantalla
        composeTestRule.onNodeWithText("Buscar productos o comercios...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pack Galletas Integrales UI").assertIsDisplayed()
        composeTestRule.onNodeWithText("$1500").assertIsDisplayed()
        composeTestRule.onNodeWithText("$3000").assertIsDisplayed()
    }
}