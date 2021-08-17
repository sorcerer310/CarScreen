//
// Created by fengc on 2021/8/13.
//
//#include <Importer.hpp>
#include "jni.h"
#include "render/MyGLRenderContext.h"

#define NATIVE_RENDER_CLASS_NAME "com.aicc.carscreen.view.lane.opengl.MyNativeRender"
//Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_

#ifdef __cplusplus
extern "C" {
#endif


/*
* Class:     com_aicc_carscreen_view_lane_opengl_MyNativeRender
* Method:    native_Init
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1Init(JNIEnv *env,jobject instance){
//    MyGLRenderContext::GetInstance();
//    Assimp::Importer* importer = new Assimp::Importer();
//    Assimp::Importer* importer = new Assimp::Importer();
}

JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1OnSurfaceCreated(JNIEnv *env,jobject instance)
{
    MyGLRenderContext::GetInstance()->OnSurfaceCreated();
}

JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1OnSurfaceChanged(JNIEnv *env,jobject instance,jint width,jint height)
{
    MyGLRenderContext::GetInstance()->OnSurfaceChanged(width,height);
}


JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1OnDrawFrame(JNIEnv *env,jobject instance){
    MyGLRenderContext::GetInstance()->OnDrawFrame();
}

#ifdef __cplusplus
}
#endif