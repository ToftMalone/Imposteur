# Keep kotlinx.serialization generated serializers for our model classes.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class com.toftmalone.imposteur.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.toftmalone.imposteur.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.toftmalone.imposteur.data.**$$serializer { *; }
