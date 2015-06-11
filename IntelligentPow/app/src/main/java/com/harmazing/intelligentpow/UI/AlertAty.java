package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.adpter.RefreshViewListener;
import com.harmazing.intelligentpow.model.AlertItem;
import com.harmazing.intelligentpow.adpter.AlertItemAdapter;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.GsonUtil;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlertAty extends Activity {
    ImageView btnBack,btnSetting;
    AlertItemAdapter alertItemAdapter;
    ListView mListView;
    private int allowHot,maxTemp,minTemp;
    private String deviceId;
    private ProgressDialog progressBar;
//    public static boolean[] flags = {false,false,false,false,false,false,false};
//    private String url = HttpHead.head + "QueryTimingSet?deviceId=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_aty);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("加载数据中。。。");
        //初始化view
        findViews();

       //初始化数据
        initAlertData();

        //点击事件
        initClickListener();
//        alertItemAdapter = new AlertItemAdapter(this,allowHot,maxTemp,minTemp);

    }
    //初始化view
    public void findViews(){

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSetting = (ImageView) findViewById(R.id.iv_setting);
        btnSetting = (ImageView) findViewById(R.id.iv_setting);
        mListView  = (ListView) findViewById(R.id.list_alert);
    }
    //添加点击事件
    public void initClickListener() {
        //back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //setting
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlertAty.this, AlertSettingAty2.class);
                intent.putExtra("allowHot", allowHot);
                intent.putExtra("maxTemp", maxTemp);
                intent.putExtra("minTemp", minTemp);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("from", AlertSettingAty2.ADD);
                intent.putExtra("type", AlertSettingAty2.CLOCK);
                startActivity(intent);
            }
        });
    }
    //初始化空调闹钟
    public void initAlertData(){
     Bundle bundle = getIntent().getExtras();
     allowHot = bundle.getInt("allowHot");
     maxTemp = bundle.getInt("maxTemp");
     minTemp = bundle.getInt("minTemp");
     deviceId = bundle.getString("deviceId");
     alertItemAdapter = new AlertItemAdapter(this,allowHot,maxTemp,minTemp,deviceId);
     alertItemAdapter.setRefreshListener(new RefreshViewListener() {
         @Override
         public void onRefreshView() {
             refresh();
         }
     });
     getDataFromHttp();
    }
   public void getDataFromHttp(){
       LogUtil.v("alert info url", HttpHead.head + API.QUERY_TIMESETTING + deviceId);
       HttpUtil.get(HttpHead.head+ API.QUERY_TIMESETTING+deviceId,new JsonHttpResponseHandler(){
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               super.onSuccess(statusCode, headers, response);
               LogUtil.v("alert info array",response.toString());
               try {
                   if (response.length()>0)
                      for (int i = 0; i < response.length(); i++){
                          AlertItem alertItem = GsonUtil.json2Bean(response.getJSONObject(i).toString(),AlertItem.class);
                          LogUtil.v("alertItemToString", alertItem.toString());
//                          AlertItemAdapter.getTimeSetting(alertItem,flags);
                          alertItemAdapter.add(alertItem);
                      }
                   else
                       Toast.makeText(getApplicationContext(),"无定时数据",Toast.LENGTH_LONG).show();
                   mListView.setAdapter(alertItemAdapter);
               }catch (JSONException e){
                   e.printStackTrace();
               }
           }

           @Override
           public void onStart() {
               if (!AlertAty.this.isFinishing())
                  progressBar.show();
               super.onStart();
           }

           @Override
           public void onFinish() {
               if (!AlertAty.this.isFinishing())
                   progressBar.dismiss();
               super.onFinish();
           }
       });
//       AlertItem alertItem = (AlertItem) getIntent().getExtras().getSerializable("alert");
//       alertItemAdapter = new AlertItemAdapter(this,allowHot,maxTemp,minTemp);
//       alertItemAdapter.add();
//       mListView.setAdapter(alertItemAdapter);
   }

    @Override
    protected void onRestart() {
        refresh();
        LogUtil.v("Restart","");
        super.onRestart();
    }
    public void refresh(){
        alertItemAdapter.clear();
        getDataFromHttp();
    }
}
