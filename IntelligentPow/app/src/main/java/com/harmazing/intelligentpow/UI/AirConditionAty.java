package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.Window;
import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.CoverFlow;
import com.harmazing.intelligentpow.view.CrossWebView;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.view.ImageAdapter;
import com.harmazing.intelligentpow.view.MyViewGroup;
import com.harmazing.intelligentpow.view.SlipButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by JTL on 2014/9/17.
 * 空调详细信息控制界面
 */

public class AirConditionAty extends Activity implements GestureDetector.OnGestureListener {
    private final static String TAG = "AirConditionAty";
    private   CoverFlow coverFlowTemp,coverFlowMode,coverFlowWind;   //温度，模式， 风速的Gallery
    /*温度，模式，风速档位*/
    private Integer [] mImageWindRecourseId = {R.drawable.icon_mode_auto,R.drawable.icon_wind1,R.drawable.icon_wind2,R.drawable.icon_wind3,R.drawable.icon_wind4,R.drawable.icon_wind5,R.drawable.icon_wind_silent};
    private Integer [] mImageTempRecourseId = {R.drawable.degree_15,R.drawable.degree_16,R.drawable.degree_17,R.drawable.degree_18,R.drawable.degree_19,R.drawable.degree_20,R.drawable.degree_21,R.drawable.degree_22,R.drawable.degree_23,R.drawable.degree_24,R.drawable.degree_25,R.drawable.degree_26,R.drawable.degree_27,R.drawable.degree_28,R.drawable.degree_29,R.drawable.degree_30,R.drawable.degree_31,R.drawable.degree_32,R.drawable.degree_33,R.drawable.degree_34,R.drawable.degree_35};
    private Integer [] mImageModeRecourseId = {R.drawable.icon_mode_auto,R.drawable.icon_mode_sendwind,R.drawable.icon_mode_cold,R.drawable.icon_mode_hot,R.drawable.icon_mode_dehumidification};
    private Integer [] mImageTempLimitedRecourseId ; //限制温度设置，实际温度范围载入
    private Integer [] mImageModeLimaitedRecourseId ; //限制模式
//    private Integer [] mImageTempRecourseId = {R.drawable.test_16,R.drawable.test_16,R.drawable.test_16,R.drawable.test_16,R.drawable.test_16,R.drawable.test_16};
    private int id, tempGet,windGet,modeGet;           // 从主界面获取到的id，温度，风速，模式。
    private int width,height; // 屏幕高度宽度
    private String deviceId;                        //设备Id
    private String gwId ;   //设备网关
    private boolean flag1, flag2, flag3;   //旗帜位指示空调第一次进入不上传，直接获取主界面的信息
    private boolean openFlag, holdFlag1, holdFlag2; //openFlag开启空调时，读一次设备数据，flag1刷新数据阻止post标识，flag2post时停止刷新
    private String  stateDevice,modifyName = null; //设备状态，修改名称
    private Boolean closeFlag;                          //设备关标识，防止开机时刷新关。
    private SlipButton btnState;                 //  开关键
    private CrossWebView webView;                     // 图表
    private TextView insideTemp,title;          //室温和标题
    private Boolean timerStart;                //TimeTask启动标识
    private Context mContext;                 //启动提示对话框的上下文
    private android.os.Handler mHandler;
    private int maxTemp = 35;           //温度范围
    private int minTemp = 15;           //温度范围
    private int allowHot = 1;           //有制热模式
    private SlidingDrawer slidingDrawer;                                      //  滑动抽屉
    private final String urlChangeAcState = HttpHead.head+"changeAcState?"; //改变空调状态的URL
    private final String urlGetDeviceData = HttpHead.head+"deviceData?"; //请求设备信息的URL
    private final String urlChangeAcName = HttpHead.head+"changeDeviceName?"; //修改设备名称
    private JsonHttpResponseHandler postJsonHttpResponseHandler,getDataJsonHttpResponseHandler;//JSON处理
    private ProgressDialog mProgressDialog;
    private ImageView deviceIcon,deviceMode,deviceWind;
    private TextView deviceTemp,deviceState;
    private TextView tempSetting, modeSetting, windSetting; //温度，风速，模式提示
    private GestureDetector mGestureDetector;
    private Button btnElectroAnalyz, btnElectroQuantity;
    private RelativeLayout deviceInfoLayout,changeNameLayout,alertSettingLayout,sleepLineLayout;
    private MyViewGroup airconditionlayout;
    private LinearLayout setting;
    private ImageButton modify;
    private Boolean settingFlag;
    ImageAdapter tempAdapter;
    ImageAdapter modeAdapter;
    ImageAdapter windAdapter;
    private final static int FLASH_TIME = 5000;          //刷新时间为5秒
    /*屏幕适配尺寸类型*/
    private final static int SMALL_WIDTH = 200;
    private final static int SMALL_HIGHT = 100;
    private final static int BIG_WIDTH = 400;
    private final static int BIG_HIGHT = 120;
    private final static int PHONE_SMALL_WIDTH = 500;
    private final static int PHONE_SMALL_HIGHT = 860;
    private final static int PHONE_BIG_WIDTH = 1000;
    /*温度，模式，风速滚动选项参数*/
    private final static int BIG_ZOOM_SIZE = -300;
    private final static int SMALL_ZOOM_SIZE = -180;
    private final static int ROTATION_ANGLE = 80;
    private final static int ANIMATION_DURACION = 1000;
    private final static int RGB_COLOR = 222;
    /*设备信息*/
    private final static int EXCEPTION = 0;
    private final static int NORMAL = 1;
    private final static int OPEN = 1;
    private final static int CLOSE = 0;
    /*当前设备的信息*/
    public static int nowTemp;
    public static int nowMode;
    public static int nowWind;
    public static int nowState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        timerStart = true;  //刷新标识{true： 刷新}
        flag1 = true;      //温度进入页面第一次不刷新{true:不刷新 false:刷新}
        flag2 = true;      //模式进入页面第一次不刷新
        flag3 = true;      //风速进入页面第一次不刷新
        openFlag = false;   //开机时，读取一次返回值  {false ：不刷新，锁定 }
        holdFlag1 = false;  //不post刷新数据标志器    {false：打开，可以刷新}
        holdFlag2 = false;  //滚动时，停止刷新数据    {false：刷新有效}
        settingFlag = false; //设置弹出收回的标志
        mGestureDetector = new GestureDetector(this); //手势识别
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("正在请求数据。。。");
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;   // 屏幕高度（像素）
//        Toast.makeText(getApplicationContext(),"屏幕尺寸"+width+"*"+height,Toast.LENGTH_LONG).show();
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_air_condition_aty);

         getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.air_aty_title);
         btnState = (SlipButton) findViewById(R.id.toggle_button);
         modify = (ImageButton) findViewById(R.id.btn_modify);
         deviceIcon = (ImageView) findViewById(R.id.icon_device_size);
         deviceTemp = (TextView) findViewById(R.id.temp_size);
         deviceMode = (ImageView) findViewById(R.id.hum_size);
         deviceWind = (ImageView) findViewById(R.id.wind_size);
         deviceState = (TextView) findViewById(R.id.state_siaz);
