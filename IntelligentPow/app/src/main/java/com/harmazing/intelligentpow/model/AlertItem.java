package com.harmazing.intelligentpow.model;

import java.io.Serializable;

/**
 * Created by jtl on 2015/3/30.
 * 定时项
 */
public class AlertItem implements Serializable {
 //闹钟： 时间，开关，温度，模式，风速，试用时间
    private String type;//舒睡曲线点击类型、node add
    private String id;//曲线id
    private String temp;//温度
    private String clocking;//设置时间
    private Integer on_off ;//开关(1：开启；0：关闭)
    private Integer windspeed ;//风速 (0,1,2......)d
    private Integer mode;//空调模式(制冷:1，制热:2，送风:3，除湿:4，自动:0)
    private Integer startend;//表示当前定时设置启用还是未启用……
    //用来设定本条设置作用在周几
    private Integer monday;//星期一
    private Integer tuesday;//星期二
    private Integer wednesday;//星期三
    private Integer thursday;//星期四
    private Integer friday;//星期五
    private Integer saturday;//星期六
    private Integer sunday;//星期日
    private Integer alone;//表示当前定时设置是否为独立的……
    private Integer num; //当前页面页码
    private String week;//星期

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "type:"+type+ " "+"id:"+id+" "+"temp:"+ temp +" "+"clocking:"+clocking+" "+"on_off:"+on_off+" "
                +"windspeed:"+windspeed+" "+"mode:"+ mode +" "+"startend:"+startend+" "+"num:"+num;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }





    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getClocking() {
        return clocking;
    }
    public void setClocking(String clocking) {
        this.clocking = clocking;
    }
    public Integer getOn_off() {
        return on_off;
    }
    public void setOn_off(Integer on_off) {
        this.on_off = on_off;
    }
    public Integer getWindspeed() {
        return windspeed;
    }
    public void setWindspeed(Integer windspeed) {
        this.windspeed = windspeed;
    }
    public Integer getMode() {
        return mode;
    }
    public void setMode(Integer mode) {
        this.mode = mode;
    }
    public Integer getStartend() {
        return startend;
    }
    public void setStartend(Integer startend) {
        this.startend = startend;
    }
    public Integer getMonday() {
        return monday;
    }
    public void setMonday(Integer monday) {
        this.monday = monday;
    }
    public Integer getTuesday() {
        return tuesday;
    }
    public void setTuesday(Integer tuesday) {
        this.tuesday = tuesday;
    }
    public Integer getWednesday() {
        return wednesday;
    }
    public void setWednesday(Integer wednesday) {
        this.wednesday = wednesday;
    }
    public Integer getThursday() {
        return thursday;
    }
    public void setThursday(Integer thursday) {
        this.thursday = thursday;
    }
    public Integer getFriday() {
        return friday;
    }
    public void setFriday(Integer friday) {
        this.friday = friday;
    }
    public Integer getSaturday() {
        return saturday;
    }
    public void setSaturday(Integer saturday) {
        this.saturday = saturday;
    }
    public Integer getSunday() {
        return sunday;
    }
    public void setSunday(Integer sunday) {
        this.sunday = sunday;
    }

    public Integer getAlone() {
        return alone;
    }
    public void setAlone(Integer alone) {
        this.alone = alone;
    }

}
