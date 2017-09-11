/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_go_algorithm_Detector */

#ifndef _Included_com_go_algorithm_Detector
#define _Included_com_go_algorithm_Detector
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_go_algorithm_Detector
 * Method:    Detect
 * Signature: (Ljava/lang/Object;III)[I
 */
JNIEXPORT jintArray JNICALL Java_com_go_algorithm_Detector_Detect
  (JNIEnv *, jclass, jobject, jint, jint, jint);


JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetCut //手机截屏
        (JNIEnv *, jclass, jobject);

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetOrigin //彩色原图
        (JNIEnv *, jclass, jobject);

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetGrid //黑白图+网格
        (JNIEnv *, jclass, jobject);

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetCircle //黑白图+圆
        (JNIEnv *, jclass, jobject);

JNIEXPORT void JNICALL Java_com_go_algorithm_Detector_GetCanny //canny
        (JNIEnv *, jclass, jobject);

#ifdef __cplusplus
}
#endif
#endif
