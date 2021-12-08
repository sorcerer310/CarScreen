package com.aicc.carscreen.mqtt;

import android.os.Bundle;

/**
 * mqtt实时通知接口
 */
public interface IMqttNotifyListener {
    void notifyMessage(Bundle b);
}
