package cl.duoc.costostock.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // apunta al localhost de su PC desde el emulador Android
    private const val BASE_URL_TRANSACCIONES = "http://localhost:8081/"

    val transaccionesApi: TransaccionesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_TRANSACCIONES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransaccionesApiService::class.java)
    }
}