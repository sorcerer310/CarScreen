package com.aicc.carscreen.view.lane.opengl;

public class MyNativeRender {
    public static final int SAMPLE_TYPE = 200;
    public static final int SAMPLE_TYPE_SET_TOUCH_LOC = SAMPLE_TYPE + 999;
    public static final int SAMPLE_TYPE_GRAITY_XY = SAMPLE_TYPE + 1000;

    static {
        System.loadLibrary("native-render");
    }

    public native void native_Init();

//    public static void native_UnInit();

//    public static void native_SetImageData(int format,int width,int height,byte[] bytes);

    public native void native_OnSurfaceCreated();

    public native void native_OnSurfaceChanged(int width, int height);

    public native void native_OnDrawFrame();

    public native void native_UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

    public native void native_SetParamFloat(int paramType,float value0,float value1);

}
