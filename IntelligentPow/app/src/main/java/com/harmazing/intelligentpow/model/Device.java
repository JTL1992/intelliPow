package com.harmazing.intelligentpow.model;

import android.widget.ImageView;

/**
 * Created by JTL on 2014/9/18.
 * 设备信息
 */
public class Device {
    private String id;
    private String gwId;
    private String temp;
    private String state;
    private String name;
    private int hum;
    private int humOrder;
    private int icon;
    private int wind;
    private int windOrder;
    private int deviceType;
    private int battery;
    private int tempOrder;
    private int minTemp;
    private int maxTemp;
    private int hotCold;
    private String errorDetail;

    public Device(String temp, String state, String name, int hum, int icon, int wind,int deviceType){
        this.hum = hum;
        this.temp = temp;
        this.icon = icon;
        this.state = state;
        this.name = name;
        this.wind = wind;
        this.deviceType = deviceType;
    }
    public Device(String temp, String state, String name, int hum, int icon, int wind,int deviceType, int minTemp, int maxTemp, int hotCold){
        this.hum = hum;
        this.temp = temp;
        this.icon = icon;
        this.state = state;
        this.name = name;
        this.wind = wind;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.deviceType = deviceType;
        this.hotCold = hotCold;
    }
    public Device(){}
    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public void setHotCold(int hotCold) {
        this.hotCold = hotCold;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHumOrder(int humOrder) {
        this.humOrder = humOrder;
    }

    public void setTempOrder(int tempOrder) {
        this.tempOrder = tempOrder;
    }

    public void setWindOrder(int windOrder) {
        this.windOrder = windOrder;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getWindOrder() {
        return windOrder;
    }

    public int getHumOrder() {
        return humOrder;
    }

    public int getTempOrder() {
        return tempOrder;
    }

    public int getHum() {
        return hum;
    }
   public int getIcon(){
       return icon;
   }
   public int getWind(){
       return wind;
   }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getTemp() {
        return temp;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public int getBattery() {
        return battery;
    }

    public String getId() {
        return id;
    }

    public String getGwId() {
        return gwId;
    }

    public int getHotCold() {
        return hotCold;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}
