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
import com.aicc.carscreen.mqtt.IMqttNotifyListener;
import com.google.protobuf.InvalidProtocolBufferException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Properties;

import aicc_adas.AiccAdas;

public class TopFragment extends Fragment implements IMqttNotifyListener {
    private ImageView iv_acc, iv_lks;
    private TextView tv_acc_speed, tv_hwa, tv_change_lane, tv_horizontal_follow;
    private AICCMqtt mqtt;
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
        if (mqttClient == null) {
            Toast.makeText(this.getContext(), "所有Mqtt服务器IP都无效，请核对Mqtt地址", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        return view;
    }

    @Override
    public void notifyMessage(Bundle b) {
        try {
            //目标速度
            if (b.getByteArray("acc_target_value") != null) {
                AiccAdas.ACCTargetVelocity akd = AiccAdas.ACCTargetVelocity.parseFrom(b.getByteArray("acc_target_value"));
                tv_acc_speed.setText((int) akd.getTargetVelocity() + "km/h");
            }
            //acc状态
            if (b.getByteArray("acc_state_enum") != null) {
                AiccAdas.ACCState accState = AiccAdas.ACCState.parseFrom(b.getByteArray("acc_state_enum"));
                switch (accState.getStateValue()) {
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
            if (b.getByteArray("lks") != null) {
                AiccAdas.State state = AiccAdas.State.parseFrom(b.getByteArray("lks"));
                if (state.getState()) Utils.changeIconColor(iv_lks, Utils.IconMode.on);
                else Utils.changeIconColor(iv_lks, Utils.IconMode.off);
            }

            //change lane状态
            if (b.getByteArray("change_lane") != null) {
                AiccAdas.State state = AiccAdas.State.parseFrom(b.getByteArray("change_lane"));
                if (state.getState())
                    Utils.changeTextColor(tv_change_lane, Utils.IconMode.on);
                else Utils.changeTextColor(tv_change_lane, Utils.IconMode.off);
            }
            //horizontal follow
            if (b.getByteArray("horizontal_follow") != null) {
                AiccAdas.State state = AiccAdas.State.parseFrom(b.getByteArray("horizontal_follow"));
                if (state.getState())
                    Utils.changeTextColor(tv_horizontal_follow, Utils.IconMode.on);
                else Utils.changeTextColor(tv_horizontal_follow, Utils.IconMode.off);
            }

            //hws图标状态
            if (b.getByteArray("hwa") != null) {
                AiccAdas.State state = AiccAdas.State.parseFrom(b.getByteArray("hwa"));
                if (state.getState()) Utils.changeTextColor(tv_hwa, Utils.IconMode.on);
                else Utils.changeTextColor(tv_hwa, Utils.IconMode.off);
            }

            //保持車距，用來顯示本車前方的車距線(暂时未启用)
//            mqttClient.subscribe(properties.getProperty("HMI.STATE.KEEPDISTANCE"), 0, (topic, message) -> {
//                AiccAdas.ACCKeepDistance kd = AiccAdas.ACCKeepDistance.parseFrom(message.getPayload());
//                Message hmsg = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putInt("keep_distance",kd.getKeepDistanceValue());
//                hmsg.setData(bundle);
//                tvHandler.sendMessage(hmsg);
//            });

        } catch (InvalidProtocolBufferException e) {
            Log.e("lane", e.getMessage());
        }
    }
}
