package com.example.gamezone.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var BASE_URL = "http://192.168.100.53:8080/"

    // Cliente HTTP
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(10, TimeUnit.SECONDS) // 10s
        .readTimeout(10, TimeUnit.SECONDS)    // 10s
        .build()

    private fun buildRetrofitInstance(baseUrl: String): GameZoneApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameZoneApiService::class.java)
    }

    @Volatile
    private var _instance: GameZoneApiService = buildRetrofitInstance(BASE_URL)

    val instance: GameZoneApiService
        get() = _instance

    val currentBaseUrl: String
        get() = BASE_URL


    // Actualiza la URL base y reconstruye la instancia de GameZoneApiService.

    fun updateBaseUrl(newIpAndPort: String) {
        val newUrl = if (newIpAndPort.startsWith("http", ignoreCase = true)) {
            newIpAndPort
        } else {
            "http://$newIpAndPort"
        }
        val finalUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"

        if (BASE_URL != finalUrl) {
            BASE_URL = finalUrl
            synchronized(this) {
                _instance = buildRetrofitInstance(BASE_URL)
            }
        }
    }
}