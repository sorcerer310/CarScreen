package com.aicc.carscreen.view.lane;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * 车道线绘制对象
 */
public class LaneDrawModel {
    private List<float[]> leftLane = new ArrayList<>();
    private List<float[]> rightLane = new ArrayList<>();
    public boolean drawCompleted = true;
    /**
     * 清理所有车道线数据
     */
    public void clearLaneData(){
        leftLane.clear();
        rightLane.clear();
    }

    /**
     * 返回要绘制的车道线点的数量
     * @return
     */
    public int getLaneLength(){
        if(leftLane.size()>=rightLane.size()) return leftLane.size();
        else return rightLane.size();
    }

    public List<float[]> getLeftLane() {
        return leftLane;
    }

    public void setLeftLane(List<float[]> leftLane) {
        this.leftLane = leftLane;
    }

    public List<float[]> getRightLane() {
        return rightLane;
    }

    public void setRightLane(List<float[]> rightLane) {
        this.rightLane = rightLane;
    }
}
