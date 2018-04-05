#include <jni.h>

JNIEXPORT jstring JNICALL
Java_no_aegisdynamics_habitat_data_weather_WeatherServiceApiImpl_getOwmApiKey(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "PLACEHOLDER");
}
