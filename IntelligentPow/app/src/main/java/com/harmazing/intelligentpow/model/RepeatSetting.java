package com.harmazing.intelligentpow.model;

/**
 * Created by jtl on 2015/4/21.
 * 设置重复周期用到的数据传输类
 */
public class RepeatSetting {
    private String type; //类型
    private Week weeks; //星期
    private String deviceId; //设备ID
    private String userId; //用户ID

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String curveId;
    //用来设定本条设置作用在周几
    public class Week{
        private Integer monday;//星期一
        private Integer tuesday;//星期二
        private Integer wednesday;//星期三
        private Integer thursday;//星期四
        private Integer friday;//星期五
        private Integer saturday;//星期六
        private Integer sunday;//星期日
//        private Integer alone;//表示当前定时设置是否为独立的……

        public Integer getMonday() {
            return monday;
        }

        public void setMonday(Integer monday) {
            this.monday = monday;
        }

        public Integer getWednesday() {
            return wednesday;
        }

        public void setWednesday(Integer wednesday) {
            this.wednesday = wednesday;
        }

        public Integer getTuesday() {
            return tuesday;
        }

        public void setTuesday(Integer tuesday) {
            this.tuesday = tuesday;
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

        @Override
        public String toString() {
            return "monday:"+monday+" "+"tuesday:"+tuesday+" "+"wednesday:"+wednesday+" "+"thursday:"+thursday+" "+"friday:"+friday+" "+"monday:"+monday+" "+
                    "saturday:"+saturday+" "+"monday:"+sunday+" ";
        }
    }

    public String getId() {
        return deviceId;
    }

    public void setId(String id) {
        this.deviceId = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Week getWeeks() {
        return weeks;
    }

    public void setWeeks(Week weeks) {
        this.weeks = weeks;
    }

    public String getCurveId() {
        return curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    @Override
    public String toString() {
        return "type:"+type+" "+"curveId:"+curveId +" "+ getWeeks().toString();
    }
}