//         deviceInfoLayout = (RelativeLayout) findViewById(R.id.device_info);
         btnElectroAnalyz = (Button) findViewById(R.id.yongdianpingkuang);
         btnElectroQuantity = (Button) findViewById(R.id.dianliangtongji);

        final RelativeLayout deviceInfo = (RelativeLayout) findViewById(R.id.device_info);
        final RelativeLayout tempRelativity = (RelativeLayout) findViewById(R.id.temp_relativity);
        setting = (LinearLayout) findViewById(R.id.setting);
        hideSelectItem();
        airconditionlayout = (MyViewGroup) findViewById(R.id.airconditionpage);
        changeNameLayout = (RelativeLayout) findViewById(R.id.layout_modify_name);
        alertSettingLayout = (RelativeLayout) findViewById(R.id.layout_alert);
        sleepLineLayout = (RelativeLayout) findViewById(R.id.layout_sleepline);
        title = (TextView) findViewById(R.id.title_air);
        ImageButton  btBack = (ImageButton) findViewById(R.id.bt_back);
        webView = (CrossWebView) findViewById(R.id.web);
        insideTemp = (TextView) findViewById(R.id.inside_temp);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        modeSetting = (TextView) findViewById(R.id.setting_mode);
        windSetting = (TextView) findViewById(R.id.setting_wind);
        tempSetting = (TextView) findViewById(R.id.temp_setting);
        //设置WebView的参数
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);//与API level相关，不影响运行
        webView.setWebViewClient(new MyWebViewClient());
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
//                setting.setVisibility(View.INVISIBLE);
                deviceInfo.setVisibility(View.VISIBLE);           //设备状态栏显示
                tempRelativity.setVisibility(View.INVISIBLE);     //温度显示栏不可见
//                Toast.makeText(getApplicationContext(),"抽屉开",Toast.LENGTH_LONG).show();
                webView.enablecrossdomain41();                    //web浏览器跨域
                loadDateUrl(API.LOCAL_YDQS_HTML_AC + deviceId + "?" + HttpHead.forhead);
//                webView.loadUrl(API.LOCAL_YDQS_HTML_AC + deviceId + "?" + HttpHead.forhead);
            }


        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                deviceInfo.setVisibility(View.INVISIBLE); //设备状态不可见
                tempRelativity.setVisibility(View.VISIBLE);//温度状态栏重新显示
                btnElectroAnalyz.setTextColor(Color.rgb(255, 84, 0));
                btnElectroQuantity.setTextColor(Color.rgb(68, 68, 68));

            }
        });
        airconditionlayout.setOnDispatch(onDispatchListener);
//        airconditionlayout.setOnTouchListener(hideShowListener);
        changeNameLayout.setOnClickListener(changeNameListener);
        alertSettingLayout.setOnClickListener(alertListener);
        sleepLineLayout.setOnClickListener(sleepLineListener);
        btnElectroAnalyz.setOnClickListener(electroAnalyzClickListener);     //注册用电情况按键监听器
        btnElectroQuantity.setOnClickListener(eletroQuantityClickListener);  //注册电量统计按键监听器
        modify.setOnClickListener(modifyClickListener);                      //注册修改名称监听器
        btBack.setOnClickListener(backClickListener);                        //注册返回监听器
        btnState.setOnChangedListener(stateChangeListener);                  //注册开关监听器
        deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    slidingDrawer.animateClose();
            }
        });
//        /*获取从主界面得到的数据*/
//         getDataFromMainActivity();
        /* 按屏幕分辨率载入温度，模式，风速滑动选项*/
          initial();

        /*获取从主界面得到的数据*/
        getDataFromMainActivity();

        /*上传消息返回的消息处理*/
        postJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                mProgressDialog.setMessage("正在提交数据。。。");
                if (!AirConditionAty.this.isFinishing())
                    mProgressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.v("postgetJson", response.toString());
                    Boolean success = response.getBoolean("success");
                    if(success){
//                        Toast.makeText(getApplicationContext(),"提交数据成功！",Toast.LENGTH_LONG).show();
                        int state = response.getInt("status");
                        int onOff = response.getInt("onOff");
                        int inTemp = response.getInt("temp");
                        int temp = response.getInt("acTemp");
                        int mode = response.getInt("mode");
                        int speed = response.getInt("speed");
                        if (state == EXCEPTION || onOff == CLOSE){
                            coverFlowTemp.setVisibility(View.INVISIBLE);
                            coverFlowMode.setVisibility(View.INVISIBLE);
                            coverFlowWind.setVisibility(View.INVISIBLE);
                            tempSetting.setVisibility(View.INVISIBLE);
                            modeSetting.setVisibility(View.INVISIBLE);
                            windSetting.setVisibility(View.INVISIBLE);
//                            btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_open_aircondition));
                            btnState.setCheck(false);
                            holdFlag2 = false; // post得到数据后，刷新有效，否则无效。
                            if (onOff == CLOSE){}
//                            Toast.makeText(getApplicationContext(),"设备已关闭，请您开机再试",Toast.LENGTH_LONG).show();
                            else
                            Toast.makeText(getApplicationContext(),"设备连接出现异常",Toast.LENGTH_LONG).show();
                       }
                        else{
//                            int inTemp = response.getInt("temp");
//                            int temp = response.getInt("acTemp");
//                            int mode = response.getInt("mode");
//                            int speed = response.getInt("speed");
                            if (openFlag){  //只有在关机状态下。才能读取这次post返回数据
                                 openFlag = false; //执行后锁死标识
                               insideTemp.setText(""+inTemp);
                               coverFlowTemp.setSelection(temp-minTemp, true);
                                if (allowHot == 0)
                                    if (mode == 4)
                                       coverFlowMode.setSelection(3, true);
                                else
                                     coverFlowMode.setSelection(mode, true);
                               coverFlowWind.setSelection(speed, true);
                               tempSetting.setText("当前温度设置："+temp+"°");
                               setWindText(speed);
                               setModeText(mode);
                               btnState.setCheck(true);
//                               setDeviceInfo(onOff, temp, mode, speed); //设置设备信息条
                               holdFlag2 = false; //允许上传数据
                            }

                            LogUtil.v("getJson","post解析数据成功");

//                            switch (mode){
//                                case 0 : modeSetting.setText("自动");break;
//                                case 1 : modeSetting.setText("送风");break;
//                                case 2 : modeSetting.setText("加热");break;
//                                case 3 : modeSetting.setText("制冷");break;
//                                case 4 : modeSetting.setText("除湿");break;
//                            }
//                            switch (speed){
//                                case 0 : windSetting.setText("自动");break;
//                                case 1 : windSetting.setText("风速一");break;
//                                case 2 : windSetting.setText("风俗二");break;
//                                case 3 : windSetting.setText("风速三");break;
//                                case 4 : windSetting.setText("风速四");break;
//                                case 5 : windSetting.setText("风速五");break;
//                                case 6 : windSetting.setText("静音");break;
//                            }
                        }
                        setDeviceInfo(onOff, temp, mode, speed); //设置设备信息条
                    }
                    else{
                        stateDevice = "关";
                        coverFlowTemp.setVisibility(View.INVISIBLE);
                        coverFlowMode.setVisibility(View.INVISIBLE);
                        coverFlowWind.setVisibility(View.INVISIBLE);
                        tempSetting.setVisibility(View.INVISIBLE);
                        modeSetting.setVisibility(View.INVISIBLE);
                        windSetting.setVisibility(View.INVISIBLE);
                        btnState.setCheck(false);
                        Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    LogUtil.v("返回成功","post解析数据失败");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(),"提交数据失败，稍后再提交。",Toast.LENGTH_LONG).show();
                LogUtil.v("failure","postFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!AirConditionAty.this.isFinishing())
                    mProgressDialog.dismiss();
//                timerStart = true;
                holdFlag2 = false;  //允许上传数据
                    LogUtil.v("finsh","postFinish");
            }
        };
        /*获取消息得到的消息处理*/
        getDataJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.v("getDataGetJson", response.toString());
                    Boolean success = response.getJSONObject(0).getBoolean("success");
