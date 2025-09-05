package com.universalmedialibrary

import android.app.Activity
import android.os.Bundle

/**
 * A minimal Activity class to satisfy the manifest requirements for the build process.
 * The full UI will be implemented in a later phase.
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We can set a simple content view later if needed for UI tests,
        // but for now, this is enough to make the app buildable.
    }
}
