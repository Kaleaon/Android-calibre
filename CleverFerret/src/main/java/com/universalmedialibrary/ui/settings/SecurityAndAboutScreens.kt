package com.universalmedialibrary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.universalmedialibrary.data.settings.SecuritySettings
import com.universalmedialibrary.data.settings.GeneralSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val securitySettings by viewModel.securitySettings.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Secure your reading library and personal data",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SecurityOptionsSection(
                    settings = securitySettings,
                    onSettingsChange = viewModel::updateSecuritySettings
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val generalSettings by viewModel.generalSettings.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AboutSection()
            }
            
            item {
                PreferencesSection(
                    settings = generalSettings,
                    onSettingsChange = viewModel::updateGeneralSettings
                )
            }
        }
    }
}

@Composable
fun SecurityOptionsSection(
    settings: SecuritySettings,
    onSettingsChange: (SecuritySettings) -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("App Protection") {
            SwitchSetting(
                title = "Password Protection",
                subtitle = "Require password to open the app",
                checked = settings.passwordProtectionEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        showPasswordDialog = true
                    } else {
                        onSettingsChange(settings.copy(passwordProtectionEnabled = false, passwordHash = ""))
                    }
                }
            )
            
            if (settings.passwordProtectionEnabled) {
                SwitchSetting(
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    checked = settings.biometricEnabled,
                    onCheckedChange = { onSettingsChange(settings.copy(biometricEnabled = it)) }
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-lock Timeout",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${settings.autoLockTimeout / 60000} minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { /* Show timeout picker */ }
                    ) {
                        Text("Change")
                    }
                }
            }
        }
        
        SettingsGroupCard("Privacy") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy Notice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All your reading data and settings are stored locally on your device. No personal information is sent to external servers without your explicit consent.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
    
    if (showPasswordDialog) {
        PasswordSetupDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordSet = { password ->
                // In a real implementation, hash the password securely
                val hashedPassword = password.hashCode().toString() // Simplified for demo
                onSettingsChange(settings.copy(
                    passwordProtectionEnabled = true,
                    passwordHash = hashedPassword
                ))
                showPasswordDialog = false
            }
        )
    }
}

@Composable
fun AboutSection() {
    SettingsGroupCard("CleverFerret") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Universal Media Library",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "A comprehensive media library application supporting books, audiobooks, comics, movies, TV shows, and music with advanced reading features and metadata management.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PreferencesSection(
    settings: GeneralSettings,
    onSettingsChange: (GeneralSettings) -> Unit
) {
    SettingsGroupCard("General Preferences") {
        SwitchSetting(
            title = "Crash Reporting",
            subtitle = "Help improve the app by sending crash reports",
            checked = settings.crashReportingEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(crashReportingEnabled = it)) }
        )
        
        SwitchSetting(
            title = "Notifications",
            subtitle = "Reading reminders and updates",
            checked = settings.notificationsEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(notificationsEnabled = it)) }
        )
        
        SwitchSetting(
            title = "Import on Startup",
            subtitle = "Automatically check for new media files",
            checked = settings.importOnStartup,
            onCheckedChange = { onSettingsChange(settings.copy(importOnStartup = it)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordSetupDialog(
    onDismiss: () -> Unit,
    onPasswordSet: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        errorMessage = ""
                    },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        password.length < 4 -> errorMessage = "Password must be at least 4 characters"
                        password != confirmPassword -> errorMessage = "Passwords do not match"
                        else -> onPasswordSet(password)
                    }
                },
                enabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                Text("Set Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}