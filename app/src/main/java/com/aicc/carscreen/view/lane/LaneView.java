package com.aicc.carscreen.view.lane;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aicc.carscreen.R;
import com.aicc.carscreen.Utils;
import com.aicc.carscreen.mqtt.AICCMqtt;
import com.aicc.carscreen.mqtt.IMqttNotifyListener;
import com.google.protobuf.InvalidProtocolBufferException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import aicc_adas.AiccAdas;

/**
 * 车道线View
 */
public class LaneView extends View implements IMqttNotifyListener {
    private Paint mPaint;
    private AiccAdas.LaneLine leftLaneParams, rightLaneParams;
    private AiccAdas.CIPV cipv;
    private int dmp = 10;                                                                           //默认值:每米的像素数，1米*10dp数量
    private int car_width_meter, car_height_meter = 2;                                              //车实际宽度：2米
    private int car_width, car_height = 60;                                                         //默认值:车像素宽度、高度（90dp）
    private TextView tv_cipv, tv_lane;                                                              //由上层传入在View中不能直接获得fragment中的插件

    public LaneView(Context context) {
        super(context);
        init();
    }

    public LaneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LaneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LaneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化内容
     */
    private void init() {

        dmp = OptionalInt.of(Integer.parseInt(Utils.getProps(getContext()).getProperty("DPM"))).orElse(0);
        car_height_meter = OptionalInt.of(Integer.parseInt(Utils.getProps(getContext()).getProperty("CAR.HEIGHT.METER"))).orElse(2);
        car_width_meter = OptionalInt.of(Integer.parseInt(Utils.getProps(getContext()).getProperty("CAR.WIDTH.METER"))).orElse(2);
        car_width = 60;                                                                             //车像素宽度
        car_height = 60;                                                                            //车像素高度

        mPaint = new Paint();
        mPaint.setAntiAlias(true);                                                                  //抗锯齿
        mPaint.setColor(getResources().getColor(R.color.lane_color, getContext().getTheme()));      //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);                                                          //画笔风格
        mPaint.setTextSize(36);                                                                     //绘制文字大小，单位px
        mPaint.setStrokeWidth(10);                                                                   //画笔粗细

    }

    //假设现实中1米相当于30像素，车宽约2米，整车大约60像素宽
    //车道线距离车中心1.5米
//    private final int laneOffset = Integer.parseInt(Utils.getProps(getContext()).getProperty("DPM")) * 2; //车道线相对于汽车中心横向偏移
    private final int laneZoomIn = 4;                                                                     //车道线放大倍数
    private final int laneLength = 13;                                                                    //车道线长度
    private final int tmp_count = 0;                                                                          //临时计数用

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDensity(getContext().getResources().getDisplayMetrics().densityDpi);

