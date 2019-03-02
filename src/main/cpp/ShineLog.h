//
// Created by hrblaoj on 2018/3/30.
//

#ifndef SHINEKTV_ANDROID_SHINELOG_H
#define SHINEKTV_ANDROID_SHINELOG_H


//haiqing
#include <android/log.h>
#define TAG "shine-jni"

#define SHINEKTV_DEBUG

#ifdef SHINEKTV_DEBUG
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define printf(...) ((void)__android_log_print(ANDROID_LOG_INFO, "HTTPSERVER", __VA_ARGS__))
#else
#define LOGD(...)
#define printf(...)
#endif

#endif //SHINEKTV_ANDROID_SHINELOG_H
