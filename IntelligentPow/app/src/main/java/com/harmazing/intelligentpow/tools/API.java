package com.harmazing.intelligentpow.tools;

/**
 * Created by Administrator on 2015/4/20.
 */
public class API {
    //登录
    public final static String LOGIN = "login?";
    //获取用户设备列表
    public final static String GET_USER_DEVICE_LIST = "index?";
    //修改密码
    public final static String CHANGE_SECRET = "changeUserPwd?";
    //连接网关
    public final static String BIND_NET_GATE = "addDevice?";
    //加载本地的空调用电趋势html
    public final static String LOCAL_YDQS_HTML_DR = "file:///android_asset/ydqs_door.html?deviceId=";
    //加载本地的用电趋势html
    public final static String LOCAL_DLTJ_HTML = "file:///android_asset/newhtml.html?deviceId=";
    //加载本地的空调用电趋势html
    public final static String LOCAL_YDQS_HTML_AC = "file:///android_asset/ydqs.html?deviceId=";
    //修改设备名称
    public final static String CHANGE_DEVICE_NAME = "changeDeviceName?";
    //请求设备信息的URL
    public final static String GET_DEVICE_DATA = "deviceData?";
    //改变空调状态的URL
    public final static String CHANGE_AC_STATE = "changeAcState?";
    //添加曲线
    public final static String ADD_CURVES_CLOCKS = "addCurvesClocks";
    //修改 添加定时
    public final static String UPDATE_ADD_ALERT = "UpdateShowTimingSet";
    //修改 修改熟睡曲线点
    public final static String UPDATE_SLEEP_ALERT = "updateClock";
    //加载本地的舒睡曲线html
    public final static String LOCAL_CURVES_HTML = "file:///android_asset/aa/charts.html?userId=";
    //查询设备的定时项
    public final static String QUERY_TIMESETTING = "QueryTimingSet?deviceId=";
    //删除定时项
    public final static String DEL_TIMESETTING = "delCurveSetting?";
    //设置重复设置
    public final static String REPEAT_SETTING = "updateCurveRepeat";
    //打开网络
    public final static String OPEN_NET = "openNetWork?usrId=";
    //初始化舒适曲线重复设置
    public final static String INIT_REPEAT = "queryCurveRepeatData";
    //更新舒适曲线重复设置
    public final static String UPDATE_REPEAT = "saveRepeatPopup";
}
