package com.harmazing.intelligentpow.model;

import java.security.PrivateKey;

/**
 * Created by jtl on 2015/4/19.
 * 更新alert的  mode
 */
public class AlertItemUpdate {
    private String deviceId; // 设备ID
    private String userId; // 用户ID
    private String rairconSetting; //
    private AlertItem clockSetting = new AlertItem(); //
    public AlertItem getClockSetting() {
        return clockSetting;
    }

    public void setClockSetting(AlertItem clockSetting) {
        this.clockSetting = clockSetting;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setRairconSetting(String rairconSetting) {
        this.rairconSetting = rairconSetting;
    }

    public String getRairconSetting() {
        return rairconSetting;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "deviceId:"+deviceId+"clockSetting: "+clockSetting.toString();
    }
}
