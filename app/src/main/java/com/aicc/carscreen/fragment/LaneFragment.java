package com.aicc.carscreen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.aicc.carscreen.R;
import com.aicc.carscreen.view.lane.LaneView;

/**
 * 车道Fragment
 */
public class LaneFragment extends Fragment {
    private TextView tv_cipv, tv_lane;

    private LaneView view_lane = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_cipv = view.findViewById(R.id.tv_cipv_params);
        tv_lane = view.findViewById(R.id.tv_lane_params);
        view_lane = view.findViewById(R.id.view_lane);
        view_lane.setTv_cipv(tv_cipv);
        view_lane.setTv_lane(tv_lane);

    }

    /**
     * 初始化重新订阅内容
     */
//    public void initSubscribe(){
//        view_lane.initSubscribe();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lane, container, false);
        return view;
    }

    public LaneView getView_lane() {
        return view_lane;
    }
}
