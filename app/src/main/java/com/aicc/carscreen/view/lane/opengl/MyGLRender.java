package com.aicc.carscreen.view.lane.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRender implements GLSurfaceView.Renderer{
    private static final String TAG = "MyGLRender";
    private MyNativeRender mNativeRender;
    private int mSampleType;

    public MyGLRender(){
        mNativeRender = new MyNativeRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        GLES20.glClearColor(0f,0f,0f,1.0f);
        mNativeRender.native_OnSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        GLES20.glViewport(0,0,width,height);
        mNativeRender.native_OnSurfaceChanged(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        System.out.println("---------------onDrawFrame----------------");
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mNativeRender.native_OnDrawFrame();
    }

    public void updateTransformMatrix(float rotateX,float rotateY,float scaleX,float scaleY){
        mNativeRender.native_UpdateTransformMatrix(rotateX,rotateY,scaleX,scaleY);
    }

    public void setTouchLoc(float x,float y){
        mNativeRender.native_SetParamFloat(MyNativeRender.SAMPLE_TYPE_SET_TOUCH_LOC,x,y);
    }

    public void setGravityXY(float x,float y){
        mNativeRender.native_SetParamFloat(MyNativeRender.SAMPLE_TYPE_GRAITY_XY,x,y);
    }

    public int getSampleType(){
        return mSampleType;
    }
}
