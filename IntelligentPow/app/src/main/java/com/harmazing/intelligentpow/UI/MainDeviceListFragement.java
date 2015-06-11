package com.harmazing.intelligentpow.UI;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.model.Device;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.adpter.DeviceAdapter;

import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link android.app.Fragment} subclass.
 *主界面中的ListFragement
 */
public class MainDeviceListFragement extends ListFragment {

 private static String TAG = "DEVICE_FRAGMENT";
 private String userId;                                     //获取设备列表的参数userId
 private Boolean flag = false;                              //空调关闭状态启动时一次刷新标识
 private  ArrayList<Device> devices = new ArrayList<Device>();  //设备列表
 public  int insideTemp;                                    //室内温
 private Boolean isTimerStart,isfirstLoad;                                //刷新启动标识
 private DeviceAdapter deviceAdapter,deviceAdapterCache;                       //设备适配器
 private ProgressDialog progressDialog;
 private  final String urlString = HttpHead.head+"index?";  //刷新URL
 private final static int FLASH_TIME = 1000;                             //刷新时间
    //定义Type
 private final static int AIRCONDITION = 2;
 private final static int DOOR = 3;
 private final static int WINDOW = 4;
    //定义状态state
 private final static int CLOSE = 0;
 private final static int OPEN = 1;
 private final static int EXCEPTION = 2;
    //定时和舒睡曲线
 private final static int NONE = 0;
 private final static int ALERT = 1;
 private final static int SLEEP = 2;
 private final static int BOTH = 3;
 private JsonHttpResponseHandler jsonHttpResponseHandler;
 private Boolean isDiviceEmpty = false;

    public MainDeviceListFragement() {

    }

