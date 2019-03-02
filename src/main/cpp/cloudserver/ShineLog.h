//
// Created by hrblaoj on 2018/3/30.
//

#ifndef SHINEKTV_ANDROID_SHINELOG_H
#define SHINEKTV_ANDROID_SHINELOG_H



#include <android/log.h>
#define TAG "shine-jni"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define printf(...) ((void)__android_log_print(ANDROID_LOG_INFO, "HTTPSERVER", __VA_ARGS__))

#endif //SHINEKTV_ANDROID_SHINELOG_H
