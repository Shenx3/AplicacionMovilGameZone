package com.example.gamezone.data

data class User(
    val id: Long = 0,

    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    val nombreUsuario: String,
    val contrasena: String,
    val generoFavorito: String,

    // Datos del perfil 
    val profilePhotoUri: String? = null
)