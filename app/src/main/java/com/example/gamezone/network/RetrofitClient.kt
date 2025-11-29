package com.example.gamezone.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Si falla mi celular usar la ip de la maquina local
    // 10.0.2.2 es el alias de tu máquina local cuando usas el emulador de Android.
    private const val BASE_URL = "http://192.168.100.53:8080/"

    // Cliente HTTP con Interceptor para debug (útil en Logcat)
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: GameZoneApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameZoneApiService::class.java)
    }
}