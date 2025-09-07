package com.universalmedialibrary.data.settings

import kotlinx.serialization.Serializable

/**
 * Settings for reader visual customization and behavior
 */

@Serializable
data class ReaderSettings(
    // Visual controls
    val fontSize: Float = 16f,
    val lineSpacing: Float = 1.5f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val textColor: Int = 0xFF000000.toInt(), // Black
    val backgroundColor: Int = 0xFFFFFFFF.toInt(), // White
    val fadeEdgeEnabled: Boolean = true,
    val margins: Float = 16f,
    
    // Theme settings
    val isDarkMode: Boolean = false,
    val theme: String = "System", // "Light", "Dark", "System"
    
    // Layout and paging
    val isJustified: Boolean = false,
    val columnsCount: Int = 1,
    val pageAnimation: String = "Slide", // "Slide", "Fade", "None"
    
    // Auto-scroll settings
    val autoScrollEnabled: Boolean = false,
    val autoScrollSpeed: Float = 30f, // pixels per second
    val autoScrollMode: AutoScrollMode = AutoScrollMode.ROLLING_BLIND,
    
    // Paging controls
    val volumeKeyPaging: Boolean = true,
    val tapToPagingEnabled: Boolean = true,
    val swipeToPageEnabled: Boolean = true,
    val customGestures: Map<String, String> = emptyMap(),
    
    // TTS settings
    val ttsEnabled: Boolean = false,
    val ttsSpeed: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val ttsVoice: String = "",
    
    // Bookmarks and progress
    val autoBookmark: Boolean = true,
    val syncReadingProgress: Boolean = false,
    
    // Accessibility
    val highContrast: Boolean = false,
    val dyslexiaFont: Boolean = false,
    val reduceAnimation: Boolean = false
)

enum class AutoScrollMode {
    ROLLING_BLIND,
    BY_PIXEL,
    BY_LINE
}

@Serializable
data class AppSettings(
    val apiSettings: ApiSettings = ApiSettings(),
    val readerSettings: ReaderSettings = ReaderSettings(),
    val securitySettings: SecuritySettings = SecuritySettings(),
    val generalSettings: GeneralSettings = GeneralSettings()
)

@Serializable
data class SecuritySettings(
    val passwordProtectionEnabled: Boolean = false,
    val passwordHash: String = "",
    val biometricEnabled: Boolean = false,
    val autoLockTimeout: Long = 300000L // 5 minutes in milliseconds
)

@Serializable
data class GeneralSettings(
    val language: String = "System",
    val defaultLibrary: Long? = null,
    val importOnStartup: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val analyticsEnabled: Boolean = false,
    val crashReportingEnabled: Boolean = true
)