package com.example.gamezone.data

// Representa la tabla de usuarios en la base de datos, combinando credenciales y datos de perfil (incluida la foto).

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Datos del registro
    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    val nombreUsuario: String,
    val contrasena: String,
    val generoFavorito: String,

    // Datos del perfil (incluye la URI de la foto persistente)
    val profilePhotoUri: String? = null
)
