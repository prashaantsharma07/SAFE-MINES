# Room SQLite rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.** { *; }
