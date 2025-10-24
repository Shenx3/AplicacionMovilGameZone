package com.example.gamezone.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Nombre del DataStore para las preferencias de fotos
val Context.photoDataStore by preferencesDataStore(name = "photo_prefs")
private val KEY_PROFILE_PHOTO_URI = stringPreferencesKey("profile_photo_uri")

object PhotoPrefsRepo {
    /**
     * Devuelve el Flow del Uri guardado (como String).
     */
    fun photoUriStringFlow(context: Context): Flow<String?> =
        context.photoDataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { it[KEY_PROFILE_PHOTO_URI] }

    /**
     * Guarda la Uri de la foto de perfil como String.
     */
    suspend fun setPhotoUriString(context: Context, uriString: String?) {
        context.photoDataStore.edit { preferences ->
            if (uriString != null) {
                preferences[KEY_PROFILE_PHOTO_URI] = uriString
            } else {
                preferences.remove(KEY_PROFILE_PHOTO_URI)
            }
        }
    }
}