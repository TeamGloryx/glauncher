-dontoptimize
-dontobfuscate

-dontwarn kotlinx.**

-keepclasseswithmembers public class LaunchKt {
    public static void main(java.lang.String[]);
}
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

-keep class cat.** { *; }
-keep class at.favre.** { *; }
-keep class me.nullicorn.** { *; }