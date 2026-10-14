# Keep native JNI bindings
-keep class com.ardev.gb.** { *; }
-keepclasseswithmembernames class * { native <methods>; }
