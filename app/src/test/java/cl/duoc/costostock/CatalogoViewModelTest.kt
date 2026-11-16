package cl.duoc.costostock

import cl.duoc.costostock.model.CategoriaProducto
import cl.duoc.costostock.model.EstadoLote
import cl.duoc.costostock.model.RadioBusquedaKm
import cl.duoc.costostock.network.LoteOfertaDto
import cl.duoc.costostock.network.TransaccionesApiService
import cl.duoc.costostock.viewmodel.CatalogoViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest : FunSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    fun crearDtoPrueba(
        id: Long = 1L,
        nombre: String = "Pack Galletas",
        categoria: String = "ABARROTES_ENVASADOS",
        precioNormal: Double = 3000.0,
        precioOferta: Double = 1500.0,
        stock: Int = 10,
        finOferta: String? = LocalDateTime.now().plusHours(2).toString(),
        fechaCaducidad: String? = LocalDateTime.now().plusDays(5).toString(),
        esAlcohol: Boolean = false,
        tiendaNombre: String = "Almacén Viña",
        activo: Boolean = true
    ) = LoteOfertaDto(
        id = id,
        nombre = nombre,
        descripcion = "Descripción de prueba",
        categoria = categoria,
        precioNormal = precioNormal,
        precioOferta = precioOferta,
        stock = stock,
        latitud = -33.0245,
        longitud = -71.5518,
        finOferta = finOferta,
        fechaCaducidad = fechaCaducidad,
        kgCo2Evitado = 1.2,
        esAlcohol = esAlcohol,
        tiendaNombre = tiendaNombre,
        activo = activo
    )

    test("Debe cargar el catálogo desde MS-Transacciones y mapear categorías correctamente") {
        val mockApi = mockk<TransaccionesApiService>()
        val listaRespuesta = listOf(
            crearDtoPrueba(id = 1L, nombre = "Pack Galletas", categoria = "ABARROTES_ENVASADOS"),
            crearDtoPrueba(id = 2L, nombre = "Cerveza IPA", categoria = "BEBIDAS_ALCOHOLICAS", esAlcohol = true),
            crearDtoPrueba(id = 3L, nombre = "Arvejas Congeladas", categoria = "CONGELADOS"),
            crearDtoPrueba(id = 4L, nombre = "Ibuprofeno", categoria = "FARMACIA"),
            crearDtoPrueba(id = 5L, nombre = "Ampolleta LED", categoria = "FERRETERIA")
        )
        coEvery { mockApi.obtenerCatalogo(any(), any(), any()) } returns listaRespuesta

        val viewModel = CatalogoViewModel(apiService = mockApi, iniciarCicloTiempoReal = false)

        viewModel.cargando.value shouldBe false
        viewModel.errorRed.value shouldBe null
        viewModel.lotesVisibles.value shouldHaveSize 5
        viewModel.lotesVisibles.value[1].categoria shouldBe CategoriaProducto.BEBIDAS_ALCOHOLICAS
        viewModel.lotesVisibles.value[1].categoria.requiereVerificacionEdad shouldBe true
    }

    test("RF-10 y RF-11: Debe revertir a precio normal si expiró la oferta y ocultar lotes caducados o sin stock") {
        val mockApi = mockk<TransaccionesApiService>()
        val listaRespuesta = listOf(
            crearDtoPrueba(
                id = 1L,
                nombre = "Oferta Activa",
                precioNormal = 5000.0,
                precioOferta = 2500.0,
                finOferta = LocalDateTime.now().plusHours(1).toString()
            ),
            crearDtoPrueba(
                id = 2L,
                nombre = "Oferta Expirada",
                precioNormal = 4000.0,
                precioOferta = 2000.0,
                finOferta = LocalDateTime.now().minusMinutes(10).toString()
            ),
            crearDtoPrueba(
                id = 3L,
                nombre = "Lote Caducado",
                fechaCaducidad = LocalDateTime.now().minusDays(1).toString()
            ),
            crearDtoPrueba(
                id = 4L,
                nombre = "Lote Sin Stock",
                stock = 0
            )
        )
        coEvery { mockApi.obtenerCatalogo(any(), any(), any()) } returns listaRespuesta

        val viewModel = CatalogoViewModel(apiService = mockApi, iniciarCicloTiempoReal = false)

        val visibles = viewModel.lotesVisibles.value
        visibles shouldHaveSize 2

        visibles[0].estadoActual shouldBe EstadoLote.OFERTA_ACTIVA
        visibles[0].precioOfertaFinal shouldBe 2500

        visibles[1].estadoActual shouldBe EstadoLote.PRECIO_NORMAL
        visibles[1].precioOfertaFinal shouldBe 4000
    }

    test("RF-08 y RNF-06: Debe enviar coordenadas GPS y radio seleccionado al servidor") {
        val mockApi = mockk<TransaccionesApiService>()
        coEvery { mockApi.obtenerCatalogo(any(), any(), any()) } returns emptyList()

        val viewModel = CatalogoViewModel(apiService = mockApi, iniciarCicloTiempoReal = false)
        viewModel.actualizarUbicacionUsuario(-33.0245, -71.5518)
        viewModel.cambiarRadioBusqueda(RadioBusquedaKm.RADIO_5_KM)

        viewModel.radioSeleccionado.value shouldBe RadioBusquedaKm.RADIO_5_KM
        coVerify { mockApi.obtenerCatalogo(-33.0245, -71.5518, 5.0) }
    }

    test("Debe filtrar dinámicamente por texto de búsqueda de producto o comercio") {
        val mockApi = mockk<TransaccionesApiService>()
        val listaRespuesta = listOf(
            crearDtoPrueba(id = 1L, nombre = "Cerveza Artesanal", tiendaNombre = "Botillería Puerto"),
            crearDtoPrueba(id = 2L, nombre = "Salsa de Tomate", tiendaNombre = "Minimarket Centro")
        )
        coEvery { mockApi.obtenerCatalogo(any(), any(), any()) } returns listaRespuesta

        val viewModel = CatalogoViewModel(apiService = mockApi, iniciarCicloTiempoReal = false)
        viewModel.actualizarTextoBusqueda("Cerveza")

        viewModel.lotesVisibles.value shouldHaveSize 1
        viewModel.lotesVisibles.value.first().nombreProducto shouldBe "Cerveza Artesanal"

        viewModel.actualizarTextoBusqueda("Minimarket")
        viewModel.lotesVisibles.value shouldHaveSize 1
        viewModel.lotesVisibles.value.first().nombreComercio shouldBe "Minimarket Centro"
    }

    test("Debe capturar errores de red sin cerrar la aplicación y exponer mensaje en errorRed") {
        val mockApi = mockk<TransaccionesApiService>()
        coEvery { mockApi.obtenerCatalogo(any(), any(), any()) } throws RuntimeException("Timeout de conexión")

        val viewModel = CatalogoViewModel(apiService = mockApi, iniciarCicloTiempoReal = false)

        viewModel.cargando.value shouldBe false
        viewModel.lotesVisibles.value.shouldBeEmpty()
        viewModel.errorRed.value shouldBe "Sin conexión con el servidor de catálogo"
    }
    test("Debe manejar valores nulos o categorías desconocidas en LoteOfertaDto e instanciar RetrofitClient") {
        val dtoConNulos = crearDtoPrueba(
            id = 99L,
            categoria = "OTRA_CATEGORIA",
            finOferta = null,
            fechaCaducidad = null
        )
        val modelo = dtoConNulos.toDomainModel()

        modelo.categoria shouldBe CategoriaProducto.ABARROTES_ENVASADOS
        cl.duoc.costostock.network.RetrofitClient.transaccionesApi.let { api ->
            (api != null) shouldBe true
        }
    }
})