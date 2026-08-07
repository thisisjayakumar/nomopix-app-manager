# ProGuard & R8 Optimization Rules for Nomopix App Manager

# Kotlinx Serialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class kotlinx.serialization.json.** { *; }

# Hilt / Dagger
-keep class * extends javax.inject.Provider

# Coil Image Loading
-keep class coil.** { *; }
