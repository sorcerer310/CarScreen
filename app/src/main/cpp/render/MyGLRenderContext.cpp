//
// Created by fengc on 2021/8/16.
//

#include "MyGLRenderContext.h"

MyGLRenderContext* MyGLRenderContext::m_pContext = nullptr;

MyGLRenderContext::MyGLRenderContext()
{

}

MyGLRenderContext::~MyGLRenderContext()
{

}

void MyGLRenderContext::OnSurfaceCreated()
{
    glClearColor(0.0f,0.0f,1.0f,1.0f);
}

void MyGLRenderContext::OnSurfaceChanged(int width, int height)
{
    glViewport(0,0,width,height);
}

void MyGLRenderContext::OnDrawFrame()
{
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
}

MyGLRenderContext *MyGLRenderContext::GetInstance()
{
    if(m_pContext== nullptr)
        m_pContext = new MyGLRenderContext();
    return m_pContext;
}