//                    String name = response.getString("name");
                     if (success){
                             int state = response.getJSONObject(0).getInt("status");
                             int onOff = response.getJSONObject(0).getInt("onOff");
                             int inTemp = response.getJSONObject(0).getInt("temp");
                             int temp = response.getJSONObject(0).getInt("acTemp");
                             int mode = response.getJSONObject(0).getInt("mode");
                             int speed = response.getJSONObject(0).getInt("speed");
                             if (state == EXCEPTION || onOff == CLOSE){
                                 stateDevice = "关";
                                 holdFlag2 = false;      //设备关机时，开启刷新数据锁
                                if (closeFlag){         //碰到关闭状态的信息，开启关闭状态不刷新
                                 closeFlag = false;
//                                 holdFlag2 = false;
                                 LogUtil.v("刷新设备信息","设备处于无连接状态");
                                 coverFlowTemp.setVisibility(View.INVISIBLE);
                                 coverFlowMode.setVisibility(View.INVISIBLE);
                                 coverFlowWind.setVisibility(View.INVISIBLE);
                                 tempSetting.setVisibility(View.INVISIBLE);
                                 modeSetting.setVisibility(View.INVISIBLE);
                                 windSetting.setVisibility(View.INVISIBLE);
//                                 btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_open_aircondition));
                                 btnState.setCheck(false);
                                }
//                            if (name != null)
//                                title.setText(name);
                            if (onOff == CLOSE){
                                nowState = CLOSE;
//                                Toast.makeText(getApplicationContext(),"设备已关闭，请您开机再试",Toast.LENGTH_LONG).show();
                                }
                            else{
                                Toast.makeText(getApplicationContext(),"设备连接出现异常",Toast.LENGTH_LONG).show();
                                nowState = EXCEPTION;
                            }
                        }
                        else{
                                 stateDevice = "开";
                                 closeFlag = true; //打开关机锁
                                 coverFlowTemp.setVisibility(View.VISIBLE);
                                 coverFlowMode.setVisibility(View.VISIBLE);
                                 coverFlowWind.setVisibility(View.VISIBLE);
                                 tempSetting.setVisibility(View.VISIBLE);
                                 modeSetting.setVisibility(View.VISIBLE);
                                 windSetting.setVisibility(View.VISIBLE);  //保持界面开启状态
//                                 btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_close_aircondition));
                                 btnState.setCheck(true);
                                if (!holdFlag2){ //触摸锁，用于阻挡操作时的刷新
//                              if(!coverFlowTemp.isSelected()){
                                holdFlag1 = true; //关闭刷新上传
                                LogUtil.v("界面刷新","holdFlag1 = true");
//                                int inTemp = response.getJSONObject(0).getInt("temp");
//                                int temp = response.getJSONObject(0).getInt("acTemp");
//                                int mode = response.getJSONObject(0).getInt("mode");
//                                int speed = response.getJSONObject(0).getInt("speed");
                                LogUtil.v("刷新设备信息","inTemp:"+inTemp+"temp:"+temp+"mode:"+mode+"speed:"+speed);
//                            if (name != null)
//                                title.setText(name);
                                    nowTemp = temp;
                                    nowMode = mode;
                                    nowWind = speed;
                                insideTemp.setText(""+inTemp);
                                coverFlowTemp.setSelection(temp - minTemp, true);
                                   if (allowHot == 0)//如果限制模式
                                      if (mode == 4)
                                        coverFlowMode.setSelection(3,true);
                                      else
                                         coverFlowMode.setSelection(mode, true);
                                    else
                                        coverFlowMode.setSelection(mode, true);
                                coverFlowWind.setSelection(speed, true);
                                tempSetting.setText("当前温度设置：" + temp + "°");
                                setWindText(speed);
                                setModeText(mode);

//                                holdFlag1 = false;
                              LogUtil.v("界面刷新完毕","holdFlag1 = false");
                                LogUtil.v("getJson","get解析数据成功");
                                }
                        }
                         setDeviceInfo(onOff, temp, mode, speed); //设置设备信息条
                    }
                    else{
                         stateDevice = "关";
                         btnState.setCheck(false);
                        Toast.makeText(getApplicationContext(),"设备连接出现问题,数据获取失败",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    LogUtil.v("刷新成功","get解析数据失败");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.v("failure","AirConditionGetDataFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
              if (!AirConditionAty.this.isFinishing())
                  mProgressDialog.dismiss();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        LogUtil.v("AC timerTask", "启动!!!!!!!!!11");
                        final RequestParams params = new RequestParams();
                        params.put("deviceId", deviceId);
                        params.put("gwId", gwId);
                        HttpUtil.get(HttpHead.head+API.GET_DEVICE_DATA, params, getDataJsonHttpResponseHandler);
                    }
                };
                   Timer timer = new Timer(true);
                if (timerStart)
                    timer.schedule(timerTask, FLASH_TIME);
                LogUtil.v("finsh","AirConditionGetDataFinish......");
    }
};
        /*延时处理，上传数据*/
           mHandler = new android.os.Handler(){
                  @Override
           public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String commandType  = msg.getData().getString("commandType");

//                mProgressDialog.show();
                int index = msg.getData().getInt("index");
                final RequestParams params = new RequestParams();
                params.put("value",index);
                params.add("commandType", commandType);
                params.put("deviceId", deviceId);
                params.put("gwId", gwId);

        if (commandType.equals("TEMP_SET"))
            if (index == coverFlowTemp.getSelectedItemId()+minTemp){
               LogUtil.v("device&&gwId",deviceId+"!!"+gwId+"!!"+msg.what);
               HttpUtil.get(HttpHead.head+ API.CHANGE_AC_STATE, params, postJsonHttpResponseHandler);

                LogUtil.v("界面数据上传后","刷新开启");
            }
        else{
              LogUtil.v("改变温度","温度发生连续改变");
            }

        if (commandType.equals("MODE_SET"))
            if (index == coverFlowMode.getSelectedItemId()){
                if (allowHot == 0)
                    if (index == 3)
                        params.put("value", 4);


                HttpUtil.get(HttpHead.head+ API.CHANGE_AC_STATE, params, postJsonHttpResponseHandler);

                LogUtil.v("界面数据上传后","刷新开启");
            }
        else{
                LogUtil.v("改变模式", "模式发生连续改变");
            }
        if (commandType.equals("FAN_SET"))
            if (index == coverFlowWind.getSelectedItemId()){

                 HttpUtil.get(HttpHead.head+ API.CHANGE_AC_STATE, params, postJsonHttpResponseHandler);

                LogUtil.v("界面数据上传后","刷新开启");
            }
            else{
              LogUtil.v("改变风速", "风速发生连续改变");
            }
        }
        };
        final RequestParams params = new RequestParams();
        params.put("deviceId",deviceId);
        params.put("gwId", gwId);
        LogUtil.v("deviceId&&gwId","deviceId:"+deviceId+"gwId:"+gwId);
        HttpUtil.get(HttpHead.head+API.GET_DEVICE_DATA, params, getDataJsonHttpResponseHandler);
}
    //手动侧滑返回主界面
    @Override
    public boolean onTouchEvent(MotionEvent event) {
         return mGestureDetector.onTouchEvent(event);
      }

    @Override
    protected void onStart() {
        super.onStart();
//        flag1 = true;
//        flag2 = true;
//        flag3 = true;
        LogUtil.v("AirConditionActivity","on Start");
//        holdFlag1 = false;
        timerStart = true;
//        final RequestParams params = new RequestParams();
//        params.put("deviceId",deviceId);
//        params.put("gwId", gwId);
//        LogUtil.v("deviceId&&gwId","deviceId:"+deviceId+"gwId:"+gwId);
//        HttpUtil.get(HttpHead.head+API.GET_DEVICE_DATA, params, getDataJsonHttpResponseHandler);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerStart =false;//销毁后，停止刷新
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){//返回按键监听
            if (slidingDrawer.isOpened())
                slidingDrawer.close();
            else{
            sendMessageBack();
            finish();
            }
            return true;
        }
        else
        return super.onKeyDown(keyCode, event);
    }


    /*温度选择监听器*/
    AdapterView.OnItemSelectedListener tempSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            LogUtil.v("温度确认",""+tempGet+"@@@@@"+coverFlowTemp.getSelectedItemId()+minTemp);
