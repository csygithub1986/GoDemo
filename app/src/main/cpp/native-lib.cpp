#include "com_go_algorithm_Detector.h"
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>
#include "CppTest.h"

using namespace cv;

JNIEXPORT jintArray JNICALL Java_com_go_algorithm_Detector_Detect
        (JNIEnv *env, jclass thizz, jobject bitmap, jint w, jint h, jint boardSize) {
    AndroidBitmapInfo info;
    void *pixels;

    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);

    Mat img;
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        img = Mat(info.height, info.width, CV_8UC4, pixels);
    } else {
        img = Mat(info.height, info.width, CV_8UC2, pixels);
    }
    AndroidBitmap_unlockPixels(env, bitmap);


    uchar *psrc = (uchar *) img.data;
    vector<int> temp;
    int leng = img.total();
    for (int i = 0; i < leng; ++i) {
        temp.push_back((int) psrc[i]);
    }


    int result[boardSize * boardSize];
    int success = Detect(img, w, h, 3, boardSize, result);
    if (!success)
        return nullptr;
    //1.新建长度len数组
    int len = boardSize * boardSize;
    jintArray jarr = env->NewIntArray(len);
    //2.获取数组指针
    jint *arr = env->GetIntArrayElements(jarr, NULL);
    //3.赋值
    for (int i = 0; i < len; i++) {
        arr[i] = result[i];
    }
    //4.释放资源
    env->ReleaseIntArrayElements(jarr, arr, 0);
    //5.返回数组
    return jarr;
}


JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetCut //手机截屏
        (JNIEnv *env, jclass thizz, jobject bitmap) {


}

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetOrigin //彩色原图
        (JNIEnv *env, jclass thizz, jobject bitmap) {

}

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetGrid //黑白图+网格
        (JNIEnv *env, jclass thizz, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);

    Mat img;
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        img = Mat(info.height, info.width, CV_8UC4, pixels);
    } else {
        img = Mat(info.height, info.width, CV_8UC2, pixels);
    }
    GetGrid(&img);
    AndroidBitmap_unlockPixels(env, bitmap);
}

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetCircle //黑白图+圆
        (JNIEnv *env, jclass thizz, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);

    Mat img;
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        img = Mat(info.height, info.width, CV_8UC4, pixels);
    } else {
        img = Mat(info.height, info.width, CV_8UC2, pixels);
    }
    GetCircle(&img);
    AndroidBitmap_unlockPixels(env, bitmap);
}