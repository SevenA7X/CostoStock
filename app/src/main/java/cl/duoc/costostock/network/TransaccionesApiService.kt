package cl.duoc.costostock.network

import retrofit2.http.GET
import retrofit2.http.Query

interface TransaccionesApiService {
    @GET("api/catalogo")
    suspend fun obtenerCatalogo(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("radioKm") radioKm: Double
    ): List<LoteOfertaDto>
}