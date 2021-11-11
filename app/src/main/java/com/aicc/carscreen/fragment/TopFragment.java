package com.aicc.carscreen.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aicc.carscreen.R;
import com.aicc.carscreen.Utils;
import com.aicc.carscreen.mqtt.AICCMqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Properties;

import aicc_adas.AiccAdas;

public class TopFragment extends Fragment {
    private ImageView iv_acc, iv_lks;
    private TextView tv_acc_speed, tv_hwa, tv_change_lane, tv_horizontal_follow;
    private AICCMqtt mqtt;
    private Handler tvHandler = null;
    private MqttClient mqttClient = null;
    private ValueAnimator va_hwa = ValueAnimator.ofFloat(0.0f, 1.0f);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iv_acc = (ImageView) view.findViewById(R.id.iv_acc);
        iv_lks = (ImageView) view.findViewById(R.id.iv_lks);
        tv_acc_speed = (TextView) view.findViewById(R.id.tv_acc_speed);
        tv_change_lane = (TextView) view.findViewById(R.id.tv_change_lane);
        tv_horizontal_follow = (TextView) view.findViewById(R.id.tv_horizontal_follow);
        tv_hwa = (TextView) view.findViewById(R.id.tv_hwa);

        tvHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                //目標速度
                if (bundle.containsKey("acc_target_value"))
                    tv_acc_speed.setText(bundle.getInt("acc_target_value") + "km/h");
                //acc狀態
                if (bundle.containsKey("acc_state_enum")) {
                    switch (bundle.getInt("acc_state_enum")) {
                        case AiccAdas.ACCState.ACCStateEnum.ACTIVE_VALUE:
                            Utils.changeIconColor(iv_acc, Utils.IconMode.on);
                            break;
                        case AiccAdas.ACCState.ACCStateEnum.INACTIVE_VALUE:
                            Utils.changeIconColor(iv_acc, Utils.IconMode.off);
                            break;
                        case AiccAdas.ACCState.ACCStateEnum.ON_VALUE:
                            Utils.changeIconColor(iv_acc, Utils.IconMode.black);
                            break;
                        case AiccAdas.ACCState.ACCStateEnum.PAUSE_VALUE:
                            Utils.changeIconColor(iv_acc, Utils.IconMode.standby);
                            break;
                        default:
                            Utils.changeIconColor(iv_acc, Utils.IconMode.on);
                            break;
                    }
                }

                //lks图标状态
                if (bundle.containsKey("lks")) {
                    if (bundle.getBoolean("lks")) Utils.changeIconColor(iv_lks, Utils.IconMode.on);
                    else Utils.changeIconColor(iv_lks, Utils.IconMode.off);
                }

                //change lane状态
                if (bundle.containsKey("change_lane")) {
                    if (bundle.getBoolean("change_lane"))
                        Utils.changeTextColor(tv_change_lane, Utils.IconMode.on);
                    else Utils.changeTextColor(tv_change_lane, Utils.IconMode.off);
                }
                //horizontal follow
                if (bundle.containsKey("horizontal_follow")) {
                    if (bundle.getBoolean("horizontal_follow"))
                        Utils.changeTextColor(tv_horizontal_follow, Utils.IconMode.on);
                    else Utils.changeTextColor(tv_horizontal_follow, Utils.IconMode.off);
                }

                //hws图标状态
                if (bundle.containsKey("hwa")) {
                    if (bundle.getBoolean("hwa")) Utils.changeTextColor(tv_hwa, Utils.IconMode.on);
                    else Utils.changeTextColor(tv_hwa, Utils.IconMode.off);
                }
            }
        };

        Utils.changeIconColor(iv_acc, Utils.IconMode.off);
        Utils.changeIconColor(iv_lks, Utils.IconMode.off);
        Utils.changeTextColor(tv_change_lane, Utils.IconMode.off);
        Utils.changeTextColor(tv_horizontal_follow, Utils.IconMode.off);
        Utils.changeTextColor(tv_hwa, Utils.IconMode.off);

    }

    @Override
    public void onStart() {
        super.onStart();
        mqtt = AICCMqtt.getInstance();
        mqttClient = mqtt.getMqttClient();
        if(mqttClient==null) {
            Toast.makeText(this.getContext(), "所有Mqtt服务器IP都无效，请核对Mqtt地址", Toast.LENGTH_SHORT).show();
            return;
        }
        initSubscribe();
    }




    /**
     * 初始化图标，接收消息后显示接收的数据
     */
    public void initSubscribe() {
        Properties properties = Utils.getProps(this.getContext());
//        if(properties!=null ) return;
        try {
            //ACC圖標狀態
            mqttClient.subscribe(properties.getProperty("HMI.STATE.ACC"), 0, (topic, message) -> {
                AiccAdas.ACCState accState = AiccAdas.ACCState.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, accState.getStateValue(), (bundle, vmsg) -> bundle.putInt("acc_state_enum", vmsg));
            });

            //顯示車的ACC速度
            mqttClient.subscribe(properties.getProperty("HMI.ACCTARGETVELOCITY"), 0, (topic, message) -> {
                AiccAdas.ACCTargetVelocity akd = AiccAdas.ACCTargetVelocity.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, (int) akd.getTargetVelocity(), (bundle, vmsg) -> bundle.putInt("acc_target_value", vmsg));
            });

            //保持車距，用來顯示本車前方的車距線
//            mqttClient.subscribe(properties.getProperty("HMI.STATE.KEEPDISTANCE"), 0, (topic, message) -> {
//                AiccAdas.ACCKeepDistance kd = AiccAdas.ACCKeepDistance.parseFrom(message.getPayload());
//                Message hmsg = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putInt("keep_distance",kd.getKeepDistanceValue());
//                hmsg.setData(bundle);
//                tvHandler.sendMessage(hmsg);
//            });
            //LKS图标状态
            mqttClient.subscribe(properties.getProperty("HMI.STATE.LKS"), 0, (topic, message) -> {
                AiccAdas.State state = AiccAdas.State.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, state.getState(), (bundle, vmsg) -> bundle.putBoolean("lks", vmsg));
            });
            //换道图标状态
            mqttClient.subscribe(properties.getProperty("HMI.STATE.CHANGELANE"), 0, (topic, message) -> {
                AiccAdas.State state = AiccAdas.State.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, state.getState(), (bundle, vmsg) -> bundle.putBoolean("change_lane", vmsg));
            });
            //横向跟车图标
            mqttClient.subscribe(properties.getProperty("HMI.STATE.HORIZONTALFOLLOW"), 0, (topic, message) -> {
                AiccAdas.State state = AiccAdas.State.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, state.getState(), (bundle, vmsg) -> bundle.putBoolean("horizontal_follow", vmsg));
            });
            //hws图标
            mqttClient.subscribe(properties.getProperty("HMI.STATE.HWA"), 0, (topic, message) -> {
                AiccAdas.State state = AiccAdas.State.parseFrom(message.getPayload());
                Utils.sendMessage(tvHandler, state.getState(), (bundle, vmsg) -> bundle.putBoolean("hwa", vmsg));
            });
        } catch (MqttException e) {
            Log.e("top fragment", e.getMessage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        return view;
    }
}
