package com.example.gamezone.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    // Buscar por email O nombre de usuario para el login
    @Query("SELECT * FROM users WHERE email = :identifier OR nombreUsuario = :identifier LIMIT 1")
    suspend fun findUserByIdentifier(identifier: String): User?

    // Buscar por ID (se usa para cargar el perfil del usuario logueado)
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?
}