    /*生成list*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list, null);

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        isTimerStart = true;
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("加载数据中。。。");
//        progressDialog.show();
//        devices.clear();
//        final RequestParams params = new RequestParams();
//        params.add("userId", userId);
//        LogUtil.v("MainDevice","onStart");
//        HttpUtil.get(urlString, params, jsonHttpResponseHandler);
//
//    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.v("MainDevice", "onActivityCreate");
        isfirstLoad = true;
        isTimerStart = true;
        deviceAdapter = new DeviceAdapter(getActivity());
        //修改list的点击时的背景颜色和分界线
        getListView().setDivider(getResources().getDrawable(R.drawable.bg_mainlist_line));
        getListView().setSelector(getResources().getDrawable(R.drawable.bg_selecter_main));
        //初始刷新等待。。
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("加载数据中。。。");
        progressDialog.show();
        //取userId，写传递参数。
        if (AppConfig.getInstance().getUserId() == null)
        userId = getActivity().getIntent().getStringExtra("userId");
        else
        userId = AppConfig.getInstance().getUserId();
        final RequestParams params = new RequestParams();
        params.add("userId",userId);
        LogUtil.v(TAG+"userId","!!!!!!!!!"+userId);
//        HttpUtil.get(urlString, params, jsonHttpResponseHandler);
        //请求的返回处理
        jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                devices.clear();
                LogUtil.v("MainDeviceListFromHTTP", response.toString());
                if (!isDiviceEmpty)
                  if (response.toString().equals("[]")){
//                      Toast.makeText(getActivity(),"您没有绑定任何设备",Toast.LENGTH_LONG).show();
                      isDiviceEmpty = true;
                  }
                    parseJsonData(response);

            }
             //请求失败
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                if (!flag){
                    if (!getActivity().isFinishing())
                        Toast.makeText(getActivity(),"服务器请求失败，请稍后重试",Toast.LENGTH_SHORT).show();
                flag =true;
                }
            }
            //请求结束，执行循环请求数据
            @Override
            public void onFinish() {
                super.onFinish();

//                for (Device newDevice : devices )
//                    deviceAdapter.add(newDevice);
//                setListAdapter(deviceAdapter);
//                deviceAdapter.notifyDataSetChanged();
//                devices.clear();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        final RequestParams params = new RequestParams();
                        if (AppConfig.getInstance().getUserId() == null)
                             userId = getActivity().getIntent().getStringExtra("userId");
                        else
                             userId = AppConfig.getInstance().getUserId();
                        params.add("userId",userId);
//                        devices.clear();
                        HttpUtil.get(HttpHead.head+ API.GET_USER_DEVICE_LIST, params,jsonHttpResponseHandler);

                 LogUtil.v(TAG+"timerTask","启动！！！！！！！！");
                    }
                };
                //timer定义刷新的频率
                 Timer timer = new Timer(true);
                 if (isTimerStart)
                 timer.schedule(timerTask,FLASH_TIME);
            }
        };
//        final RequestParams params = new RequestParams();
//        params.add("userId", userId);
        HttpUtil.get(HttpHead.head+ API.GET_USER_DEVICE_LIST, params, jsonHttpResponseHandler);

        ListView mAirList = getListView();
        mAirList.setOnItemClickListener(mAirListener);
//        mSensorList.setOnItemClickListener(mSensorListener);

    }

    /*设备列表的点击响应事件*/
    AdapterView.OnItemClickListener mAirListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent in;
          if (devices != null && devices.size() != 0)
            for (int i = 0; i < devices.size(); i++){
                if (position == i){
                    in = new Intent();
                    in.putExtra("name", devices.get(i).getName());
                    in.putExtra("type", devices.get(i).getDeviceType());
                    in.putExtra("state", devices.get(i).getState());
                    in.putExtra("id", i);
                    in.putExtra("deviceId", devices.get(i).getId());
                    in.putExtra("deviceGwId", devices.get(i).getGwId());
                    in.putExtra("minTemp", devices.get(i).getMinTemp());
                    in.putExtra("maxTemp", devices.get(i).getMaxTemp());
                    in.putExtra("allowHeat", devices.get(i).getHotCold());
                    LogUtil.v("传递设备号",""+devices.get(i).getId()+"+++"+devices.get(i).getGwId());
                    if (devices.get(i).getDeviceType() == 0){
                        in.setClass(getActivity(),AirConditionAty.class);
                        in.putExtra("insideTemp",insideTemp);
                        if (devices.get(i).getState().equals("关")){
                           startActivityForResult(in,101);
                    }
                        if (devices.get(i).getState().equals("开")){
                        in.putExtra("wind",devices.get(i).getWindOrder());
                        in.putExtra("hum",devices.get(i).getHumOrder());
                        in.putExtra("temp",devices.get(i).getTempOrder());
                        startActivityForResult(in,101);
                       }
                        if (devices.get(i).getState().equals("离线")){
                            Toast.makeText(getActivity(),"您点击的设备离线",Toast.LENGTH_LONG).show();
                        }
                        if (devices.get(i).getState().equals("异常")){
                            in.putExtra("error",devices.get(i).getErrorDetail());
                            startActivityForResult(in,101);
                        }
                   }
                   else{
                        if (devices.get(i).getState().equals("离线")){
                            Toast.makeText(getActivity(),"您点击的设备离线",Toast.LENGTH_LONG).show();
                        }
                        else{
                        in.putExtra("battery",devices.get(i).getBattery());
                        in.setClass(getActivity(),DoorAty.class);
                        startActivityForResult(in, 101);
                        }
                    }

                }
            }
           else
              Toast.makeText(getActivity(),"网络异常，请稍后重试",Toast.LENGTH_LONG).show();


//    switch (position) {
//        case 0:
//            in = new Intent(getActivity(), AirConditionAty.class);
//            startActivity(in);
//            break;
//        case 1:
//            in = new Intent(getActivity(), AirConditionAty.class);
//            startActivity(in);
//            break;
//        case 2:
//            in = new Intent(getActivity(),AirConditionAty.class);
//            startActivity(in);
//            break;
//    }
}
};
   //Activity销毁时，刷新停止
    @Override
    public void onDestroy() {
        super.onDestroy();
        isTimerStart = false;
    }
    //响应返回的设备名更改信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 101){
            DeviceAdapter deviceAdapter = new DeviceAdapter(getActivity());
            String name = data.getStringExtra("name");
            int id = data.getExtras().getInt("id");
            LogUtil.v("id",""+id);
            Toast.makeText(getActivity(),"name"+name+"id"+id,Toast.LENGTH_LONG).show();
            devices.get(id).setName(name);
            for (Device device : devices)
                deviceAdapter.add(device);
            setListAdapter(deviceAdapter);

        }
