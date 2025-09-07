package com.universalmedialibrary

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Basic instrumentation tests to verify the app starts and core functionality works.
 * These are smoke tests to catch major integration issues.
 */
@RunWith(AndroidJUnit4::class)
class AppSmokeTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun appStartsSuccessfully() {
        // This test verifies that the app can start without crashing
        activityRule.scenario.onActivity { activity ->
            assertThat(activity).isNotNull()
        }
    }

    @Test
    fun appContextIsCorrect() {
        // Context of the app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertThat(appContext.packageName).isEqualTo("com.universalmedialibrary")
    }

    @Test
    fun mainActivityLoadsWithoutCrash() {
        // Verify the main activity loads and is in a valid state
        activityRule.scenario.onActivity { activity ->
            assertThat(activity.javaClass.simpleName).isEqualTo("MainActivity")
        }
    }
}