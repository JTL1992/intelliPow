package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.view.MyDialog;

import java.util.Calendar;
/**
 * Created by JTL on 2015/4/17.
 * 定时页面（废弃使用了）
 */

public class AlertSettingAty extends Activity {
    RelativeLayout rlTime,rlState,rlTemp,rlWind,rlMode,rlDay;
    TextView txTime,txState,txTemp,txWind,txMode,txDay;
    int mHour,mMin;
    private final int ON_OFF_DIALOG = 1;
    private final int WIND_DIALOG = 2;
    private final int MODE_DIALOG = 3;
    private final int DAY_DIALOG = 4;
    Boolean flags[] = {false,false,false,false,false,false,false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_setting_aty);
        //设置布局
        rlTime = (RelativeLayout) findViewById(R.id.layout_time);
        rlState = (RelativeLayout) findViewById(R.id.layout_turn);
        rlTemp = (RelativeLayout) findViewById(R.id.layout_temp);
        rlWind = (RelativeLayout) findViewById(R.id.layout_wind);
        rlMode = (RelativeLayout) findViewById(R.id.layout_mode);
        rlDay = (RelativeLayout) findViewById(R.id.layout_day);
        //设置数值
        txTime = (TextView) findViewById(R.id.value_time);
        txState = (TextView) findViewById(R.id.value_turn);
        txTemp = (TextView) findViewById(R.id.value_temp);
        txWind = (TextView) findViewById(R.id.value_wind);
        txMode = (TextView) findViewById(R.id.value_mode);
        txDay = (TextView) findViewById(R.id.value_day);
        //初始化
        initView();
        //点击事件
        initClickItem();
    }
    public void initView(){
//        Bundle bundle = getIntent().getExtras();
//        txTime.setText(bundle.getString("time"));
//        txState.setText(bundle.getString("state"));
//        txTemp.setText(bundle.getString("temp"));
//        txWind.setText(bundle.getString("wind"));
//        txMode.setText(bundle.getString("mode"));
//        txDay.setText(bundle.getString("day"));
    }
    public void initClickItem(){
        //时间选择
        rlTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AlertSettingAty.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i2) {
                            txTime.setText(i+":"+i2);
                    }
                },getTime("hour"),getTime("min"),true);
                timePickerDialog.show();
            }
        });
        //开关选择
        rlState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("position",0);
                showDialog(ON_OFF_DIALOG,args);
            }
        });
        //模式选择
        rlMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("position",0);
                showDialog(MODE_DIALOG,args);
            }
        });
        //风速选择
        rlWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("position",0);
                showDialog(WIND_DIALOG,args);
            }
        });
        //重复选择
        rlDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putIntArray("position",new int[]{0,1});
                showDialog(DAY_DIALOG,args);
            }
        });
        //温度选择
        rlTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog myDialog = new MyDialog(AlertSettingAty.this);
                myDialog.setOnPickerListener(new MyDialog.OnPickerListener() {
                    @Override
                    public void onChoose(MyDialog myDialog, int i) {
                        txTemp.setText(i+"℃");
                    }
                });
//                myDialog.showMyDialog("请选择温度",20);
            }
        });

    }
    private String getTime(){
        long time=System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR);
        int min = mCalendar.get(Calendar.MINUTE);
        return hour+":"+min;
    }
    private int getTime(String s){
        long time=System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR);
        int min = mCalendar.get(Calendar.MINUTE);
        if (s.equals("hour"))
            return hour;
        else
            return min;
    }


    @Override
    protected Dialog onCreateDialog(int id,Bundle args) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id){
            case ON_OFF_DIALOG :
                builder.setTitle("请选择空调开关");
                builder.setSingleChoiceItems(R.array.onOff,args.getInt("position"),null);
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txState.setText(getResources().getStringArray(R.array.onOff)[i]);
                    }
                };
                builder.setPositiveButton("确定",clickListener);
                builder.setNegativeButton("取消",null);
                break;
            case MODE_DIALOG :
                builder.setTitle("请选择空调模式");
                builder.setSingleChoiceItems(R.array.mode,args.getInt("position"),null);
                DialogInterface.OnClickListener clickListener1 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txMode.setText(getResources().getStringArray(R.array.mode)[i]);
                    }
                };
                builder.setPositiveButton("确定",clickListener1);
                builder.setNegativeButton("取消",null);
                break;
            case WIND_DIALOG :
                builder.setTitle("请选择空调风速");
                builder.setSingleChoiceItems(R.array.wind,args.getInt("position"),null);
                DialogInterface.OnClickListener clickListener2 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txMode.setText(getResources().getStringArray(R.array.wind)[i]);
                    }
                };
                builder.setPositiveButton("确定",clickListener2);
                builder.setNegativeButton("取消",null);
                break;
            case DAY_DIALOG :
                builder.setTitle("请选择重复日期");
                builder.setMultiChoiceItems(R.array.day, args.getBooleanArray("position"), null);
                DialogInterface.OnMultiChoiceClickListener clickListener3 = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        for (int l = 0; l < flags.length; l++)
                           flags[i] = b;
                    }
                };
                builder.setMultiChoiceItems(R.array.day, args.getBooleanArray("position"), clickListener3);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String day = "";
                        for (int l = 0; l < flags.length; l++)
                            if (flags[l]){
                                day += " "+getResources().getStringArray(R.array.day)[l];
                            }
                        txDay.setText(day);
                    }
                });
                builder.setNegativeButton("取消",null);
                break;
        }
        dialog = builder.create();
        return dialog;
    }
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }
    public Boolean isAcOff(){
             return txState.getText().toString().equals("空调关");
    }
}