//        if (requestCode==101 && resultCode == 100 ){
//            int humId,windId;
//            DeviceAdapter deviceAdapter = new DeviceAdapter(getActivity());
//            int temp = data.getExtras().getInt("temp");
//            int hum = data.getExtras().getInt("hum")+1;
//            int wind = data.getExtras().getInt("wind")+1;
//            int id = data.getExtras().getInt("id");
//            LogUtil.v("id","!!!!!!!!!!!!!!"+id);
//            String name = data.getExtras().getString("name");
//
////            Toast.makeText(getActivity(),"temp"+temp+"hum"+hum+"wind"+wind,Toast.LENGTH_LONG).show();
//            switch (hum){
//                case 1 : humId = R.drawable.icon_mode_auto;break;
//                case 2 : humId = R.drawable.icon_mode_sendwind;break;
//                case 3 : humId = R.drawable.icon_mode_hot;break;
//                case 4 : humId = R.drawable.icon_mode_cold;break;
//                case 5 : humId = R.drawable.icon_mode_dehumidification;break;
//                default: humId = R.drawable.icon_snow;
//            }
//            switch (wind){
//                case 1 : windId = R.drawable.icon_wind_auto;break;
//                case 2 : windId = R.drawable.icon_wind1;break;
//                case 3 : windId = R.drawable.icon_wind2;break;
//                case 4 : windId = R.drawable.icon_wind3;break;
//                case 5 : windId = R.drawable.icon_wind4;break;
//                case 6 : windId = R.drawable.icon_wind5;break;
//                case 7 : windId = R.drawable.icon_wind_silent;break;
//                default: windId = R.drawable.icon_wind;
//
//            }
//            if (name != null)
//            devices.get(id).setName(name);
//            if (temp != -1) {
//                Toast.makeText(getActivity(),"空调开",Toast.LENGTH_LONG).show();
//                devices.get(id).setTemp("" + (temp + 16) + "°");
//                devices.get(id).setTempOrder(temp + 16);
//                devices.get(id).setHum(humId);
//                devices.get(id).setHumOrder(hum);
//                devices.get(id).setWind(windId);
//                devices.get(id).setWindOrder(wind);
//                devices.get(id).setState("开");
//                devices.get(id).setIcon(R.drawable.icon_air_condition);
//            }
//            else{
//                Toast.makeText(getActivity(),"空调关",Toast.LENGTH_LONG).show();
//                devices.get(id).setTemp(null);
//                devices.get(id).setHum(0);
//                devices.get(id).setWind(0);
//                devices.get(id).setIcon(R.drawable.icon_air_conditioning_close);
//                devices.get(id).setState("关");
//            }
//            for (Device device : devices)
//                deviceAdapter.add(device);
//            setListAdapter(deviceAdapter);
//        }
    }
    /**
     * 解析JSONArray数据
     * @param response 请求到的JSON数据
     */
    private void parseJsonData(JSONArray response){
        progressDialog.dismiss();
        for (int i = 0; i < response.length(); i++) {
//            if (response.length() == 0)
//                Toast.makeText(getActivity(),"您没有绑定任何设备",Toast.LENGTH_LONG).show();
//            LogUtil.v("MainDeviceListFromHTTP",response.toString());
            int iconId,windId,humId;
            Device device;
            try {
                String id = response.getJSONObject(i).getString("id");
                String gwId = response.getJSONObject(i).getString("gwId");
                int type = response.getJSONObject(i).getInt("type");
                LogUtil.v("Id&gwId",""+id+"!!!!!!"+gwId);
                String name = response.getJSONObject(i).getString("name");
                if (type == AIRCONDITION){   //空调
                    int state = response.getJSONObject(i).getInt("state");
                    int maxTemp = response.getJSONObject(i).getInt("maxTemp");
                    int minTemp = response.getJSONObject(i).getInt("minTemp");
                    int coldHot = response.getJSONObject(i).getInt("allowHeat");
                    iconId = getIconId(response,i);
//                    if(!response.getJSONObject(i).isNull("clock") && !response.getJSONObject(i).isNull("alert")){
//                        int clock = response.getJSONObject(i).getInt("clock");
//                        int alert = response.getJSONObject(i).getInt("alert");
//                       iconId = setDeviceIcon(clock,state,alert);
//                    }else {
//                        iconId = setDeviceIcon(NONE,state);
//                    }
                    if (state == OPEN) {//开
//                        int maxTemp = response.getJSONObject(i).getInt("maxTemp");
//                        int minTemp = response.getJSONObject(i).getInt("minTemp");
//                        int coldHot = response.getJSONObject(i).getInt("allowHeat");
                        int speed = response.getJSONObject(i).getInt("speed");
                        int temp = response.getJSONObject(i).getInt("acTemp");
                        int mode = response.getJSONObject(i).getInt("mode");
                        LogUtil.v("服务器传递的空调参数","speed:"+speed+"temp:"+temp+"mode:"+mode);
                        insideTemp = response.getJSONObject(i).getInt("temp");

//                        iconId = R.drawable.icon_air_condition;
                        switch (mode){
                            case 0 : humId = R.drawable.icon_mode_auto;break;
                            case 1 : humId = R.drawable.icon_mode_sendwind;break;
                            case 2 : humId = R.drawable.icon_mode_cold;break;
                            case 3 : humId = R.drawable.icon_mode_hot;break;
                            case 4 : humId = R.drawable.icon_mode_dehumidification;break;
                            default: humId = R.drawable.icon_snow;
                        }
                        switch (speed){
                            case 0 : windId = R.drawable.icon_wind_auto;break;
                            case 1 : windId = R.drawable.icon_wind1;break;
                            case 2 : windId = R.drawable.icon_wind2;break;
                            case 3 : windId = R.drawable.icon_wind3;break;
                            case 4 : windId = R.drawable.icon_wind4;break;
                            case 5 : windId = R.drawable.icon_wind5;break;
                            case 6 : windId = R.drawable.icon_wind_silent;break;
                            default: windId = R.drawable.icon_wind;
                        }
                        if (response.getJSONObject(i).isNull("errorDetail") && response.getJSONObject(i).isNull("errorCode"))
                            device = new Device(""+temp+"°","开", name, humId, iconId, windId, 0, minTemp, maxTemp, coldHot);
                        else{
                            device = new Device(""+temp+"°","异常", name, humId, iconId, windId, 0, minTemp, maxTemp, coldHot);
                            device.setErrorDetail(response.getJSONObject(i).getString("errorDetail")+response.getJSONObject(i).
                            getString("errorCode"));
                        }
                        device.setTempOrder(temp);
                        device.setWindOrder(speed);
                        device.setHumOrder(mode);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if(state == CLOSE){//关
//                        iconId = R.drawable.icon_air_conditioning_close;
                        if (response.getJSONObject(i).isNull("errorDetail") && response.getJSONObject(i).isNull("errorCode"))
                            device = new Device(null, "关" , name , 0, iconId, 0, 0, minTemp, maxTemp, coldHot);
                        else{
                            device = new Device(null, "异常" , name , 0, iconId, 0, 0, minTemp, maxTemp, coldHot);
                            device.setErrorDetail(response.getJSONObject(i).getString("errorDetail")+response.getJSONObject(i).
                                    getString("errorCode"));
                        }
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if (state == EXCEPTION){//异常
//                        iconId = R.drawable.icon_air_conditioning_close;
                        device = new Device(null, "离线", name, 0, iconId, 0, 0, minTemp, maxTemp, coldHot);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }

                }
                else if (type == DOOR){//门
                    int state = response.getJSONObject(i).getInt("state");
                    int battery;
                    if((response.getJSONObject(i).get("remain").equals(null)))
                         battery = 0;
                    else
                         battery = response.getJSONObject(i).getInt("remain");

                    if (state == OPEN){
                        iconId = R.drawable.icon_door_open;
                        device = new Device(null,"开",name,0,iconId,0,1);
//                        int battery = response.getJSONObject(i).getInt("remain");
                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if (state == CLOSE){
                        iconId = R.drawable.icon_door_close;
                        device = new Device(null,"关",name,0,iconId,0,1);
//                        int battery = response.getJSONObject(i).getInt("remain");
                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if (state == EXCEPTION){
                        iconId = R.drawable.icon_door_close;
                        device = new Device(null,"离线",name,0,iconId,0,1);
//                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                }
                else if (type == WINDOW){//窗
                    int battery;
                    int state = response.getJSONObject(i).getInt("state");
                    if((response.getJSONObject(i).get("remain").equals(null)))
                       battery = 0;
                    else
                       battery = response.getJSONObject(i).getInt("remain");

                    if (state == OPEN){
                        iconId = R.drawable.icon_window_open;
                        device = new Device(null,"开",name,0,iconId,0,2);
                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if (state == CLOSE){
                        iconId = R.drawable.icon_window_close;
                        device = new Device(null,"关",name,0,iconId,0,2);
                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                    else if (state == EXCEPTION){
                        iconId = R.drawable.icon_window_close;
                        device = new Device(null,"离线",name,0,iconId,0,2);
                        device.setBattery(battery);
                        device.setId(id);
                        device.setGwId(gwId);
                        devices.add(device);
                    }
                }

//                deviceAdapter = new DeviceAdapter(getActivity());
//              deviceCash = (ArrayList<Device>) devices.clone();
                deviceAdapter.clear();
                for (Device newDevice : devices )
                    deviceAdapter.add(newDevice);
                if (isfirstLoad){
                setListAdapter(deviceAdapter);
                isfirstLoad = false;
                }
                deviceAdapter.notifyDataSetChanged();
//                deviceAdapter.clear();
            }catch (Exception e){
                LogUtil.v("MainActivity","解析数据失败!!!!!!!!!");
            }

        }
    }

    private int setDeviceIcon(int clock,int i) {
        int icon = 0;
        switch (i){
            case OPEN :
                switch (clock){
                    case BOTH : icon = R.drawable.icon_air_condition_open_alertandsleep; break;
                    case ALERT : icon = R.drawable.icon_air_condition_open_alert; break;
                    case SLEEP : icon = R.drawable.icon_air_condition_open_sleep;break;
                    case NONE :  icon = R.drawable.icon_air_condition; break;
                    }
                break;
            case CLOSE :
                switch (clock){
                    case BOTH : icon = R.drawable.icon_air_conditioning_close_alertandsleep; break;
                    case ALERT : icon = R.drawable.icon_air_conditioning_close_alert; break;
                    case SLEEP : icon = R.drawable.icon_air_conditioning_close_sleep;break;
                    case NONE :  icon = R.drawable.icon_air_conditioning_close; break;
                }break;
            case EXCEPTION :
                switch (clock){
                    case BOTH : icon = R.drawable.icon_air_conditioning_close_alertandsleep; break;
                    case ALERT : icon = R.drawable.icon_air_conditioning_close_alert; break;
                    case SLEEP : icon = R.drawable.icon_air_conditioning_close_sleep;break;
                    case NONE :  icon = R.drawable.icon_air_conditioning_close; break;
                }break;
        }
        return  icon;
    }
    private int setDeviceIcon(int clock,int i,int alert) {
        int icon = 0;
        switch (i){
            case OPEN :
                if (clock != 0 && alert != 0){
                    icon = R.drawable.icon_air_condition_open_alertandsleep;
                }
                if (clock == 0 && alert == 0){
                    icon = R.drawable.icon_air_condition;
                }
                if (clock == 0 && alert != 0){
                    icon = R.drawable.icon_air_condition_open_alert;
                }
                if (clock != 0 && alert == 0){
                    icon = R.drawable.icon_air_condition_open_sleep;
                }
               break;
            case CLOSE :
                if (clock != 0 && alert != 0){
                    icon = R.drawable.icon_air_conditioning_close_alertandsleep;
                }
                if (clock == 0 && alert == 0){
                    icon = R.drawable.icon_air_conditioning_close;
                }
                if (clock == 0 && alert != 0){
                    icon = R.drawable.icon_air_conditioning_close_alert;
                }
                if (clock != 0 && alert == 0){
                    icon = R.drawable.icon_air_conditioning_close_sleep;
                }
                break;
            case EXCEPTION :
                if (clock != 0 && alert != 0){
                    icon = R.drawable.icon_air_conditioning_close_alertandsleep;
                }
                if (clock == 0 && alert == 0){
                    icon = R.drawable.icon_air_conditioning_close;
                }
                if (clock == 0 && alert != 0){
                    icon = R.drawable.icon_air_conditioning_close_alert;
                }
                if (clock != 0 && alert == 0){
                    icon = R.drawable.icon_air_conditioning_close_sleep;
                }
                break;
        }
        return  icon;
    }
   private int getIconId(JSONArray response,int i){
       int iconId = 0;
       try {
           int state = response.getJSONObject(i).getInt("state");
           if(!response.getJSONObject(i).isNull("clock") && !response.getJSONObject(i).isNull("alert")){
               int clock = response.getJSONObject(i).getInt("clock");
               int alert = response.getJSONObject(i).getInt("alert");
               iconId = setDeviceIcon(clock,state,alert);
           }else if (!response.getJSONObject(i).isNull("clock") && response.getJSONObject(i).isNull("alert")){
               int clock = response.getJSONObject(i).getInt("clock");
               iconId = setDeviceIcon(clock,state,NONE);
           }
           else if(response.getJSONObject(i).isNull("clock") && !response.getJSONObject(i).isNull("alert")){
               int alert = response.getJSONObject(i).getInt("alert");
               iconId = setDeviceIcon(NONE,state,alert);
           }
           else{
               iconId = setDeviceIcon(NONE,state,NONE);
           }
       } catch (JSONException e) {
           e.printStackTrace();
       }
       return  iconId;
   }
}
