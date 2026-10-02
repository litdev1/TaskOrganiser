# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Kotlinx Serialization rules
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclasseswithmembernames class * {
    @kotlinx.serialization.Serializable <init>(...);
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
    synthetic <methods>;
}

# Keep TaskOrganiser data models and actions package for serialization
-keep class com.litdev.taskorganiser.actions.** { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
