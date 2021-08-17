package com.aicc.carscreen.fragment;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.aicc.carscreen.R;
import com.aicc.carscreen.Utils;
import com.aicc.carscreen.mqtt.AICCMqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import aicc_adas.AiccAdas;

public class BottomFragment extends Fragment {
    private SwitchCompat sw_acc;
    private Button bt_lks, bt_cl, bt_set_plus, bt_set_sub, bt_cancel, bt_hf;
    private ImageButton ibt_keep_distance;
    private AICCMqtt mqtt;
    private MqttClient mqttClient;
    private Drawable da_no_gap, da_stage1, da_stage2, da_stage3;
    private ScheduledExecutorService seService = Executors.newScheduledThreadPool(10);
    private Handler bt_handler = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mqtt = AICCMqtt.getInstance();
        mqttClient = mqtt.getMqttClient();

        bt_set_plus = (Button) view.findViewById(R.id.bt_set_plus);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        sw_acc = (SwitchCompat) view.findViewById(R.id.sw_acc);
        ibt_keep_distance = (ImageButton) view.findViewById(R.id.ibt_keep_distance);
        bt_set_sub = (Button) view.findViewById(R.id.bt_set_sub);

        bt_cl = (Button) view.findViewById(R.id.bt_cl);
        bt_hf = (Button) view.findViewById(R.id.bt_hf);
        bt_lks = (Button) view.findViewById(R.id.bt_lks);

        setButtonScaleEffect(new Button[]{bt_set_plus, bt_cancel, bt_set_sub, bt_cl, bt_hf, bt_lks}, new ImageButton[]{ibt_keep_distance}, 80);

        da_no_gap = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.dk_no_gap));
        da_stage1 = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.dk_stage1));
        da_stage2 = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.dk_stage2));
        da_stage3 = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.dk_stage3));

        bt_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                //车距保持状态
                if (bundle.containsKey("keep_distance")) {
                    switch (bundle.getInt("keep_distance")) {
                        case AiccAdas.ACCKeepDistance.DistanceStage.NO_GAP_VALUE:
                            ibt_keep_distance.setImageResource(R.drawable.dk_no_gap);
//                            ibt_keep_distance.setBackground(da_no_gap);
                            break;
                        case AiccAdas.ACCKeepDistance.DistanceStage.STAGE1_VALUE:
                            ibt_keep_distance.setImageResource(R.drawable.dk_no_gap);
                            break;
                        case AiccAdas.ACCKeepDistance.DistanceStage.STAGE2_VALUE:
                            ibt_keep_distance.setImageResource(R.drawable.dk_stage1);
                            break;
                        case AiccAdas.ACCKeepDistance.DistanceStage.STAGE3_VALUE:
                            ibt_keep_distance.setImageResource(R.drawable.dk_stage2);
                            break;
                        case AiccAdas.ACCKeepDistance.DistanceStage.STAGE4_VALUE:
                            ibt_keep_distance.setImageResource(R.drawable.dk_stage3);
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        initButton();
        initSwitchTask();
        initSubscribe();

    }

    /**
     * 按钮处理
     */
    private void initButton() {
        bt_set_plus.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.SP")));
        bt_cancel.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.CANCEL")));
        ibt_keep_distance.setOnClickListener(v -> {
            publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.KD"));
        });
        bt_set_sub.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.SS")));

        bt_cl.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.CL")));
        bt_hf.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.HF")));
        bt_lks.setOnClickListener(v -> publishButtonMessage(Utils.getProps(this.getContext()).getProperty("HMI.BT.LKS")));
    }

    /**
     * 初始化switch开关的循环信号量
     */
    private void initSwitchTask() {
        seService.scheduleAtFixedRate(() -> {
            AiccAdas.Switch aSwitch = null;
            if (sw_acc.isChecked())
                aSwitch = AiccAdas.Switch.newBuilder().setState(AiccAdas.Switch.State.ON).build();
            else
                aSwitch = AiccAdas.Switch.newBuilder().setState(AiccAdas.Switch.State.OFF).build();

            MqttMessage message = new MqttMessage(aSwitch.toByteArray());
            message.setQos(0);
            try {
                mqttClient.publish(Utils.getProps(this.getContext()).getProperty("HMI.SW.ACC"), message);
//                System.out.println("-----switch:-------");
            } catch (MqttException e) {
                Log.e("switch", Utils.getProps(this.getContext()).getProperty("HMI.SW.ACC") + ":" + message.toString() + " " + e.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 初始化订阅部分
     */
    public void initSubscribe() {
        Properties properties = Utils.getProps(this.getContext());
        try {
            mqttClient.subscribe(properties.getProperty("HMI.STATE.KEEPDISTANCE"), 0, (topic, message) -> {
                AiccAdas.ACCKeepDistance kd = AiccAdas.ACCKeepDistance.parseFrom(message.getPayload());
                Utils.sendMessage(bt_handler, kd.getKeepDistanceValue(), (bundle, vmsg) -> bundle.putInt("keep_distance", vmsg));
            });
        } catch (MqttException e) {
            Log.e("top fragment", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);
        return view;
    }

    /**
     * 发送按钮click消息
     *
     * @param topic 不同的按钮使用不同的频道
     */
    private void publishButtonMessage(String topic) {
        AiccAdas.Button button = AiccAdas.Button.newBuilder().setEvent(AiccAdas.Button.Event.CLICKED).build();
        MqttMessage message = new MqttMessage(button.toByteArray());
        //Qos设置为2，对方一定会收到该消息
        message.setQos(2);
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            Log.e("button", topic + ":" + message.toString() + " " + e.getMessage());
        }
    }

    /**
     * 设置按钮缩放效果
     *
     * @param bts
     */
    private void setButtonScaleEffect(Button[] bts, ImageButton[] ibts, long duration) {
        for (int i = 0; i < bts.length; i++) {
            final Button bt = bts[i];
            bt.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bt.animate().scaleX(0.9f).scaleY(0.9f).setDuration(duration).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        bt.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        bt.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start();
                        break;
                }
                return false;
            });
        }

        for (int i = 0; i < ibts.length; i++) {
            final ImageButton ibt = ibts[i];
            ibt.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ibt.animate().scaleX(0.9f).scaleY(0.9f).setDuration(duration).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ibt.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ibt.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration).start();
                        break;
                }
                return false;
            });
        }
    }
}