        //绘制车道线
        for (int i = 1; i < ldm_zoom_in.getLaneLength(); i++) {
//            System.out.println("----getLaneLength:" + ldm_zoom_in.getLaneLength() + "---getLeftLength:" + ldm_zoom_in.getLeftLane().size() + "---getRightLength:" + ldm_zoom_in.getRightLane().size());
            if (ldm_zoom_in.getLeftLane() != null && ldm_zoom_in.getLaneLength() == ldm_zoom_in.getLeftLane().size()) {
                List<float[]> leftLane = ldm_zoom_in.getLeftLane();
                if (leftLane.size() - i > 2) {
                    try {
                        canvas.drawLine(leftLane.get(i - 1)[0], leftLane.get(i - 1)[1]
                                , leftLane.get(i)[0], leftLane.get(i)[1], mPaint);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }

            if (ldm_zoom_in.getRightLane() != null && ldm_zoom_in.getLaneLength() == ldm_zoom_in.getRightLane().size()) {
                List<float[]> rightLane = ldm_zoom_in.getRightLane();
                if (rightLane.size() - i > 2) {
                    try {
                        canvas.drawLine(rightLane.get(i - 1)[0], rightLane.get(i - 1)[1]
                                , rightLane.get(i)[0], rightLane.get(i)[1], mPaint);
                    } catch (Exception e) {
                        System.out.println((e.toString()));
                    }
                }
            }
        }
        ldm.drawCompleted = true;

        //绘制障碍物
        for (Map.Entry<Integer, ObstacleDrawModel.SingleObstacle> entry : odm.getHashMapSingleObstacle().entrySet()) {
            if (entry.getValue().getBitmap() == null) continue;
            float[] dpoint = entry.getValue().getDrawPoint();

            Bitmap bitmap = entry.getValue().getBitmap();
            Rect drect = new Rect();
            drect.left = (int) dpoint[0];
            drect.top = (int) dpoint[1];
            //障碍车增加偏移量
            drect.right = (int) dpoint[0] + car_width;
            drect.bottom = (int) dpoint[1] + car_height;
            if (!entry.getValue().isHide())
                canvas.drawBitmap(bitmap, null, new Rect((int) dpoint[0], (int) dpoint[1], (int) dpoint[0] + car_width, (int) dpoint[1] + car_height), mPaint);
        }

        //调试信息：
        odm.drawCompleted = true;
    }

    private final LaneDrawModel ldm = new LaneDrawModel();
    private final LaneDrawModel ldm_zoom_in = new LaneDrawModel();

    /**
     * 绘制前更新准备数据
     */
    private void updateLane() {
        float lx = 0;
        float rx = 0;
        ldm.clearLaneData();
        ldm_zoom_in.clearLaneData();

        //1:计算原始曲线，此时计算出的lx、rx、ly、ny为道路上的实际米数
        for (float i = 0; i < laneLength; i += 0.16) {
            if (leftLaneParams != null) {
                float nlx = (i + lx);
                float nly = (float) (leftLaneParams.getC3() * Math.pow(nlx, 3) + leftLaneParams.getC2() * Math.pow(nlx, 2) + leftLaneParams.getC1() * nlx + leftLaneParams.getC0());
                ldm.getLeftLane().add(new float[]{nlx, nly});
                lx = nlx;
            } else
                ldm.getLeftLane().clear();

            if (rightLaneParams != null) {
                float nrx = (i + rx);
                float nry = (float) (rightLaneParams.getC3() * Math.pow(nrx, 3) + rightLaneParams.getC2() * Math.pow(nrx, 2)
                        + rightLaneParams.getC1() * nrx + rightLaneParams.getC0());
                ldm.getRightLane().add(new float[]{nrx, nry});
                rx = nrx;
            } else
                ldm.getRightLane();

        }

        //2:图像放大，插值补点，让曲线更平滑
        //坐车道右车道分别处理
        for (int i = 0; i < ldm.getLaneLength(); i++) {
            if (ldm.getLeftLane().size() == ldm.getLaneLength() && ldm.getLeftLane().size() > 0) {
                float[] sLeftLane = ldm.getLeftLane().get(i);
                for (int j = 1; j <= laneZoomIn; j++) {
                    sLeftLane[0] *= j;
                    sLeftLane[1] *= j;
                    ldm_zoom_in.getLeftLane().add(sLeftLane);
                }
            } else ldm_zoom_in.getLeftLane().clear();

            if (ldm.getRightLane().size() == ldm.getLaneLength() && ldm.getRightLane().size() > 0) {
                float[] sRightLane = ldm.getRightLane().get(i);
                for (int j = 1; j <= laneZoomIn; j++) {
                    sRightLane[0] *= j;
                    sRightLane[1] *= j;
                    ldm_zoom_in.getRightLane().add(sRightLane);
                }
            } else ldm_zoom_in.getRightLane().clear();
        }

        //3:坐标轴变换，并为处理车辆图片过大问题，左右各增加60dp的偏移量
        for (int i = 0; i < ldm_zoom_in.getLaneLength(); i++) {
            if (ldm_zoom_in.getLeftLane().size() == ldm_zoom_in.getLaneLength() && ldm_zoom_in.getLeftLane().size() > 0) {
                float[] sLeftLane = ldm_zoom_in.getLeftLane().get(i);
                float[] cLeftLane = convertCoordition(sLeftLane[0], sLeftLane[1]);
                cLeftLane[0] -= car_width * 0.75;
                ldm_zoom_in.getLeftLane().set(i, cLeftLane);
            } else ldm_zoom_in.getLeftLane().clear();

            if (ldm_zoom_in.getRightLane().size() == ldm_zoom_in.getLaneLength() && ldm_zoom_in.getRightLane().size() > 0) {
                float[] sRightLane = ldm_zoom_in.getRightLane().get(i);
                float[] cRightLane = convertCoordition(sRightLane[0], sRightLane[1]);
                cRightLane[0] += car_width * 0.75;
                ldm_zoom_in.getRightLane().set(i, cRightLane);
            } else ldm_zoom_in.getRightLane().clear();
        }

        ldm.drawCompleted = false;
        //数据准备完毕后，通知系统重画车道线
        this.postInvalidate();
    }

    private final ObstacleDrawModel odm = new ObstacleDrawModel();

    private void updateCIPV() {
        for (Map.Entry<Integer, ObstacleDrawModel.SingleObstacle> entry : odm.getHashMapSingleObstacle().entrySet()) {
            float[] point = entry.getValue().getPoint();                                            //车距米数
//            float[] dpiPoint = convertCoordition(point[0]*10,point[1]*10);
            float[] dpiPoint = convertCoordition(point[0] * dmp, point[1] * dmp);            //转换为dpi坐标数量
            dpiPoint[0] = dpiPoint[0] - car_width / 2;                                              //障礙車左上角對應本车中心，因此要向左偏移半个车身
            dpiPoint[1] = dpiPoint[1] - car_height * 2;                                             //障碍车左上角原点y坐标对应本车底部，要上移一个车身；本车与障碍车的距离是本车车头对障碍车车尾位置，要增加一个车身的位移

            entry.getValue().setDrawPoint(dpiPoint);
        }
        odm.drawCompleted = false;
        this.postInvalidate();
    }

    /**
     * 车辆dpi坐标转换坐标系至屏幕坐标
     * 屏幕坐标原点在左上角，x轴向右，y轴向下
     * 车辆坐标原点在车中心，x轴向上，y轴向左
     *
     * @param sx 车辆要转换的源x坐标dpi
     * @param sy 车辆要转换的源y坐标dpi
     */
    private float[] convertCoordition(float sx, float sy) {
        float[] pos = new float[2];
        //车的实际像素宽度
        pos[0] = -sy + this.getWidth() / 2;                                                             //原点在车尾部中心，x方向要减去车长的一半
        pos[1] = this.getHeight() - sx;                                                               //原点在车尾部中心，y方向要减去车长度偏移量
        return pos;
    }

    /**
     * 调试paint的透明度
     *
     * @param p   //Paint对象
     * @param idx //索引数据
     */
    private void debugPaintStroke(Paint p, int idx) {
        if (idx % 3 == 1) p.setAlpha(0);
        else if (idx % 3 == 0) p.setAlpha(255);
    }

    public void setTv_cipv(TextView tv_cipv) {
        this.tv_cipv = tv_cipv;
    }

    public void setTv_lane(TextView tv_lane) {
        this.tv_lane = tv_lane;
    }

    @Override
    public void notifyMessage(Bundle b) {
        //接收消息后处理lane、cipv的数据
        try {
            if (b.getByteArray("lane") != null) {
                if (!ldm.drawCompleted) return;
                AiccAdas.Lane lane = AiccAdas.Lane.parseFrom(b.getByteArray("lane"));
                leftLaneParams = lane.getHasLeftBoundaryCase().getNumber() > 0 ? lane.getLeftBoundary() : null;
                rightLaneParams = lane.getHasRightBoudnaryCase().getNumber() > 0 ? lane.getRightBoundary() : null;
                updateLane();
                tv_lane.setText(lane.toString());
            }
            if (b.getByteArray("cipv") != null) {
                if (!odm.drawCompleted) return;
                cipv = AiccAdas.CIPV.parseFrom(b.getByteArray("cipv"));
                odm.addObstacle(cipv);
                ObstacleDrawModel.SingleObstacle singleObstacle = odm.getHashMapSingleObstacle().get(cipv.getCipv().getId());
                if (singleObstacle != null && singleObstacle.getBitmap() == null) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
                    odm.setBitmap(cipv.getCipv().getId(), bitmap);
                }
                updateCIPV();
                tv_cipv.setText(cipv.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            Log.e("lane", e.getMessage());
        }
    }
}

