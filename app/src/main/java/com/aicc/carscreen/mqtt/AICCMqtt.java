package com.aicc.carscreen.mqtt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.aicc.carscreen.R;
import com.aicc.carscreen.Utils;
import com.aicc.carscreen.map.AICCMapView;
import com.aicc.carscreen.view.lane.LaneDrawModel;
import com.aicc.carscreen.view.lane.ObstacleDrawModel;

import aicc_adas.AiccAdas;

import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.function.Function;

/**
 * 接收数据
 */
public class AICCMqtt {
    //    private String[] mqttAddress = {"tcp://192.168.1.6:1883","tcp://192.168.107.33:1883"};
    private String[] mqttAddress = {"tcp://192.168.1.6:1883", "tcp://192.168.106.178:1883","tcp://192.168.31.141:1883"};
    private String clientId = "aicc_" + UUID.randomUUID();
    private MqttClient mqttClient = null;
    private MqttConnectOptions connectOptions = null;
    private boolean subscribeFlag = false;

    private static AICCMqtt instance = null;

    public static AICCMqtt getInstance() {
        if (instance == null) instance = new AICCMqtt();
        return instance;
    }

    public MqttClient getMqttClient() {
        return mqttClient!=null && mqttClient.isConnected() ? mqttClient : null;
    }
    public void setMqttClient(MqttClient mc){
        mqttClient = mc;
    }

    private AICCMqtt() {
        System.out.println("--------------clientId:" + clientId + "-----------------------");
        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(false);
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setConnectionTimeout(3);
        mqttClient = connect();


    }

    /**
     * 判断当前是否连接，主要为了解决mqtt服务器关闭问题
     * @return
     */
    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    /**
     * 连接客户端
     */
    public MqttClient connect() {
        /**
         * 正常连接退出，发生异常尝试下一个连接地址
         */
        for (int i = 0; i < mqttAddress.length; i++) {
            try {
                mqttClient = new MqttClient(mqttAddress[i], clientId, null);
                Log.v("connecting to broker", "Connecting to broker: " + mqttAddress[i]);
                mqttClient.connect(connectOptions);
                Log.v("connected", "Connected");
                return mqttClient;
            } catch (MqttException e) {
                e.printStackTrace();
                continue;
            }
        }
        return null;
    }

    /**
     * Client重连操作
     * @return
     */
    public MqttClient reConnect(){
        try {
            mqttClient.reconnect();
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {

                }

                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            Log.e("Mqtt reconnect",e.getMessage());
        }
        return mqttClient;
    }



    /**
     * 订阅所有topic（测试功能）
     */
    public void subscribeAllTopices(Handler handler){
        if(mqttClient!=null){
            subscribeLane(handler);
            subscribeTopFragment(handler);
        }
    }

    /**
     * 订阅车道线、障碍物主题信息
     * @param handler
     */
    private void subscribeLane(Handler handler){
        try {
            mqttClient.subscribe("/hmi/lane",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"lane",message);
            });
            mqttClient.subscribe("/hmi/cipv", 0, (topic, message) -> {
                Utils.sendByteArrayMessage(handler,"cipv",message);
            });

        } catch (MqttException e) {
            Log.e("Lane View", e.getMessage());
        }
    }

    /**
     * 订阅顶部图标状态
     * @param handler
     */
    private void subscribeTopFragment(Handler handler){
        try{
            mqttClient.subscribe("/hmi/state/acc",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"acc_state_enum",message);
            });
            mqttClient.subscribe("/hmi/acc_target_velocity",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"acc_target_value",message);
            });
            mqttClient.subscribe("/hmi/state/lks",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"lks",message);
            });
            mqttClient.subscribe("/hmi/state/change_lane",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"change_lane",message);
            });
            mqttClient.subscribe("/hmi/state/horizontal_follow",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"horizontal_follow",message);
            });
            mqttClient.subscribe("/hmi/state/hwa",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"hwa",message);
            });
        }catch(MqttException e){
            Log.e("Top Fragment",e.getMessage());
        }
    }

    private void subscribeBottomFragment(Handler handler){
        try{
            mqttClient.subscribe("/hmi/state/keep_distance",0,(topic,message)->{
                Utils.sendByteArrayMessage(handler,"keep_distance",message);
            });
        }catch(MqttException e){
            Log.e("Bottom Fragment",e.getMessage());
        }
    }

    /**
     * 测试程序
     * @param args
     * @throws MqttException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws MqttException, InterruptedException {
        String broker = "tcp://192.168.106.178:1883";
        String clientId = "liudian_sub";
        MqttClient mqttClient = new MqttClient(broker, clientId, null);
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        System.out.println("Connecting to broker: " + broker);
        mqttClient.connect(connectOptions);
        System.out.println("Connected");

//        mqttClient.subscribe("/cipv",0,(topic,message)->{
//            AiccAdas.CIPV cipv = AiccAdas.CIPV.parseFrom(message.getPayload());
//            System.out.println("cipv: "+cipv.toString());
//        });
//
//        mqttClient.subscribe("/lane",0,(topic,message)->{
//            AiccAdas.Lane lane = AiccAdas.Lane.parseFrom(message.getPayload());
//            System.out.println("lane: "+lane.toString());
//        });

        mqttClient.subscribe("/hmi/location", 0, (topic, message) -> {
            AiccAdas.Location location = AiccAdas.Location.parseFrom(message.getPayload());
//            System.out.println("location:" + location.toString());
        });

        Thread.currentThread().join();
    }
}
