plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("jacoco")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
}

android {
    namespace = "com.universalmedialibrary"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.universalmedialibrary"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        // Debug signing - use default Android debug keystore
        getByName("debug") {
            // Android Gradle Plugin handles debug signing automatically
            // Uses ~/.android/debug.keystore with default credentials
        }
        
        // Release signing configuration
        // Keystore should be stored securely, not in repository
        // For CI/CD: use GitHub Secrets to provide environment variables
        // For local development: set environment variables or use gradle.properties
        val keystoreFile = System.getenv("KEYSTORE_FILE")?.let { file(it) }
            ?: file("release.keystore") // Local fallback (not in repo)
        val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
        val keyAlias = System.getenv("KEY_ALIAS") ?: "androidreleasekey"
        val keyPassword = System.getenv("KEY_PASSWORD") ?: keystorePassword
        
        // Only create release signing config if all required properties are available
        if (keystoreFile?.exists() == true && !keystorePassword.isNullOrEmpty()) {
            create("release") {
                storeFile = keystoreFile
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
                
                // Validate signing configuration
                println("✓ Release signing configured with keystore: ${keystoreFile.name}")
                println("✓ Using key alias: $keyAlias")
            }
        } else {
            println("⚠️  Release signing not configured - missing keystore or credentials")
            println("   For production builds, ensure KEYSTORE_FILE, KEYSTORE_PASSWORD are set")
            println("   Keystore path: ${keystoreFile?.absolutePath ?: "not specified"}")
            println("   Password provided: ${if (keystorePassword.isNullOrEmpty()) "no" else "yes"}")
        }
    }

    buildTypes {
        debug {
            // Use default debug signing config
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Configure release signing
            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig != null) {
                signingConfig = releaseSigningConfig
                println("✓ Release build will be signed with release keystore")
            } else {
                // For development builds without release keystore, use debug signing
                // This allows local development without requiring production keystore
                signingConfig = signingConfigs.getByName("debug")
                applicationIdSuffix = ".unsigned"
                versionNameSuffix = "-unsigned"
                println("⚠️  Release build using debug signing (unsigned for production)")
                println("   This APK should not be distributed to users")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // Required for Robolectric to access resources
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Audio/Video player
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")
    implementation("androidx.media3:media3-session:1.3.0")

    // Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room for Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // HTML rendering and text processing
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Kotlinx serialization for settings
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // Audio processing for enhanced TTS (placeholder for future Coqui integration)
    implementation("androidx.media:media:1.7.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("org.robolectric:robolectric:4.12.1")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.google.truth:truth:1.4.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// JaCoCo configuration for code coverage
jacoco {
    toolVersion = "0.8.8"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/databinding/**/*.*",
        "**/android/databinding/*Binding.*",
        "**/BR.*",
        "**/Hilt_*.*",
        "**/*_Hilt*.*",
        "**/*Module.*",
        "**/*Dagger*.*",
        "**/*MembersInjector*.*",
        "**/*_Factory*.*",
        "**/*_Provide*Factory*.*",
        "**/di/**"
    )
    
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("**/*.exec", "**/*.ec")
    })
}
