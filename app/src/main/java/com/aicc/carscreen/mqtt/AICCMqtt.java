package com.aicc.carscreen.mqtt;

import android.os.AsyncTask;
import android.util.Log;

import com.aicc.carscreen.map.AICCMapView;

import aicc_adas.AiccAdas;

import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;

/**
 * 接收数据
 */
public class AICCMqtt {
    //    private String[] mqttAddress = {"tcp://192.168.1.6:1883","tcp://192.168.107.33:1883"};
    private String[] mqttAddress = {"tcp://192.168.1.6:1883", "tcp://192.168.107.33:1883","tcp://192.168.31.141:1883"};
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
     *
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
     * 测试程序
     *
     * @param args
     * @throws MqttException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws MqttException, InterruptedException {
        String broker = "tcp://192.168.107.33:1883";
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
