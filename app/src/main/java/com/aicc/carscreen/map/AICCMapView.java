package com.aicc.carscreen.map;

import android.content.Context;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.aicc.carscreen.R;
import com.aicc.carscreen.Utils;
import com.aicc.carscreen.mqtt.AICCMqtt;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;

import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import aicc_adas.AiccAdas;

/**
 * 扩展地图控件内容
 */
public class AICCMapView extends MapView {
    private AMap aMap = null;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private final MyLocationStyle myLocationStyle = null;
    private final AICCMqtt mqtt = AICCMqtt.getInstance();
    private MqttClient mc = mqtt.getMqttClient();
    private Handler mHandler = null;
    private MarkerOptions markerOptions = null;
    private Button btZoomIn,btZoomOut = null;

    public AICCMapView(Context context) {
        this(context, null);
    }

    public AICCMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init(this.getContext());
        initSubscribe();
    }

    public void setZoomButton(Button zoomIn,Button zoomOut){
        btZoomIn = zoomIn;
        btZoomOut = zoomOut;

        btZoomIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom++;
            }
        });

        btZoomOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom--;
            }
        });

    }

//    private double lat = 39.781576;
//    private double lon = 116.513977;
//    private float bearing = 20;

    private CoordinateConverter converter;
    private float zoom = 15;
//    private boolean zoomChange = false;
    private Marker marker = null;
    /**
     * 初始化工作
     *
     * @param context
     */
    public void init(Context context) {
        System.out.println("------------------MapView init------------------------");
        if (aMap == null)
            aMap = this.getMap();

        converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.GPS);
        markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_map_gps_locked))     //设置图片
                .anchor(0.5f, 0.5f);                                                         //设置锚点，默认为图片中心
        marker = aMap.addMarker(markerOptions);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.getUiSettings().setZoomControlsEnabled(false);

        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {

                if (mLocationClient == null) {
                    mLocationClient = new AMapLocationClient(AICCMapView.super.getContext());
                    mLocationOption = new AMapLocationClientOption();
                    mLocationOption.setSensorEnable(true);


                    mHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.getData().containsKey("empty")) {
                                Toast.makeText(context, "未接收到有效定位信息", Toast.LENGTH_SHORT).show();
                            } else {

                                try {
                                    double lat = msg.getData().getDouble("lat");                    //未转换经度
                                    double lon = msg.getData().getDouble("lon");                    //未转换纬度
                                    float bearing = msg.getData().getFloat("bearing");
                                    //转换后经纬度
                                    DPoint cPoint = converter.coord(new DPoint(lat, lon)).convert();
                                    LatLng latLng = new LatLng(cPoint.getLatitude(), cPoint.getLongitude());
//                                    CameraPosition cp = new CameraPosition(latLng, aMap.getCameraPosition().zoom, 0, bearing);
                                    CameraPosition cp = new CameraPosition(latLng, zoom, 0, bearing);
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cp);
//                                    if(!zoomChange)
//                                        aMap.animateCamera(cameraUpdate);
                                        aMap.moveCamera(cameraUpdate);
                                    marker.setRotateAngle(0);
                                    marker.setPosition(latLng);
//                                    marker.showInfoWindow();
                                } catch (Exception e) {
                                    Log.e("gps converter:", e.getMessage());
                                }
                            }
                        }
                    };
                    mLocationClient.setLocationOption(mLocationOption);
                }
            }

            @Override
            public void deactivate() {
                if (mLocationClient != null) {
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                }
                mLocationClient = null;
            }
        });

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setMyLocationEnabled(true);

    }

    /**
     * 初始化消息队列接收数据
     */
    public void initSubscribe() {
        try {
            mc.subscribe(Utils.getProps(AICCMapView.super.getContext()).getProperty("HMI.GNSS"), 0, (topic, message) -> {
                AiccAdas.GNSS gnss = AiccAdas.GNSS.parseFrom(message.getPayload());
                if (gnss.getHasLocationCase().getNumber() > 0) {
                    double lat = gnss.getLocation().getLat();
                    double lon = gnss.getLocation().getLon();
                    float bearing = gnss.getLocation().getBearing();

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    bundle.putFloat("bearing", bearing);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } else {
                    Utils.sendStringMessage(mHandler, "empty", "未接收到有效定位信息");
                }
            });
        } catch (MqttException e) {
            Log.e("map rotate subscribe", e.getMessage());
        }
    }

    public AMapLocationClient getmLocationClient() {
        return mLocationClient;
    }
}
