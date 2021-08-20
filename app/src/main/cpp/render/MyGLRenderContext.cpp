//
// Created by fengc on 2021/8/16.
//

#include "MyGLRenderContext.h"
#include <Car.h>

MyGLRenderContext* MyGLRenderContext::m_pContext = nullptr;

MyGLRenderContext::MyGLRenderContext()
{
    m_pCurSample = new Car();
    m_pBeforeSample = nullptr;
}

MyGLRenderContext::~MyGLRenderContext()
{
    if(m_pCurSample)
    {
        delete m_pCurSample;
        m_pCurSample = nullptr;
    }

    if(m_pBeforeSample)
    {
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }
}


void MyGLRenderContext::OnSurfaceCreated()
{
    glClearColor(0.0f,0.0f,1.0f,1.0f);
}

void MyGLRenderContext::OnSurfaceChanged(int width, int height)
{
    glViewport(0,0,width,height);
    m_ScreenH = width;
    m_ScreenW = width;
}

void MyGLRenderContext::OnDrawFrame()
{
    LOGCATE("MyGLRenderContext::OnDrawFrame()");

    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    if(m_pBeforeSample){
        m_pBeforeSample->Destroy();
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }

    if(m_pCurSample){
        m_pCurSample->Init();
        m_pCurSample->Draw(m_ScreenW,m_ScreenH);
    }
}

void MyGLRenderContext::SetParamsFloat(int paramType, float value0, float value1) {
    switch(paramType){
        case SAMPLE_TYPE_KEY_SET_TOUCH_LOC:
            m_pCurSample->SetTouchLocation(value0,value1);
            break;
        case SAMPLE_TYPE_SET_GRAVITY_XY:
//            m_pCurSample->
            break;
        default:
            break;
    }
}

/**
 *
 * @param rotateX
 * @param rotateY
 * @param scaleX
 * @param scaleY
 */
void MyGLRenderContext::UpdateTransformMatrix(float rotateX, float rotateY, float scaleX,
                                              float scaleY) {
    if(m_pCurSample){
        m_pCurSample->UpdateTransformMatrix(rotateX,rotateY,scaleX,scaleY);
    }
}

MyGLRenderContext *MyGLRenderContext::GetInstance()
{
    if(m_pContext== nullptr)
        m_pContext = new MyGLRenderContext();
    return m_pContext;
}

void MyGLRenderContext::DestroyInstance() {
    if(m_pContext)
    {
        delete m_pContext;
        m_pContext = nullptr;
    }
}
