#include <jni.h>
#include <string>
//不需要加extern "C"因为该头文件帮我们加了
#include <rtmp.h>
#include <x264.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_dntutty_rtmp_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    x264_picture_t *picture = new x264_picture_t;
    char version[50];sprintf(version,"librtmp version:%d",RTMP_LibVersion());
//    return env->NewStringUTF(hello.c_str());
    return env->NewStringUTF(version);
}
