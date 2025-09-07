package com.universalmedialibrary.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsRepository @Inject constructor(
    private val context: Context
) {
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private val API_SETTINGS_KEY = stringPreferencesKey("api_settings")
        private val READER_SETTINGS_KEY = stringPreferencesKey("reader_settings") 
        private val SECURITY_SETTINGS_KEY = stringPreferencesKey("security_settings")
        private val GENERAL_SETTINGS_KEY = stringPreferencesKey("general_settings")
    }

    val apiSettings: Flow<ApiSettings> = context.dataStore.data.map { preferences ->
        preferences[API_SETTINGS_KEY]?.let { jsonString ->
            try {
                json.decodeFromString<ApiSettings>(jsonString)
            } catch (e: Exception) {
                ApiSettings() // Return default if parsing fails
            }
        } ?: ApiSettings()
    }

    val readerSettings: Flow<ReaderSettings> = context.dataStore.data.map { preferences ->
        preferences[READER_SETTINGS_KEY]?.let { jsonString ->
            try {
                json.decodeFromString<ReaderSettings>(jsonString)
            } catch (e: Exception) {
                ReaderSettings() // Return default if parsing fails
            }
        } ?: ReaderSettings()
    }

    val securitySettings: Flow<SecuritySettings> = context.dataStore.data.map { preferences ->
        preferences[SECURITY_SETTINGS_KEY]?.let { jsonString ->
            try {
                json.decodeFromString<SecuritySettings>(jsonString)
            } catch (e: Exception) {
                SecuritySettings() // Return default if parsing fails
            }
        } ?: SecuritySettings()
    }

    val generalSettings: Flow<GeneralSettings> = context.dataStore.data.map { preferences ->
        preferences[GENERAL_SETTINGS_KEY]?.let { jsonString ->
            try {
                json.decodeFromString<GeneralSettings>(jsonString)
            } catch (e: Exception) {
                GeneralSettings() // Return default if parsing fails
            }
        } ?: GeneralSettings()
    }

    suspend fun updateApiSettings(settings: ApiSettings) {
        context.dataStore.edit { preferences ->
            preferences[API_SETTINGS_KEY] = json.encodeToString(ApiSettings.serializer(), settings)
        }
    }

    suspend fun updateReaderSettings(settings: ReaderSettings) {
        context.dataStore.edit { preferences ->
            preferences[READER_SETTINGS_KEY] = json.encodeToString(ReaderSettings.serializer(), settings)
        }
    }

    suspend fun updateSecuritySettings(settings: SecuritySettings) {
        context.dataStore.edit { preferences ->
            preferences[SECURITY_SETTINGS_KEY] = json.encodeToString(SecuritySettings.serializer(), settings)
        }
    }

    suspend fun updateGeneralSettings(settings: GeneralSettings) {
        context.dataStore.edit { preferences ->
            preferences[GENERAL_SETTINGS_KEY] = json.encodeToString(GeneralSettings.serializer(), settings)
        }
    }

    // Convenience methods for updating specific API settings
    suspend fun updateBookApiSettings(bookApis: BookApiSettings) {
        val current = apiSettings.map { it }.let { flow ->
            // Get current value - this is a simplified approach
            // In production, you might want to use a different pattern
            try {
                context.dataStore.data.map { it[API_SETTINGS_KEY] }.let { jsonFlow ->
                    val jsonString = context.dataStore.data.map { it[API_SETTINGS_KEY] }.toString()
                    json.decodeFromString<ApiSettings>(jsonString).copy(bookApis = bookApis)
                }
            } catch (e: Exception) {
                ApiSettings(bookApis = bookApis)
            }
        }
    }

    // Helper method to get current settings synchronously (use with caution)
    suspend fun getCurrentApiSettings(): ApiSettings {
        val preferences = context.dataStore.data.map { it[API_SETTINGS_KEY] }
        return try {
            val jsonString = preferences.toString()
            if (jsonString.isNotEmpty()) {
                json.decodeFromString<ApiSettings>(jsonString)
            } else {
                ApiSettings()
            }
        } catch (e: Exception) {
            ApiSettings()
        }
    }
}