package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.adpter.AlertItemAdapter;
import com.harmazing.intelligentpow.model.AlertItem;
import com.harmazing.intelligentpow.model.AlertItemUpdate;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.GsonUtil;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.CoverFlow;
import com.harmazing.intelligentpow.view.ImageAdapter;
import com.harmazing.intelligentpow.view.SlipButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by jtl on 2015/4/14.
 * 定时设置状态界面
 */
public class AlertSettingAty2 extends Activity {
    ImageView btnBack;
    private TextView tempSetting, modeSetting, windSetting; //温度，风速，模式提示
    private CoverFlow coverFlowTemp,coverFlowMode,coverFlowWind;   //温度，模式， 风速的Gallery
    private SlipButton btnState;                 //  开关键
    private Button btnConfirm;//提交数据
    private RelativeLayout btnTimePicker,btnDayPicker;
    ImageAdapter tempAdapter;
    ImageAdapter modeAdapter;
    ImageAdapter windAdapter;
    TextView txTime,txWeek;
    private int width,height; // 屏幕高度宽度
    private int maxTemp = 35;           //温度范围
    private int minTemp = 15;           //温度范围
    private int allowHot = 1;           //有制热模式
    private String deviceId;
    private int type,from;
    private final int DAY_DIALOG = 4;
    boolean flags[] = {false,false,false,false,false,false,false};
    /*温度，模式，风速档位*/
    private Integer [] mImageWindRecourseId = {R.drawable.icon_mode_auto,R.drawable.icon_wind1,R.drawable.icon_wind2,R.drawable.icon_wind3,R.drawable.icon_wind4,R.drawable.icon_wind5,R.drawable.icon_wind_silent};
    private Integer [] mImageTempRecourseId = {R.drawable.degree_15,R.drawable.degree_16,R.drawable.degree_17,R.drawable.degree_18,R.drawable.degree_19,R.drawable.degree_20,R.drawable.degree_21,R.drawable.degree_22,R.drawable.degree_23,R.drawable.degree_24,R.drawable.degree_25,R.drawable.degree_26,R.drawable.degree_27,R.drawable.degree_28,R.drawable.degree_29,R.drawable.degree_30,R.drawable.degree_31,R.drawable.degree_32,R.drawable.degree_33,R.drawable.degree_34,R.drawable.degree_35};
    private Integer [] mImageModeRecourseId = {R.drawable.icon_mode_auto,R.drawable.icon_mode_sendwind,R.drawable.icon_mode_cold,R.drawable.icon_mode_hot,R.drawable.icon_mode_dehumidification};
    private Integer [] mImageTempLimitedRecourseId ; //限制温度设置，实际温度范围载入
    private Integer [] mImageModeLimaitedRecourseId ; //限制模式
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
    /*添加 查看*/
    public final static int CLOCK = 4;
    public final static int SLEEP = 3;
    public final static int ADD = 1;
    public final static int EDIT = 0;
    //当前设定的状态
    private AlertItemUpdate nowAlertItem = new AlertItemUpdate();
//    private String url = HttpHead.head + "UpdateShowTimingSet";
    private int hour, min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;   // 屏幕高度（像素）
        setContentView(R.layout.activity_alert_setting);
        //界面图标
        findViews();

        //初始化状态选择器
        initial();

        //设置选择器当前状态
        if (from == ADD)
             getStateFromAC();//添加，现在的空调状态
        else
             getStateFromAA();//添加，从AlertActiity获取空调状态

