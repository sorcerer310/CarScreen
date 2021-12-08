//
// Created by fengc on 2021/8/16.
//

#ifndef CARSCREEN_MYGLRENDERCONTEXT_H
#define CARSCREEN_MYGLRENDERCONTEXT_H

#include "stdint.h"
#include <GLES3/gl3.h>
#include <GLSampleBase.h>


class MyGLRenderContext {
    MyGLRenderContext();
    ~MyGLRenderContext();

public:
    void SetParamsFloat(int paramType,float value0,float value1);
    void UpdateTransformMatrix(float rotateX,float rotateY,float scaleX,float scaleY);
    void OnSurfaceCreated();
    void OnSurfaceChanged(int width,int height);
    void OnDrawFrame();


    static MyGLRenderContext* GetInstance();
    static void DestroyInstance();

private:
    static MyGLRenderContext *m_pContext;
    GLSampleBase *m_pBeforeSample;
    GLSampleBase *m_pCurSample;
    int m_ScreenW;
    int m_ScreenH;
};


#endif //CARSCREEN_MYGLRENDERCONTEXT_H
