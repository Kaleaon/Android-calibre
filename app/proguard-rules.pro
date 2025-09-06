# Add project specific ProGuard rules here.
# By default, the shrinker preserves all classes that have a public entry point (such as activities, services, etc.).
# You can add additional rules here for specific classes that you want to keep.
#
# For more information, see the Android documentation:
# https://developer.android.com/studio/build/shrink-code

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Compose related classes for debugging
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep AndroidX navigation
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**
