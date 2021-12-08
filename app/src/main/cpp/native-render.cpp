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

JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1UpdateTransformMatrix(JNIEnv *env,jobject instance,jfloat rotateX,jfloat rotateY,jfloat scaleX,jfloat scaleY){
    MyGLRenderContext::GetInstance()->UpdateTransformMatrix(rotateX,rotateY,scaleX,scaleY);
}

JNIEXPORT void JNICALL Java_com_aicc_carscreen_view_lane_opengl_MyNativeRender_native_1SetParamFloat(JNIEnv *env,jobject instance,jint paramType,jfloat value0,jfloat value1){
    MyGLRenderContext::GetInstance()->SetParamsFloat(paramType,value0,value1);
}

#ifdef __cplusplus
}
#endif