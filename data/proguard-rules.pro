# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep data layer classes
-keep class com.stepunlock.data.** { *; }

# Keep Room entities and DAOs
-keep class com.stepunlock.data.local.entities.** { *; }
-keep class com.stepunlock.data.local.dao.** { *; }
