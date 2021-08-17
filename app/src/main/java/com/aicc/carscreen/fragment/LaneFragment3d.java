package com.aicc.carscreen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.aicc.carscreen.view.lane.opengl.MyGLRender;
import com.aicc.carscreen.view.lane.opengl.MyGLSurfaceView;

/**
 * 车道Fragment
 */
public class LaneFragment3d extends Fragment {
    private MyGLSurfaceView view_lane_3d = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view_lane_3d = view.findViewById(R.id.view_lane_3d);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_lane_3d, container, false);
//        return view;

        MyGLRender renderer = new MyGLRender();
        MyGLSurfaceView myGLSurfaceView = new MyGLSurfaceView(getActivity(),renderer);

//        glSurfaceLaneView.setEGLContextClientVersion(2);
//        glSurfaceLaneView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        glSurfaceLaneView.setRenderer(renderer);
        return myGLSurfaceView;
    }
}
