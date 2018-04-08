//
// Created by Jyoung on 2018. 4. 6..
//

#include <jni.h>
#include "libavformat/avformat.h"
#include <android/log.h>
#include "jy_sopt_gifexample_NDK.h"

#define LOG_TAG "FFmpegForAndroid"

#define LOGI(...) __android_log_print(4, LOG_TAG, __VA_ARGS__);
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , "libnav", __VA_ARGS__)
#define LOGE(...) __android_log_print(6, LOG_TAG, __VA_ARGS__);

JNIEXPORT jstring JNICALL Java_jy_sopt_gifexample_NDK_getNDKTestString
  (JNIEnv *env, jobject){

    // getNDKTestString함수를 Android쪽에서 호출할 경우 문자열만 반환하는 함수
    jstring str =  (*env).NewStringUTF("This is AvILoS First NDK TEST STRING!!");
    return str;
    }