        //添加点击事件
        setOnclickLisenter();
    }
    //界面图标
    public void findViews(){
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnState = (SlipButton) findViewById(R.id.toggle_button);
        //三种状态选择提示
        modeSetting = (TextView) findViewById(R.id.setting_mode);
        windSetting = (TextView) findViewById(R.id.setting_wind);
        tempSetting = (TextView) findViewById(R.id.temp_setting);
        //时间，重复选择器
        btnTimePicker = (RelativeLayout) findViewById(R.id.time_setting);
        btnDayPicker = (RelativeLayout) findViewById(R.id.week_setting);

        //时间，周期
        txTime = (TextView) findViewById(R.id.time_spc);
        txWeek = (TextView) findViewById(R.id.week_spc);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);

    }

    //点击事件
    public void setOnclickLisenter(){
        //返回监听
         btnBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 finish();
             }
         });
        //状态监听
        btnState.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if (CheckState){
                    setOn();
                    nowAlertItem.getClockSetting().setOn_off(OPEN);
                }
                else{
                    setOff();
                    nowAlertItem.getClockSetting().setOn_off(CLOSE);
                }
            }
        });
        //重复选择器
        btnDayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DAY_DIALOG);
            }
        });
        //时间选择器
       btnTimePicker.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (getIntent().getExtras().getInt("from") == EDIT)
                   showTimePickerDialog(hour,min);
               else
                   showTimePickerDialog(0,0);
           }
       });
        //提交数据
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowAlertItem.getClockSetting().setStartend(1);
                String jsonString = GsonUtil.bean2Json(nowAlertItem);
                LogUtil.v("jsonString", HttpHead.head + API.UPDATE_ADD_ALERT + jsonString);
                if (!txTime.getText().toString().equals("未设置") && !txWeek.getText().toString().equals("未设置") &&
                        !txTime.getText().toString().equals("") && !txWeek.getText().toString().equals("")) {
                    if (type == CLOCK) {
                        httpPost(HttpHead.head + API.UPDATE_ADD_ALERT, jsonString);
                    } else {
                        LogUtil.v("httpPosturl", HttpHead.head + API.ADD_CURVES_CLOCKS);
                        httpPost(HttpHead.head + API.ADD_CURVES_CLOCKS, jsonString);
                    }
                }else{
                    Toast.makeText(AlertSettingAty2.this,"请设置一个时间并添加重复周期",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void httpPost(String s, String jsonString) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存设置。。。");
        HttpUtil.post(AlertSettingAty2.this, s, jsonString, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                if (!AlertSettingAty2.this.isFinishing()){
                    progressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                if (!AlertSettingAty2.this.isFinishing()){
                    progressDialog.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("post json succ",statusCode+response.toString());
                Toast.makeText(getApplicationContext(),"设置成功",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("post json fail",statusCode+responseString);
                Toast.makeText(getApplicationContext(),"设置失败，服务器打烊了。。。",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /*温度选择监听器*/
    AdapterView.OnItemSelectedListener tempSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            LogUtil.v("温度确认",""+"@@@@@"+coverFlowTemp.getSelectedItemId()+minTemp);
            int temp = i+minTemp;
            coverFlowTemp.setSelection(i);
            tempSetting.setText("当前温度设置："+temp+"°");
            nowAlertItem.getClockSetting().setTemp(temp + "");
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
            nowAlertItem.getClockSetting().setMode(i);
//            holdFlag2 = true;
            LogUtil.v("模式确认","modeGET:"+"@@@@@"+coverFlowMode.getSelectedItemId());
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
            nowAlertItem.getClockSetting().setWindspeed(i);
//            holdFlag2 = true;
            LogUtil.v("风速确认","windGet:"+"@@@@@@"+coverFlowWind.getSelectedItemId());
        }
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private void initial(){
        try {
            getDataFromBundle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initAlertItem();
        int startNumber = minTemp - 15;
        int length = maxTemp - minTemp + 1;
        LogUtil.v("maxTemp:minTemp:length:startNumber", "" + maxTemp + "   " + minTemp + "   " + length + "   " + startNumber);
        mImageTempLimitedRecourseId = new Integer[length];
        mImageModeLimaitedRecourseId = new Integer[4];
        System.arraycopy(mImageTempRecourseId, startNumber, mImageTempLimitedRecourseId, 0, length);
        if (allowHot == 0){//无制热模式
            System.arraycopy(mImageModeRecourseId,0,mImageModeLimaitedRecourseId,0,3);
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
        coverFlowTemp.setAnimationDuration(ANIMATION_DURACION);
        coverFlowTemp.setAlphaMode(true);
        coverFlowTemp.setMaxZoom(SMALL_ZOOM_SIZE);
        coverFlowTemp.setMaxRotationAngle(ROTATION_ANGLE);
        if (width < PHONE_SMALL_WIDTH && height < PHONE_SMALL_HIGHT){
            coverFlowMode.setMaxZoom(BIG_ZOOM_SIZE);
            modeAdapter.setCoverFlowSize(smallViewSize);
            LogUtil.v("小屏幕","!!!!!!!!!!!");}
        if (width > PHONE_BIG_WIDTH){
            modeAdapter.setCoverFlowSize(bigViewSize);
            LogUtil.v("大屏幕","!!!!!!!");
        }
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
//        coverFlowWind.setBackgroundColor(Color.rgb(RGB_COLOR,RGB_COLOR,RGB_COLOR));
        coverFlowWind.setAnimationDuration(ANIMATION_DURACION);
        coverFlowWind.setAlphaMode(true);
//        coverFlowWind.setAdapter(windAdapter);
//        coverFlowWind.setSelection(2,true);

    }

    private void initAlertItem() {
        nowAlertItem.setUserId(AppConfig.getInstance().getUserId());
        nowAlertItem.setDeviceId(deviceId);
        for (int l = 0; l < flags.length; l++)
            if (flags[l]){
                setWeekChecked(l);
            }
            else{
                setWeekUnchecked(l);
            }
    }

    private void getDataFromBundle() throws Exception{
        Bundle bundle = getIntent().getExtras();
        maxTemp = bundle.getInt("maxTemp");
        minTemp = bundle.getInt("minTemp");
        allowHot = bundle.getInt("allowHot");
        deviceId = bundle.getString("deviceId");
        type = bundle.getInt("type");
        from = bundle.getInt("from");
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

    public void getStateFromAA(){
        AlertItem alertItem = (AlertItem) getIntent().getExtras().getSerializable("alertItem");
        nowAlertItem.getClockSetting().setId(alertItem.getId());
        String time = alertItem.getClocking();
        LogUtil.v("time",time);
        setTimeFromAA(time);
        txTime.setText(time);
        txWeek.setText(AlertItemAdapter.getTimeSetting(alertItem,flags));
        initAlertItem();
        if (alertItem.getOn_off() == CLOSE){
            setOff();
            nowAlertItem.getClockSetting().setOn_off(CLOSE);
            coverFlowTemp.setAdapter(tempAdapter);
            coverFlowMode.setAdapter(modeAdapter);
            coverFlowWind.setAdapter(windAdapter);
            btnState.setCheck(false);
        }
        else{
            int temp = Integer.parseInt(alertItem.getTemp());
            int mode = alertItem.getMode();
            int speed = alertItem.getWindspeed();
            nowAlertItem.getClockSetting().setOn_off(OPEN);
            showTempWindMode(temp,speed,mode);
        }
    }

    private void setTimeFromAA(String time) {
        LogUtil.v("time in set",time);
        if (!time.equals("null")){
            hour = Integer.parseInt(time.split(":")[0]);
            min = Integer.parseInt(time.split(":")[1]);
            nowAlertItem.getClockSetting().setClocking(hour+":"+min);
            LogUtil.v("hour : min",hour+":"+min);
        }
        else{
            LogUtil.v("hour : min",null);
        }
    }

    public void getStateFromAC(){
        LogUtil.v("设备状态","nowState:"+AirConditionAty.nowState+"nowTemp:"+ AirConditionAty.nowTemp+"nowMode:"+AirConditionAty.nowMode+"nowWind:"+AirConditionAty.nowWind);
        if (AirConditionAty.nowState == CLOSE){
            setOff();
            coverFlowTemp.setAdapter(tempAdapter);
            coverFlowMode.setAdapter(modeAdapter);
            coverFlowWind.setAdapter(windAdapter);
            btnState.setCheck(false);
            nowAlertItem.getClockSetting().setOn_off(CLOSE);
        }
        else{
             int temp = AirConditionAty.nowTemp;
             int mode = AirConditionAty.nowMode;
             int speed = AirConditionAty.nowWind;
             nowAlertItem.getClockSetting().setOn_off(OPEN);
             showTempWindMode(temp,speed,mode);
        }
    }
    public void setOn(){
        coverFlowTemp.setVisibility(View.VISIBLE);
        coverFlowMode.setVisibility(View.VISIBLE);
        coverFlowWind.setVisibility(View.VISIBLE);
        tempSetting.setVisibility(View.VISIBLE);
        modeSetting.setVisibility(View.VISIBLE);
        windSetting.setVisibility(View.VISIBLE);
    }
    public void setOff(){
        coverFlowTemp.setVisibility(View.INVISIBLE);
        coverFlowMode.setVisibility(View.INVISIBLE);
        coverFlowWind.setVisibility(View.INVISIBLE);
        tempSetting.setVisibility(View.INVISIBLE);
        modeSetting.setVisibility(View.INVISIBLE);
        windSetting.setVisibility(View.INVISIBLE);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id){
            case DAY_DIALOG :
                builder.setTitle("请选择重复日期");
                builder.setMultiChoiceItems(R.array.day, flags, null);
                DialogInterface.OnMultiChoiceClickListener clickListener3 = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        for (int l = 0; l < flags.length; l++)
                            flags[i] = b;
                    }
                };
                builder.setMultiChoiceItems(R.array.day, flags, clickListener3);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String day = "";
                        for (int l = 0; l < flags.length; l++)
                            if (flags[l]){
//                                day += " "+getResources().getStringArray(R.array.day)[l];
                                day += setWeekChecked(l);
                            }
                            else{
                                setWeekUnchecked(l);
                            }
                        txWeek.setText(day);
                        Toast.makeText(getApplicationContext(),day,Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                break;
        }
        dialog = builder.create();
        return dialog;
    }
    public void showTimePickerDialog(int h, int m) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //如果传入0，则取当时时间戳
        if (h == 0 && m ==0){
            hour = h;
            minute = m;
        }
        final TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker arg0, int hour, int minute) {
                String time;
                System.out.println(hour + ":" + minute);
                if (minute < 10)
                    time = hour+":"+"0"+minute;
                else
                    time = hour + ":" + minute;
                nowAlertItem.getClockSetting().setClocking(time);
                txTime.setText(time);
//                Toast.makeText(getApplicationContext(),hour + ":" + minute,Toast.LENGTH_LONG).show();
            }
        }, hour, minute, true);
        dialog.setTitle("请设置时间");
//        dialog.setButton(Dialog.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }
    public void showTempWindMode(int temp, int speed, int mode){
        coverFlowTemp.setAdapter(tempAdapter);
        coverFlowTemp.setSelection(temp - minTemp, true);
        coverFlowMode.setAdapter(modeAdapter);
        if (allowHot == 0)//如果限制模式
            if (mode == 4)
                coverFlowMode.setSelection(3,true);
            else
                coverFlowMode.setSelection(mode, true);
        else
            coverFlowMode.setSelection(mode, true);
        coverFlowWind.setAdapter(windAdapter);
        coverFlowWind.setSelection(speed, true);
        tempSetting.setText("当前温度设置：" + temp + "°");
        setWindText(speed);
        setModeText(mode);
        btnState.setCheck(true);
    }
    public String setWeekChecked(int i){
        String s = "";
        switch (i){
            case 0: s = "一 "; nowAlertItem.getClockSetting().setMonday(OPEN);break;
            case 1: s = "二 "; nowAlertItem.getClockSetting().setTuesday(OPEN);break;
            case 2: s = "三 "; nowAlertItem.getClockSetting().setWednesday(OPEN);break;
            case 3: s = "四 "; nowAlertItem.getClockSetting().setThursday(OPEN);break;
            case 4: s = "五 "; nowAlertItem.getClockSetting().setFriday(OPEN);break;
            case 5: s = "六 "; nowAlertItem.getClockSetting().setSaturday(OPEN);break;
            case 6: s = "日 "; nowAlertItem.getClockSetting().setSunday(OPEN);break;
        }
        return s;
    }
    public void setWeekUnchecked(int i){
        switch (i){
            case 0:  nowAlertItem.getClockSetting().setMonday(CLOSE);break;
            case 1:  nowAlertItem.getClockSetting().setTuesday(CLOSE);break;
            case 2:  nowAlertItem.getClockSetting().setWednesday(CLOSE);break;
            case 3:  nowAlertItem.getClockSetting().setThursday(CLOSE);break;
            case 4:  nowAlertItem.getClockSetting().setFriday(CLOSE);break;
            case 5:  nowAlertItem.getClockSetting().setSaturday(CLOSE);break;
            case 6:  nowAlertItem.getClockSetting().setSunday(CLOSE);break;
        }
    }

}
