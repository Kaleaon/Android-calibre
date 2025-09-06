package com.universalmedialibrary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The main [Application] class for the app.
 *
 * The `@HiltAndroidApp` annotation triggers Hilt's code generation, including a
 * base class for the application that serves as the application-level dependency container.
 */
@HiltAndroidApp
class MainApplication : Application()
