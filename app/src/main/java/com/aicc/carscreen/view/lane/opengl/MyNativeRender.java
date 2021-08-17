package com.aicc.carscreen.view.lane.opengl;

public class MyNativeRender {
    static{
        System.loadLibrary("native-render");
    }

    public native void native_Init();

//    public static void native_UnInit();

//    public static void native_SetImageData(int format,int width,int height,byte[] bytes);

    public native void native_OnSurfaceCreated();

    public native void native_OnSurfaceChanged(int width,int height);

    public native void native_OnDrawFrame();
}
