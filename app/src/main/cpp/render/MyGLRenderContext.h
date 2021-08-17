//
// Created by fengc on 2021/8/16.
//

#ifndef CARSCREEN_MYGLRENDERCONTEXT_H
#define CARSCREEN_MYGLRENDERCONTEXT_H

#include "stdint.h"
#include <GLES3/gl3.h>


class MyGLRenderContext {
    MyGLRenderContext();
    ~MyGLRenderContext();

public:
    void OnSurfaceCreated();
    void OnSurfaceChanged(int width,int height);
    void OnDrawFrame();

    static MyGLRenderContext* GetInstance();
    static void DestroyInstance();

private:
    static MyGLRenderContext *m_pContext;
};


#endif //CARSCREEN_MYGLRENDERCONTEXT_H