//            holdFlag2 = true;
            int temp = i+minTemp;
            coverFlowTemp.setSelection(i);
            tempSetting.setText("当前温度设置："+temp+"°");
         if (flag1)
                flag1 = false;//第一次进入不刷新
//         if (tempGet != coverFlowTemp.getSelectedItemId()+18){
         else{
               holdFlag2 = true;  //关闭刷新
//            LogUtil.v("界面滑动中","刷新无效");
               if (!holdFlag1){ //是否是刷新得到的数据
                    if (stateDevice.equals("开")){
//                         holdFlag2 = true;
                          post(i + minTemp, "TEMP_SET");
//                timerStart = false;
                         LogUtil.v("post success","post成功"+"温度"+i);
                    }
                }
             else{
                 holdFlag1 = false;
                 holdFlag2 = false;
                }
             }
       }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    /*模式选择监听器*/
    AdapterView.OnItemSelectedListener modeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (allowHot == 0){
                if (i == 3)
                    setModeText(4);
                 else
                    setModeText(i);
            }
            else
                 setModeText(i);
            coverFlowMode.setSelection(i);
//            holdFlag2 = true;
            LogUtil.v("模式确认","modeGET:"+modeGet+"@@@@@"+coverFlowMode.getSelectedItemId());
//            if (modeGet != coverFlowMode.getSelectedItemId()) {
            if (flag2)
                 flag2 = false;
            else{
                holdFlag2 = true;
//            LogUtil.v("界面滑动中","刷新无效");
               if (!holdFlag1){
                 if (stateDevice.equals("开")) {
//                     holdFlag2 = true;
                    post(i, "MODE_SET");
                    LogUtil.v("post success", "post成功" + "模式" + i);
                }
               }
                else{
                holdFlag1 = false;
                holdFlag2 = false;
               }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    /*风速选择监听器*/
    AdapterView.OnItemSelectedListener windSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            setWindText(i);
            coverFlowWind.setSelection(i);
//            holdFlag2 = true;
            LogUtil.v("风速确认","windGet:"+windGet+"@@@@@@"+coverFlowWind.getSelectedItemId());
//           if (windGet != coverFlowWind.getSelectedItemId())
            if (flag3)
                flag3 = false;
            else {
                holdFlag2 = true;
//            LogUtil.v("界面滑动中","刷新无效");
                if (!holdFlag1){
                    if (stateDevice.equals("开")) {
                        post(i, "FAN_SET");
//                        holdFlag2 = true;
                        LogUtil.v("post success", "post成功" + "风速" + i);
                    }
                }
                else{
                   holdFlag2 = false;
                   holdFlag1 = false;
                }
               }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    //温度按下监听，阻挡刷新界面
    View.OnTouchListener tempTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                holdFlag2 = true;   //锁住刷新
                holdFlag1 = false;   //打开上传通道
            LogUtil.v("tempTouch","!!!!!!!!!!!!!!!!!");
            return false;

        }
    };
    //模式按下监听器，阻挡刷新界面
    View.OnTouchListener modeTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                holdFlag2 = true;
            holdFlag1 = false;
            LogUtil.v("modeTouch","!!!!!!!!!!!!!!!!!");
            return false;
        }
    };
    //风速按下监听器，阻挡刷新
    View.OnTouchListener windTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                holdFlag2 = true;
            holdFlag1 = false;
            LogUtil.v("windTouch","!!!!!!!!!!!!!!!!!");
            return false;
        }
    };
   //返回监听器
    View.OnClickListener backClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
     if (slidingDrawer.isOpened())
            slidingDrawer.close();
     else{
         sendMessageBack();
//        Intent intent = new Intent();
//        if (id != 0)
//            intent.putExtra("id", id);
//        if (stateDevice.equals("开")) {
//            intent.putExtra("temp", (int) coverFlowTemp.getSelectedItemId());
//            intent.putExtra("hum", (int) coverFlowMode.getSelectedItemId());
//            intent.putExtra("wind", (int) coverFlowWind.getSelectedItemId());
//
//            if (modifyName != null)
//                intent.putExtra("name", modifyName);
////        Toast.makeText(getApplicationContext(),"当前的温度"+(coverFlowTemp.getSelectedItemId()+18)+"/n当前模式"+coverFlowMode.getSelectedItemId()+"/n当前风速"+coverFlowWind.getSelectedItemId(),Toast.LENGTH_LONG).show();
//
//            setResult(100, intent);
//          }
//        else{
//            intent.putExtra("temp",-1);
//            if (modifyName != null)
//                intent.putExtra("name", modifyName);
//            setResult(100, intent);
//
//        }
//         timerStart = false;
         finish();
//         timerStart = false;
     }
    }
};
   //设置框监听器
    View.OnClickListener modifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if (!settingFlag){
              animationShow();
//               setting.startAnimation(AnimationUtils.loadAnimation(AirConditionAty.this,R.anim.anim_slide_down));
//              setting.setVisibility(View.VISIBLE);
           }
            else{
               animationHide();
//               setting.startAnimation(AnimationUtils.loadAnimation(AirConditionAty.this,R.anim.anim_slid_up));
//               setting.setVisibility(View.INVISIBLE);

           }
