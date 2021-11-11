package com.aicc.carscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.aicc.carscreen.exception.CrashHandler;
import com.aicc.carscreen.fragment.BottomFragment;
import com.aicc.carscreen.fragment.LaneFragment;
import com.aicc.carscreen.fragment.TopFragment;
import com.aicc.carscreen.map.AICCMapView;
import com.aicc.carscreen.mqtt.AICCMqtt;
import com.aicc.carscreen.view.lane.LaneView;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AICCMapView mapView = null;
    public static DisplayMetrics dm = new DisplayMetrics();
    private ScheduledExecutorService seService = Executors.newScheduledThreadPool(10);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private TopFragment topFragment;
    private LaneFragment laneFragment;
    private BottomFragment bottomFragment;
    private Handler msgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        CrashHandler.getInstance().init(getApplicationContext());
        //全屏不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
//        mapView = (AICCMapView) findViewById(R.id.aiccmap);
//        mapView.onCreate(savedInstanceState);
//        mapView.setZoomButton(findViewById(R.id.bt_zoom_in),findViewById(R.id.bt_zoom_out));

        topFragment = (TopFragment) fragmentManager.findFragmentById(R.id.fg_top);
        laneFragment = (LaneFragment) fragmentManager.findFragmentById(R.id.fg_lane);
        bottomFragment = (BottomFragment) fragmentManager.findFragmentById(R.id.fg_bottom);

        Button bt_reconnect = findViewById(R.id.bt_reconnect);
        bt_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                topFragment.initSubscribe();
//                laneFragment.initSubscribe();
                bottomFragment.initSubscribe();
                mapView.initSubscribe();
//                AICCMqtt.getInstance().reConnect();
            }
        });

        //每3秒检查一次mqtt的连接，如果未连接则重新连接并重新订阅。
        seService.scheduleAtFixedRate(()->{
            if(!AICCMqtt.getInstance().isConnected()) {
//                System.out.println("--------------AICCMqtt is not connected");
                AICCMqtt.getInstance().reConnect();
            }
//            else
//                System.out.println("++++++++++++++AICCMqtt is connected");

        },0,3000, TimeUnit.MILLISECONDS);

        initSubscribe();
    }

        void initSubscribe(){
        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                //LaneView
                if(bundle.getByteArray("lane")!=null
                    || bundle.getByteArray("cipv")!=null) {
                    laneFragment.getView_lane().notifyMessage(bundle);
                }
                //TopFragment
                if(bundle.getByteArray("acc_target_value")!=null
                        || bundle.getByteArray("acc_state_enum")!=null
                        || bundle.getByteArray("lks")!=null
                        || bundle.getByteArray("change_lane")!=null
                        || bundle.getByteArray("horizontal_follow")!=null
                        || bundle.getByteArray("hwa")!=null
                )
                    topFragment.notifyMessage(bundle);

//                    laneFragment.setArguments(msg.getData());
            }
        };
        AICCMqtt mqtt = AICCMqtt.getInstance();
        mqtt.subscribeAllTopices(msgHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mapView.onResume();
//        mapView.getmLocationClient().startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.getmLocationClient().stopLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapView.getmLocationClient().onDestroy();
    }
}