package com.aicc.carscreen.view.lane.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
    private static final String TAG = "MyGLSurfaceView";

    public static final int IMAGE_FORMAT_RGBA = 0x01;
    public static final int IMAGE_FORMAT_NV21 = 0x02;
    public static final int IMAGE_FORMAT_NV12 = 0x03;
    public static final int IMAGE_FORMAT_I420 = 0x04;

    private MyGLRender mGLRender;
    private MyNativeRender mNativeRender;

    public MyGLSurfaceView(Context context, MyGLRender glRenderer) {
        this(context,glRenderer,null);
    }

    public MyGLSurfaceView(Context context, MyGLRender glRenderer , AttributeSet attrs){
        super(context,attrs);
        setEGLContextClientVersion(2);
        mGLRender = glRenderer;
        setEGLConfigChooser(8,8,8,8,16,8);

        setRenderer(mGLRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public MyNativeRender getNativeRender(){
        return mNativeRender;
    }


}