//             changeTitleAlert();
        }
    };
    //修改名称监听
    View.OnClickListener changeNameListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeTitleAlert();
        }
    };
    //闹钟监听器
    View.OnClickListener alertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            AlertItem alertItem = new AlertItem("7:00","一，二，三，四，五，六，七",1,"23℃",4,2);
            Intent intent = new Intent(AirConditionAty.this,AlertAty.class);
            intent.putExtra("allowHot",allowHot);
            intent.putExtra("maxTemp",maxTemp);
            intent.putExtra("minTemp",minTemp);
            intent.putExtra("deviceId",deviceId);
//            intent.putExtra("alert",alertItem);
            animationHide();
//            setting.setVisibility(View.INVISIBLE);
//            modify.setBackgroundResource(R.drawable.icon_select_plus);
            startActivity(intent);
        }
    };
    //舒适曲线监听器
    View.OnClickListener sleepLineListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(AirConditionAty.this, AlertSleepAty.class);
            intent.putExtra("allowHot",allowHot);
            intent.putExtra("maxTemp",maxTemp);
            intent.putExtra("minTemp",minTemp);
            intent.putExtra("deviceId",deviceId);
            startActivity(intent);
            animationHide();
        }
    };
    /**
     * 修改名称自定义对话框
     */
    private void changeTitleAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        android.view.Window window = dialog.getWindow(); //获取窗口
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_change_title);//为窗口添加layout
        TextView textView  = (TextView) window.findViewById(R.id.this_title);
        textView.setText("修改设备名称");
        final EditText editText = (EditText) window.findViewById(R.id.name_door_and_win);
        Button confirm = (Button) window.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //确定按钮事件
                LogUtil.v("editText",editText.getText().toString());
                if (!editText.getText().toString().equals("")){
//                EditText editText = (EditText) findViewById(R.id.name_door_and_win);
                  final  TextView textView = (TextView) findViewById(R.id.title_air);
//                    textView.setText(editText.getText().toString());
                    modifyName = editText.getText().toString();
                    LogUtil.v("EditText", editText.getText().toString());
                   final RequestParams params = new RequestParams();//发送数据请求更改名字
                    params.put("deviceid",deviceId);
                    params.put("newName",modifyName);
                    HttpUtil.get(HttpHead.head+API.CHANGE_DEVICE_NAME, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try{
                            Boolean success = response.getBoolean("success");
                            String msg = response.getString("msg");
                                if (success){
                                    textView.setText(editText.getText().toString());
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                            }catch (Exception e){
                                LogUtil.v("","");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            LogUtil.v("修改名称失败",responseString);
                            Toast.makeText(getApplicationContext(),"连接超时，修改名称失败。",Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(),"修改成功！！！",Toast.LENGTH_SHORT).show();
                }
                else{
                     Toast.makeText(getApplicationContext(),"设备名称不能为空！",Toast.LENGTH_LONG).show();
               }
            }
        });
        Button cancel = (Button) window.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });//取消按钮响应事件
    }
    //开关状态监听器
    SlipButton.OnChangedListener stateChangeListener = new SlipButton.OnChangedListener() {
        @Override
        public void OnChanged(boolean CheckState) {

            if (stateDevice.equals("关")){  //打开动作响应
//                btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_close_aircondition));
                stateDevice = "开";
                openFlag = true;
                coverFlowTemp.setVisibility(View.VISIBLE);
                coverFlowMode.setVisibility(View.VISIBLE);
                coverFlowWind.setVisibility(View.VISIBLE);
                tempSetting.setVisibility(View.VISIBLE);
                modeSetting.setVisibility(View.VISIBLE);
                windSetting.setVisibility(View.VISIBLE);
                final RequestParams params = new RequestParams();
                params.put("value", "1");
                params.put("deviceId", deviceId);
                params.put("commandType","ON");
                params.put("gwId", gwId);

//                mProgressDialog.show();
                String url = HttpHead.head+ API.CHANGE_AC_STATE +"value=1"+"&deviceId="+deviceId+"&commandType=ON"+"&gwId="+gwId;
                LogUtil.v("信息",url);
                HttpUtil.get(HttpHead.head+ API.CHANGE_AC_STATE, params, postJsonHttpResponseHandler);
                //http://192.168.1.90:8089/spmsuc/app/ChangeAcState?value=1&deviceId=12501&commandType=ON&gwId=10000
              // http://192.168.1.90:8089/spmsuc/app/changeAcState?value=1&deviceId=12501&commandType=ON&gwId=10000
            }
            else{ //执行关闭动作
//                btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_open_aircondition));
                stateDevice = "关";
//                coverFlowTemp.setVisibility(View.INVISIBLE);
//                coverFlowMode.setVisibility(View.INVISIBLE);
//                coverFlowWind.setVisibility(View.INVISIBLE);
//                tempSetting.setVisibility(View.INVISIBLE);
//                modeSetting.setVisibility(View.INVISIBLE);
//                windSetting.setVisibility(View.INVISIBLE);
                final RequestParams params = new RequestParams();
                params.put("commandType", "OFF");
                params.put("value", "0");
                params.put("deviceId", deviceId);
                params.put("gwId", gwId);
               if (!AirConditionAty.this.isFinishing())
                mProgressDialog.show();
                HttpUtil.get(HttpHead.head+ API.CHANGE_AC_STATE, params, postJsonHttpResponseHandler);
            }
        }
    };
    View.OnClickListener electroAnalyzClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            btnElectroAnalyz.setTextColor(Color.rgb(255,84,0));
            btnElectroQuantity.setTextColor(Color.rgb(68,68,68));
            webView.enablecrossdomain41();                    //web浏览器跨域
            loadDateUrl(API.LOCAL_YDQS_HTML_AC + deviceId+"?"+HttpHead.forhead);
