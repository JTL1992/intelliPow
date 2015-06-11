package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.CrossWebView;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JTL on 2014/9/17.
 * 门窗详细信息控制界面
 */
public class DoorAty extends Activity {
    private ImageButton btnBack;
    private int id = 0;
    private CrossWebView webView;
    ImageView doorWindow, battery;
    TextView stateDevice, title;
    private String deviceId, gwId;
    private boolean timerStart = true;
    private String modifyName = null; //修改名称
    private JsonHttpResponseHandler getDataJsonResponseHandler;
    private final String urlChangeAcName = HttpHead.head+"changeDeviceName?"; //修改设备名称
    private final String urlGetDeviceData = HttpHead.head+"deviceData?"; //请求设备信息的URL
    public DoorAty(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_door_aty);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.actionbar_winanddoor);
        btnBack = (ImageButton) findViewById(R.id.title_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
        doorWindow = (ImageView) findViewById(R.id.icon_door);
        battery = (ImageView) findViewById(R.id.icon_baterry);
        stateDevice = (TextView) findViewById(R.id.state_door);
        webView = (CrossWebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        webView.loadUrl("file:///android_asset/ydqs_door.html?deviceId"+deviceId);

        int batteryId = R.drawable.icon_electricity_2;
        Bundle bundle = getIntent().getExtras();
        int type = bundle.getInt("type");
        id = bundle.getInt("id");
        deviceId = bundle.getString("deviceId");
        gwId = bundle.getString("deviceGwId");
        String name = bundle.getString("name");
        String state = bundle.getString("state");
        int batteryLeft = bundle.getInt("battery");
        title.setText(name);
        webView.enablecrossdomain41();
        webView.loadUrl(API.LOCAL_YDQS_HTML_DR+deviceId+"?"+HttpHead.forhead);
        switch (batteryLeft){
            case 0 : batteryId = R.drawable.icon_electricity_0;break;
            case 1 : batteryId = R.drawable.icon_electricity_1;break;
            case 2 : batteryId = R.drawable.icon_electricity_2;break;
            case 3 : batteryId = R.drawable.icon_electricity_3;break;
            case 4 : batteryId = R.drawable.icon_electricity_4;break;
        }
        battery.setBackgroundDrawable(getResources().getDrawable(batteryId));
        if (type == 1){
            if(state.equals("开"))
            doorWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_door_open_big));
            else {
                stateDevice.setText("关");
                doorWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_door_close_bige));
            }
        }
        else {
            if (state.equals("开"))
                doorWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_win_open_big));
            else {
                stateDevice.setText("关");
                doorWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_win_close_big));
            }
        }

        RelativeLayout modify = (RelativeLayout) findViewById(R.id.modify);
        modify.setOnClickListener(modifyClickListener);
        getDataJsonResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("winAndDoor", response.toString());
                try {
                    int batteryResourseId;
                    String name = response.getString("name");
                    Boolean success = response.getBoolean("success");
                    if (success){
                    int type = response.getInt("type");
                    int state = response.getInt("status");
                    int remain = response.getInt("remain");
                    title.setText(name);
                    switch (remain){
                        case 0 : batteryResourseId = R.drawable.icon_electricity_0;break;
                        case 1 : batteryResourseId = R.drawable.icon_electricity_1;break;
                        case 2 : batteryResourseId = R.drawable.icon_electricity_2;break;
                        case 3 : batteryResourseId = R.drawable.icon_electricity_3;break;
                        case 4 : batteryResourseId = R.drawable.icon_electricity_4;break;
                        default : batteryResourseId = R.drawable.icon_electricity_0;break;
                    }
                    if (type == 3){
                        if (state == 1){
                          doorWindow.setBackgroundResource(R.drawable.icon_door_open_big);
                          stateDevice.setText("开");
                        }
                        if (state == 0){
                          doorWindow.setBackgroundResource(R.drawable.icon_door_close_bige);
                          stateDevice.setText("关");
                        }
                        if (state == 2){
                          doorWindow.setBackgroundResource(R.drawable.icon_door_close_bige);
                          stateDevice.setText("异常");
                        }
                        battery.setBackgroundResource(batteryResourseId);

                    }
                    else {
                        if (state == 1){
                            doorWindow.setBackgroundResource(R.drawable.icon_win_open_big);
                             stateDevice.setText("开");
                        }
                        if (state == 0){
                            doorWindow.setBackgroundResource(R.drawable.icon_win_close_big);
                            stateDevice.setText("关");
                        }
                        if (state ==2){
                            doorWindow.setBackgroundResource(R.drawable.icon_win_close_big);
                            stateDevice.setText("异常");
                        }
                        battery.setBackgroundResource(batteryResourseId);
                     }
                   }
                    else
                        Toast.makeText(getApplicationContext(),"请求失败，请稍后再试！",Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    LogUtil.v("getJson",response.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.v("请求失败",errorResponse.toString());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                LogUtil.v("请求结束","!!!!!!!");
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        LogUtil.v("门窗界面刷新请求数据","开始");
                        RequestParams params = new RequestParams();
                        params.put("deviceId",deviceId);
                        params.put("gwId",gwId);
                        HttpUtil.get(HttpHead.head+ API.GET_DEVICE_DATA, params, getDataJsonResponseHandler);
                    }
                };
               Timer timer = new Timer();
                if (timerStart)
                    timer.schedule(timerTask,5000);
            }
        };
        RequestParams params = new RequestParams();
        params.put("deviceId",deviceId);
        params.put("gwId",gwId);
        LogUtil.v("门窗deviceId和gwId",""+gwId+"!!!!"+deviceId);
        HttpUtil.get(HttpHead.head+ API.GET_DEVICE_DATA, params, getDataJsonResponseHandler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerStart = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerStart = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.door_aty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    View.OnClickListener modifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeTitleAlert();
        }
    };
    private void changeTitleAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        android.view.Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_change_title);
        final  EditText editText = (EditText) window.findViewById(R.id.name_door_and_win);
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
                                    title.setText(editText.getText().toString());
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

}
