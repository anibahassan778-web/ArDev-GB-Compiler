#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include "sameboy/sameboy_core.h"

#define LOG_TAG "SameBoyNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static GB_gameboy_t g_gameboy;
static uint32_t g_frame_buffer[160 * 144];

extern "C" {

JNIEXPORT void JNICALL
Java_com_ardev_gb_pokemon_yellow_MainActivity_nativeInit(
    JNIEnv *env, jobject thiz, jbyteArray rom_bytes, jboolean is_cgb) {
    jsize len = env->GetArrayLength(rom_bytes);
    jbyte *bytes = env->GetByteArrayElements(rom_bytes, nullptr);
    sameboy_init(&g_gameboy, (const uint8_t *)bytes, (size_t)len, is_cgb);
    LOGI("SameBoy core initialized for POKEMON YELLOW (%d bytes)", len);
}

JNIEXPORT void JNICALL
Java_com_ardev_gb_pokemon_yellow_MainActivity_nativeStepFrame(
    JNIEnv *env, jobject thiz, jintArray pixel_out) {
    sameboy_step_frame(&g_gameboy, g_frame_buffer);
    env->SetIntArrayRegion(pixel_out, 0, 160 * 144, (const jint *)g_frame_buffer);
}

JNIEXPORT void JNICALL
Java_com_ardev_gb_pokemon_yellow_MainActivity_nativeSetJoypad(
    JNIEnv *env, jobject thiz, jint buttons) {
    sameboy_set_joypad(&g_gameboy, (uint8_t)buttons);
}

}