//            webView.loadUrl(API.LOCAL_YDQS_HTML_AC + deviceId+"?"+HttpHead.forhead);
        }
    };
    View.OnClickListener eletroQuantityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            btnElectroQuantity.setTextColor(Color.rgb(255,84,0));
            btnElectroAnalyz.setTextColor(Color.rgb(68,68,68));
            webView.enablecrossdomain41();                  //web浏览器跨域
            loadDateUrl(API.LOCAL_DLTJ_HTML + deviceId+"?" +HttpHead.forhead);
//            webView.loadUrl(API.LOCAL_DLTJ_HTML + deviceId+"?" +HttpHead.forhead);
        }
    };
//    View.OnClickListener hideShowListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//          if (settingFlag){
//              animationHide();
//          }
//        }
//    };
MyViewGroup.OnDispatchTouchEvent onDispatchListener = new MyViewGroup.OnDispatchTouchEvent() {
    @Override
    public boolean onDispatch(RelativeLayout viewGroup, MotionEvent me) {
        if (settingFlag && me.getAction() == MotionEvent.ACTION_DOWN){
            animationHide();
            return true;
        }
        else
            return false;
    }
};

    /**
     * 上传数据延时两秒
     * @param index  数据
     * @param commandType  数据的类型
     */
    public void post(int index, String commandType){

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("commandType", commandType);
            bundle.putInt("index", index);
            message.setData(bundle);
           mHandler.sendMessageDelayed(message, 0);

            LogUtil.v("send message",""+index);


    }

    /**
     * 设置模式切换提醒
     * @param mode  模式的位置
     */
    private void setModeText(int mode){
        switch (mode){
            case 0 : modeSetting.setText("当前模式设置："+"自动");break;
            case 1 : modeSetting.setText("当前模式设置："+"送风");break;
            case 2 : modeSetting.setText("当前模式设置："+"制冷");break;
            case 3 : modeSetting.setText("当前模式设置："+"加热");break;
            case 4 : modeSetting.setText("当前模式设置："+"除湿");break;
        }
    }


    /**
     * 设置风速的设置提醒
     * @param wind 风速位置
     */
    private void setWindText(int wind){
        switch (wind){
            case 0 : windSetting.setText("当前风速设置："+"自动");break;
            case 1 : windSetting.setText("当前风速设置："+"风速一");break;
            case 2 : windSetting.setText("当前风速设置："+"风速二");break;
            case 3 : windSetting.setText("当前风速设置："+"风速三");break;
            case 4 : windSetting.setText("当前风速设置："+"风速四");break;
            case 5 : windSetting.setText("当前风速设置："+"风速五");break;
            case 6 : windSetting.setText("当前风速设置："+"静音");break;
        }
    }

    /**
     * 返回时传递数据
     */
    private void sendMessageBack(){
        Intent intent = new Intent();
        if (id != 0)
            intent.putExtra("id", id);
        if (stateDevice.equals("开")) {
            intent.putExtra("temp", (int) coverFlowTemp.getSelectedItemId());
            intent.putExtra("hum", (int) coverFlowMode.getSelectedItemId());
            intent.putExtra("wind", (int) coverFlowWind.getSelectedItemId());

            if (modifyName != null)
                intent.putExtra("name", modifyName);
//        Toast.makeText(getApplicationContext(),"当前的温度"+(coverFlowTemp.getSelectedItemId()+18)+"/n当前模式"+coverFlowMode.getSelectedItemId()+"/n当前风速"+coverFlowWind.getSelectedItemId(),Toast.LENGTH_LONG).show();

            setResult(100, intent);
        }
        else{
            intent.putExtra("temp",-1);
            if (modifyName != null)
                intent.putExtra("name", modifyName);
            setResult(100, intent);

        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }
    //滑动响应时间，从左向右滑动打开上层的Activity
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
//        int previousX = (int) motionEvent.getX();
//        int currentX = (int) motionEvent2.getX();
//        if (currentX - previousX >= 150){
//            sendMessageBack();
//            finish();
//        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
//        int previousX = (int) motionEvent.getX();
//        int currentX = (int) motionEvent2.getX();
//        if (currentX - previousX >= 150){
//            sendMessageBack();
//            finish();
//        }
//       if (v > 50){
//           sendMessageBack();
//            finish();
//       }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    /**
     * 当抽屉窗口上拉后，最上层显示当前设备的信息栏
     * @param state  状态
     * @param temp   温度
     * @param mode   模式
     * @param wind   风速
     */
    private void setDeviceInfo(String state, String temp, int mode, int wind){
        int windId,humId;
        if (state.equals("关")){
           deviceIcon.setBackgroundResource(R.drawable.icon_air_conditioning_close);
           deviceState.setText("关");
           deviceState.setTextColor(Color.rgb(99, 99, 99));
        }
        else{
            deviceIcon.setBackgroundResource(R.drawable.icon_air_condition);
            deviceState.setText("开");
            deviceState.setTextColor(Color.rgb(255,84,0));
            deviceTemp.setText(temp+"°");
            switch (mode){
                case 0 : humId = R.drawable.icon_mode_auto;break;
                case 1 : humId = R.drawable.icon_mode_sendwind;break;
                case 2 : humId = R.drawable.icon_mode_cold;break;
                case 3 : humId = R.drawable.icon_mode_hot;break;
                case 4 : humId = R.drawable.icon_mode_dehumidification;break;
                default: humId = R.drawable.icon_snow;
            }
            switch (wind){
                case 0 : windId = R.drawable.icon_wind_auto;break;
                case 1 : windId = R.drawable.icon_wind1;break;
                case 2 : windId = R.drawable.icon_wind2;break;
                case 3 : windId = R.drawable.icon_wind3;break;
                case 4 : windId = R.drawable.icon_wind4;break;
                case 5 : windId = R.drawable.icon_wind5;break;
                case 6 : windId = R.drawable.icon_wind_silent;break;
                default: windId = R.drawable.icon_wind;

            }
            deviceMode.setBackgroundResource(humId);
            deviceWind.setBackgroundResource(windId);
        }

    }

    /**
     * 重载上面的函数
     * @param onOff  开关状态
     * @param temp   温度
     * @param mode   模式
     * @param wind   风速
     */
    private void setDeviceInfo(int onOff, int temp, int mode, int wind ){
        int windId,humId;
        if (onOff == 0){
            deviceIcon.setBackgroundResource(R.drawable.icon_air_conditioning_close);
            deviceState.setText("关");
            deviceTemp.setText("");
            deviceState.setTextColor(Color.rgb(99, 99, 99));
            deviceMode.setVisibility(View.INVISIBLE);
            deviceWind.setVisibility(View.INVISIBLE);
        }
        else{
            deviceIcon.setBackgroundResource(R.drawable.icon_air_condition);
            deviceState.setText("开");
            deviceState.setTextColor(Color.rgb(255,84,0));
            deviceTemp.setText(temp+"°");
            deviceMode.setVisibility(View.VISIBLE);
            deviceWind.setVisibility(View.VISIBLE);
            switch (mode){
                case 0 : humId = R.drawable.icon_mode_auto;break;
                case 1 : humId = R.drawable.icon_mode_sendwind;break;
                case 2 : humId = R.drawable.icon_mode_cold;break;
                case 3 : humId = R.drawable.icon_mode_hot;break;
                case 4 : humId = R.drawable.icon_mode_dehumidification;break;
                default: humId = R.drawable.icon_snow;
            }
            switch (wind){
                case 0 : windId = R.drawable.icon_wind_auto;break;
                case 1 : windId = R.drawable.icon_wind1;break;
                case 2 : windId = R.drawable.icon_wind2;break;
                case 3 : windId = R.drawable.icon_wind3;break;
                case 4 : windId = R.drawable.icon_wind4;break;
                case 5 : windId = R.drawable.icon_wind5;break;
                case 6 : windId = R.drawable.icon_wind_silent;break;
                default: windId = R.drawable.icon_wind;

        }
            deviceMode.setBackgroundResource(humId);
            deviceWind.setBackgroundResource(windId);
        }
    }

    /**
     * 从主界面获得空调的数据
     */
    public void getDataFromMainActivity(){
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        stateDevice = bundle.getString("state");
        id = bundle.getInt("id");
        deviceId = bundle.getString("deviceId");
        gwId = bundle.getString("deviceGwId");
        int inTemp = bundle.getInt("temp");
        insideTemp.setText(""+inTemp);
        LogUtil.v("deviD",""+deviceId+"######"+gwId);
        title.setText(name);
        if (stateDevice.equals("关")){
            setDeviceOff();
            coverFlowTemp.setAdapter(tempAdapter);
            coverFlowMode.setAdapter(modeAdapter);
            coverFlowWind.setAdapter(windAdapter);
            closeFlag = false; //如果处于关机状态时，刷新的INVISIBLE无效
            coverFlowTemp.setVisibility(View.INVISIBLE);
            coverFlowMode.setVisibility(View.INVISIBLE);
            coverFlowWind.setVisibility(View.INVISIBLE);
            tempSetting.setVisibility(View.INVISIBLE);
            modeSetting.setVisibility(View.INVISIBLE);
            windSetting.setVisibility(View.INVISIBLE);
//              btnState.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_open_aircondition));
            btnState.setCheck(false);
        }
        else{
            setDeviceOn();
            closeFlag = true; //如果处于开机状态，关机刷新有效
            tempGet = bundle.getInt("temp");
            modeGet = bundle.getInt("hum");
            windGet = bundle.getInt("wind");
            LogUtil.v("空调数据","temp:"+tempGet+"hum:"+modeGet+"wind:"+windGet);
            coverFlowTemp.setAdapter(tempAdapter);
            if (tempGet < minTemp)
                coverFlowTemp.setSelection(0,true);
            else if (tempGet > maxTemp)
                coverFlowTemp.setSelection(maxTemp - minTemp,true);
            else
                coverFlowTemp.setSelection(tempGet - minTemp, true);
            coverFlowMode.setAdapter(modeAdapter);
            if (allowHot == 0){
                if (modeGet == 4)
                    coverFlowMode.setSelection(3, true);
                else
                    coverFlowMode.setSelection(modeGet, true);
            }
            else
                coverFlowMode.setSelection(modeGet, true);
            coverFlowWind.setAdapter(windAdapter);
            coverFlowWind.setSelection(windGet,true);
            tempSetting.setText("当前温度设置："+tempGet+"°");
            setModeText(modeGet);
            setWindText(windGet);
            setDeviceInfo(stateDevice,""+tempGet, modeGet, windGet);//设置设备信息条
            btnState.setCheck(true);
        }
    }
    private void initial(){
       Bundle bundle = getIntent().getExtras();
       if (null != bundle.getString("error")){
//           Toast.makeText(this,bundle.getString("error"),Toast.LENGTH_LONG).show();
       }
       maxTemp = bundle.getInt("maxTemp");
       minTemp = bundle.getInt("minTemp");
       allowHot = bundle.getInt("allowHeat");
       int startNumber = minTemp - 15;
       int length = maxTemp - minTemp + 1;
       LogUtil.v("maxTemp:minTemp:length:startNumber",""+maxTemp+"   "+minTemp+"   "+length+"   "+startNumber);
       mImageTempLimitedRecourseId = new Integer[length];
       mImageModeLimaitedRecourseId = new Integer[4];
       System.arraycopy(mImageTempRecourseId, startNumber, mImageTempLimitedRecourseId, 0, length);
//       for (int i = 0; i<length; i++)
//            mImageTempLimitedRecourseId[i] = mImageTempRecourseId[startNumber+i];
      if (allowHot == 0){//无制热模式
           System.arraycopy(mImageModeRecourseId,0,mImageModeLimaitedRecourseId,0,3);
//           for (int i = 0; i < 3; i++)
//               mImageModeLimaitedRecourseId[i] = mImageModeRecourseId[i];
           mImageModeLimaitedRecourseId [3] = mImageModeRecourseId [4];
          for (int i = 0; i < 4; i++)
              LogUtil.v("mImageModeLimaitedRecourseId", ""+mImageModeLimaitedRecourseId[i]);
          for(int i = 0; i < 5; i++)
              LogUtil.v("mImageModeRecourseId", ""+mImageModeRecourseId[i]);


       }

        /* 按屏幕分辨率载入温度，模式，风速滑动选项*/
         tempAdapter = new ImageAdapter(this,mImageTempLimitedRecourseId);
        if (allowHot == 0){
         modeAdapter = new ImageAdapter(this,mImageModeLimaitedRecourseId);
        }
        else
         modeAdapter = new ImageAdapter(this,mImageModeRecourseId);
        windAdapter = new ImageAdapter(this,mImageWindRecourseId);
        coverFlowTemp = (CoverFlow) findViewById(R.id.temp_wheel_num);
        coverFlowMode = (CoverFlow) findViewById(R.id.mode_wheel);
        coverFlowWind = (CoverFlow) findViewById(R.id.wind_wheel);
        coverFlowTemp.setOnTouchListener(tempTouchListener);
        coverFlowMode.setOnTouchListener(modeTouchListener);
        coverFlowWind.setOnTouchListener(windTouchListener);
        coverFlowTemp.setCallbackDuringFling(false);
        coverFlowTemp.setOnItemSelectedListener(tempSelectedListener);
        coverFlowMode.setCallbackDuringFling(false);
        coverFlowWind.setOnItemSelectedListener(windSelectedListener);
        coverFlowWind.setCallbackDuringFling(false);
        coverFlowMode.setOnItemSelectedListener(modeSelectedListener);
        //大小手机屏幕适配滑动温度，模式，风速滚动条
        CoverFlow.LayoutParams smallViewSize = new CoverFlow.LayoutParams(SMALL_WIDTH, SMALL_HIGHT);
        CoverFlow.LayoutParams bigViewSize = new CoverFlow.LayoutParams(BIG_WIDTH, BIG_HIGHT);
        if (width < PHONE_SMALL_WIDTH && height < PHONE_SMALL_HIGHT){
            tempAdapter.setCoverFlowSize(smallViewSize);
            LogUtil.v("小屏幕","!!!!!!!!!!!");}
        if (width > PHONE_BIG_WIDTH){
            tempAdapter.setCoverFlowSize(bigViewSize);
            LogUtil.v("大屏幕","!!!!!!!");
        }

        coverFlowTemp.setBackgroundColor(Color.rgb(RGB_COLOR,RGB_COLOR,RGB_COLOR));
        coverFlowTemp.setAnimationDuration(ANIMATION_DURACION);
        coverFlowTemp.setAlphaMode(true);
        coverFlowTemp.setMaxZoom(SMALL_ZOOM_SIZE);
        coverFlowTemp.setMaxRotationAngle(ROTATION_ANGLE);
//        coverFlowTemp.setAdapter(tempAdapter);
//        coverFlowTemp.setSelection(10, true);

        if (width < PHONE_SMALL_WIDTH && height < PHONE_SMALL_HIGHT){
            coverFlowMode.setMaxZoom(BIG_ZOOM_SIZE);
            modeAdapter.setCoverFlowSize(smallViewSize);
            LogUtil.v("小屏幕","!!!!!!!!!!!");}
        if (width > PHONE_BIG_WIDTH){
            modeAdapter.setCoverFlowSize(bigViewSize);
            LogUtil.v("大屏幕","!!!!!!!");
        }
        coverFlowMode.setBackgroundColor(Color.rgb(RGB_COLOR,RGB_COLOR,RGB_COLOR));
        coverFlowMode.setAnimationDuration(ANIMATION_DURACION);
        coverFlowMode.setAlphaMode(true);
//        coverFlowMode.setAdapter(modeAdapter);
//        coverFlowMode.setSelection(2, true);

        if (width < PHONE_SMALL_WIDTH && height < PHONE_SMALL_HIGHT){
            coverFlowWind.setMaxZoom(BIG_ZOOM_SIZE);
            windAdapter.setCoverFlowSize(smallViewSize);
            LogUtil.v("小屏幕","!!!!!!!!!!");}
        if (width > PHONE_BIG_WIDTH){
            windAdapter.setCoverFlowSize(bigViewSize);
            LogUtil.v("大屏幕","!!!!!!!");
        }
        coverFlowWind.setBackgroundColor(Color.rgb(RGB_COLOR,RGB_COLOR,RGB_COLOR));
        coverFlowWind.setAnimationDuration(ANIMATION_DURACION);
        coverFlowWind.setAlphaMode(true);
//        coverFlowWind.setAdapter(windAdapter);
//        coverFlowWind.setSelection(2,true);
    }
    public void setDeviceOff(){
        nowState = CLOSE;
    }
    public void setDeviceOn(){
        nowState = OPEN;
        nowTemp = tempGet;
        nowMode = modeGet;
        nowWind = windGet;
    }
    public void hideSelectItem(){
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(setting,"Y",0F,-250F).setDuration(1);
        objectAnimator1.start();
    }
    public void animationShow(){
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(setting,"Y",-250F,0F).setDuration(500);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(airconditionlayout,"alpha",1F,0.5F).setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(objectAnimator1,objectAnimator2);
        set.start();
        modify.setBackgroundResource(R.drawable.icon_select_x);
        settingFlag = true;
    }
    public void animationHide(){
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(setting,"Y",0F,-250F).setDuration(500);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(airconditionlayout,"alpha",0.5F,1F).setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(objectAnimator1,objectAnimator2);
        set.start();
        settingFlag = false;
        modify.setBackgroundResource(R.drawable.icon_menu);
    }
    private void loadDateUrl(String url){
        webView.loadUrl(url);
        btnElectroAnalyz.setClickable(false);
        btnElectroQuantity.setClickable(false);
    }
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("loaded")){
                btnElectroQuantity.setClickable(true);
                btnElectroAnalyz.setClickable(true);
                LogUtil.v("loaded","加载完成，打开按钮锁");
                return true;
            }
            else {
                return false;
            }
        }
    }
}
