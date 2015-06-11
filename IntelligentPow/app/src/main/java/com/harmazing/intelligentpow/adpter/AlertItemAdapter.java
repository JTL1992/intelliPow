package com.harmazing.intelligentpow.adpter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.UI.AlertAty;
import com.harmazing.intelligentpow.UI.AlertSettingAty2;
import com.harmazing.intelligentpow.UI.SleepSettingAty;
import com.harmazing.intelligentpow.model.AlertItem;
import com.harmazing.intelligentpow.model.AlertItemUpdate;
import com.harmazing.intelligentpow.model.RepeatSetting;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.GsonUtil;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.MyDialog;
import com.harmazing.intelligentpow.view.OnConfirmListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jtl on 2015/3/30.
 * 闹钟adapter
 */
public class AlertItemAdapter extends ArrayAdapter<AlertItem> {

    private int allowHot,maxTemp,minTemp;
    private String deviceId;
    private String url = HttpHead.head + "UpdateShowTimingSet";
    private ProgressDialog progressDialog;
    private RefreshViewListener rfListener;
    public AlertItemAdapter(Context context){
        super(context,0);
    }
    public AlertItemAdapter(Context context,int allowHot, int maxTemp, int minTemp, String deciveId){
        super(context,0);
        this.allowHot = allowHot;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.deviceId = deciveId;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("提交数据中。。。");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adp_alert2, null);
            viewHolder.layoutAlertItem = (LinearLayout) convertView.findViewById(R.id.layout_alert_time);
            viewHolder.layoutAlert = (LinearLayout) convertView.findViewById(R.id.alert_layout);
            viewHolder.txMode = (TextView) convertView.findViewById(R.id.tx_mode);
            viewHolder.txShut = (TextView) convertView.findViewById(R.id.tx_turn_off);
            viewHolder.txTemp = (TextView) convertView.findViewById(R.id.tx_temp);
            viewHolder.txTime = (TextView) convertView.findViewById(R.id.tx_time);
            viewHolder.txWind = (TextView) convertView.findViewById(R.id.tx_wind);
            viewHolder.txWeek = (TextView) convertView.findViewById(R.id.tx_week);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.switch_check);
            convertView.setTag(viewHolder);
        }
        else{
        viewHolder = (ViewHolder)convertView.getTag();
        }
        //设置时间
        viewHolder.txTime.setText(getItem(position).getClocking());
        //设置三种状态
        if (getItem(position).getOn_off() == 0){//关
            viewHolder.txShut.setVisibility(View.VISIBLE);
            viewHolder.txMode.setVisibility(View.GONE);
            viewHolder.txTemp.setVisibility(View.GONE);
            viewHolder.txWind.setVisibility(View.GONE);
        }
        else{
            viewHolder.txMode.setText(getMode(getItem(position).getMode()));
            viewHolder.txTemp.setText(getItem(position).getTemp()+"℃");
            viewHolder.txWind.setText(getWind(getItem(position).getWindspeed()));
        }
        //设置开关
       if (getItem(position).getStartend() == 1){
           viewHolder.checkBox.setChecked(true);
       }
        else {
           viewHolder.checkBox.setChecked(false);
       }
        //设置周
        viewHolder.txWeek.setText(getTimeSetting(getItem(position).getMonday(), getItem(position).getTuesday(),
                getItem(position).getWednesday(), getItem(position).getThursday(), getItem(position).getFriday(),
                getItem(position).getSaturday(), getItem(position).getSunday()));
        //点击一条数据的跳转
        viewHolder.layoutAlertItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AlertSettingAty2.class);
                intent.putExtra("allowHot",allowHot);
                intent.putExtra("maxTemp",maxTemp);
                intent.putExtra("minTemp",minTemp);
                intent.putExtra("deviceId",deviceId);
                intent.putExtra("from",AlertSettingAty2.EDIT);
                intent.putExtra("type",AlertSettingAty2.CLOCK);
                intent.putExtra("alertItem",getItem(position));
                getContext().startActivity(intent);
            }
        });
        viewHolder.layoutAlertItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setMessage("确定要删除此项定时设置么？");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteItem(getItem(position));
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        //状态开关
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    getItem(position).setStartend(1);
                else
                    getItem(position).setStartend(0);
                httpPostSwitch(getItem(position));
                Log.v("httpPostSwitch",b+"");
            }
        });
        return convertView;
    }
    public void setRefreshListener(RefreshViewListener l){rfListener = l;}
    public void refresh(){
        if (rfListener != null){
            rfListener.onRefreshView();
        }
    }
    private void deleteItem(AlertItem item) {
        LogUtil.v("deleturl",HttpHead.head+API.DEL_TIMESETTING+item.getId());
        RequestParams params = new RequestParams();
        params.put("userId", AppConfig.getInstance().getUserId());
        params.put("clocksettingid",item.getId());
        HttpUtil.post(HttpHead.head+API.DEL_TIMESETTING,params,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                if (getContext() instanceof AlertAty){
                    progressDialog.show();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                if (getContext() instanceof AlertAty){
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("deletItem",response.toString());
                Toast.makeText(getContext(),"删除成功！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("deletItem",responseString+statusCode);
                Toast.makeText(getContext(),"删除成功！",Toast.LENGTH_LONG).show();
                refresh();
            }

        });
    }

    private void httpPostSwitch(AlertItem alertItem) {
        AlertItemUpdate alertItemUpdate = new AlertItemUpdate();
        alertItemUpdate.setDeviceId(deviceId);
        alertItemUpdate.setClockSetting(alertItem);
        String urlString = GsonUtil.bean2Json(alertItemUpdate);
        Log.v("httpPosturlString:",urlString);
        HttpUtil.post(getContext(),url,urlString,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                if (getContext() instanceof AlertAty){
                    progressDialog.show();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                if (getContext() instanceof AlertAty){
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v("post json succ",statusCode+response.toString());
                Toast.makeText(getContext(), "设置成功", Toast.LENGTH_LONG).show();
            }
        });
    }



    public class ViewHolder{
      LinearLayout layoutAlert,layoutAlertItem;
      TextView txTime,txWind,txTemp,txMode,txShut,txWeek;
      CheckBox checkBox;
    }
    public static String getMode(Integer hum){
        String humString ="";
        if (hum != null)
        switch (hum){
            case 0 : humString = "自动";break;
            case 1 : humString = "送风";break;
            case 2 : humString = "制冷";break;
            case 3 : humString = "制热";break;
            case 4 : humString = "除湿";break;
            default: humString = "error";
        }
        return  humString;
    }
    public static String getWind(Integer wind){
        String windString;
        switch (wind){
            case 0 : windString = "自动";break;
            case 1 : windString = "风速一";break;
            case 2 : windString = "风速二";break;
            case 3 : windString = "风速三";break;
            case 4 : windString = "风速四";break;
            case 5 : windString = "风速五";break;
            case 6 : windString = "静音";break;
            default: windString = "error";
        }
        return windString;
    }
    public static String getTimeSetting(AlertItem alertItem, boolean[] flags){
          return  getTimeSetting(flags,alertItem.getMonday(),alertItem.getTuesday(),
                  alertItem.getWednesday(),alertItem.getThursday(),alertItem.getFriday(),
                  alertItem.getSaturday(),alertItem.getSunday());
    }
    public static String getTimeSetting(RepeatSetting repeatSetting ,boolean[] flags){
        return  getTimeSetting(flags,repeatSetting.getWeeks().getMonday(),repeatSetting.getWeeks().getTuesday(),
                repeatSetting.getWeeks().getWednesday(),repeatSetting.getWeeks().getThursday(),repeatSetting.getWeeks().getFriday(),
                repeatSetting.getWeeks().getSaturday(),repeatSetting.getWeeks().getSunday());
    }
    public static String getTimeSetting(Integer...args){
        String s = "";
        for (int i = 0; i < 7 ; i++){
            if (args[i] == 1){
                switch (i){
                    case 0 : s += "一"; break;
                    case 1 : s += "二"; break;
                    case 2 : s += "三"; break;
                    case 3 : s += "四"; break;
                    case 4 : s += "五"; break;
                    case 5 : s += "六"; break;
                    case 6 : s += "日"; break;
                }
            }
        }
        return s;
    }
    public static String getTimeSetting(boolean[] flags,Integer...args){
        String s = "";
        for (int i = 0; i < 7 ; i++){
            if (args[i] == 1){
               flags[i] = true;
                switch (i){
                    case 0 : s += "一";
                        break;
                    case 1 : s += "二";
                        break;
                    case 2 : s += "三";
                        break;
                    case 3 : s += "四";
                        break;
                    case 4 : s += "五";
                        break;
                    case 5 : s += "六";
                        break;
                    case 6 : s += "日";
                        break;
                }
            }
        }
        return s;
    }

}
