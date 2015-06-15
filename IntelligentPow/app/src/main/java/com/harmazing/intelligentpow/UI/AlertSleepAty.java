package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.adpter.AlertItemAdapter;
import com.harmazing.intelligentpow.model.AlertItem;
import com.harmazing.intelligentpow.model.RepeatSetting;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.GsonUtil;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.CrossWebView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.net.URLDecoder;

import static com.harmazing.intelligentpow.UI.AboutUsFragment.num;
import static com.harmazing.intelligentpow.UI.AboutUsFragment.parseJson;
/**
 * Created by JTL on 2015/4/17.
 * 从空调页面进入的舒睡曲线界面
 */

public class AlertSleepAty extends Activity {
    CrossWebView webView;
    String deviceId;
    ImageView btnBack,btnAdd;
    int maxTemp,minTemp,allowHot;
    private final int DAY_DIALOG = 4;
    RepeatSetting repeatSetting;
    Bundle bundleDeviceLimit = null;
    boolean flags[] = {false,false,false,false,false,false,false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_sleep_aty);
        getDataFromBundle();
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnAdd = (ImageView) findViewById(R.id.iv_setting);
        deviceId = getIntent().getExtras().getString("deviceId");
        webView = (CrossWebView) findViewById(R.id.web_sleep);
        //设置WebView的参数
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);//与API level相关，不影响运行
        webView.enablecrossdomain41();
//      webView.loadUrl("file:///android_asset/ydqs.html?deviceId=" +deviceId+"?"+ HttpHead.getHead());
        webView.loadUrl(API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+deviceId+"&url="+HttpHead.forhead + "&isDevice=1"+"&num="+0);
        LogUtil.v("loadUrl",API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+deviceId+"&url="+HttpHead.forhead);
        webView.setWebViewClient(new webViewClient());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertSleepAty.this.finish();
                num = 0;
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = AlertSleepAty.this.getIntent().getExtras();
                String deviceId = bundle.getString("deviceId");
                Intent intent = new Intent(AlertSleepAty.this, SleepSettingAty.class);
                intent.putExtra("allowHot",allowHot);
                intent.putExtra("maxTemp",maxTemp);
                intent.putExtra("minTemp",minTemp);
                intent.putExtra("from",AlertSettingAty2.ADD);
                intent.putExtra("deviceId",deviceId);
//                intent.putExtra("type",AlertSettingAty2.SLEEP);
                startActivity(intent);
                num = 0;
            }
        });
    }

    private void getDataFromBundle() {
        bundleDeviceLimit = getIntent().getExtras();
        maxTemp = bundleDeviceLimit.getInt("maxTemp");
        minTemp = bundleDeviceLimit.getInt("minTemp");
        allowHot = bundleDeviceLimit.getInt("allowHot");
        deviceId = bundleDeviceLimit.getString("deviceId");
    }

    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String s = URLDecoder.decode(url);
            String json = s.split("=")[1];
            LogUtil.v("web url",s+"@"+json);
            if (json.contains("repeat")){
                repeatSetting = GsonUtil.json2Bean(json,RepeatSetting.class);
                LogUtil.v("repeatSetting",repeatSetting.toString());
                AlertItemAdapter.getTimeSetting(repeatSetting,flags);
                initAlertItem(repeatSetting);
                showDialog(DAY_DIALOG);
            }
            else{
               LogUtil.v("url", json);
               final AlertItem alertItem = GsonUtil.json2Bean(json, AlertItem.class);
               parseJson(AlertSleepAty.this, alertItem,bundleDeviceLimit);
            }
            return true;
        }
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
                                setWeekChecked(repeatSetting,l);
                            }
                            else{
                                setWeekUnchecked(repeatSetting,l);
                            }
                        repeatSetting.setType(null);
                        postRepeat(GsonUtil.bean2Json(repeatSetting));
                        LogUtil.v("repeatSetting jsonString",GsonUtil.bean2Json(repeatSetting));
//                        Toast.makeText(getApplicationContext(),day,Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                break;
        }
        dialog = builder.create();
        return dialog;
    }

    private void postRepeat(String jsonString) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在为您的空调应用该曲线。。。");
        HttpUtil.post(this,HttpHead.head+API.REPEAT_SETTING,jsonString,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                if (!AlertSleepAty.this.isFinishing())
                    progressDialog.show();
                super.onStart();
            }

            @Override
            public void onFinish() {
                if (!AlertSleepAty.this.isFinishing())
                    progressDialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("repeat_response succ",response.toString()+statusCode);
                Toast.makeText(getApplicationContext(),"设置成功！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("repeat_response fail",responseString+statusCode);
                Toast.makeText(getApplicationContext(),"设置失败！",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initAlertItem(RepeatSetting repeatSetting) {
        repeatSetting.setUserId(AppConfig.getInstance().getUserId());
        repeatSetting.setDeviceId(deviceId);
        for (int l = 0; l < flags.length; l++)
            if (flags[l]){
                setWeekChecked(repeatSetting,l);
            }
            else{
                setWeekUnchecked(repeatSetting,l);
            }
    }
    public void setWeekChecked(RepeatSetting repeatSetting,int i){
        switch (i){
            case 0:  repeatSetting.getWeeks().setMonday(1);break;
            case 1:  repeatSetting.getWeeks().setTuesday(1);break;
            case 2:  repeatSetting.getWeeks().setWednesday(1);break;
            case 3:  repeatSetting.getWeeks().setThursday(1);break;
            case 4:  repeatSetting.getWeeks().setFriday(1);break;
            case 5:  repeatSetting.getWeeks().setSaturday(1);break;
            case 6:  repeatSetting.getWeeks().setSunday(1);break;
        }

    }
    public void setWeekUnchecked(RepeatSetting repeatSetting,int i){
        switch (i){
            case 0:  repeatSetting.getWeeks().setMonday(0);break;
            case 1:  repeatSetting.getWeeks().setTuesday(0);break;
            case 2:  repeatSetting.getWeeks().setWednesday(0);break;
            case 3:  repeatSetting.getWeeks().setThursday(0);break;
            case 4:  repeatSetting.getWeeks().setFriday(0);break;
            case 5:  repeatSetting.getWeeks().setSaturday(0);break;
            case 6:  repeatSetting.getWeeks().setSunday(0);break;
        }
    }

    @Override
    public void onResume() {
        webView.loadUrl(API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+deviceId
                +"&url="+HttpHead.forhead + "&isDevice=1"+"&num="+num);
        LogUtil.v("onResumDevice",API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+deviceId
                +"&url="+HttpHead.forhead + "&isDevice=1"+"&num="+num);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v("AlertSleepAty","num = 0");
//        num = 0;
    }
}
