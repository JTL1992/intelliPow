package com.harmazing.intelligentpow.adpter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.model.Device;

import java.util.List;

/**
 * Created by JTL on 2014/9/19.
 * 设备Adapter
 */
public class DeviceAdapter extends ArrayAdapter<Device> {

    public DeviceAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.devicemenu, null);
        }
        TextView text_state = (TextView) convertView.findViewById(R.id.state_siaz);
        //判断开关状态
        if(getItem(position).getState().equals("关")){
            text_state.setText(getItem(position).getState());
            text_state.setTextColor(Color.rgb(99, 99, 99));
            convertView.setBackgroundResource(R.drawable.bg_selecter_main);
        }
        else if (getItem(position).getState().equals("开"))
        {
            text_state.setText(getItem(position).getState());
            text_state.setTextColor(Color.rgb(255,84,0));
            convertView.setBackgroundResource(R.drawable.bg_selecter_main);

        }
        else  if(getItem(position).getState().equals("离线")){
            text_state.setText(getItem(position).getState());
            text_state.setTextColor(Color.rgb(164,41,10));
            convertView.setBackgroundColor(Color.rgb(208,208,208));
        }
        else  if(getItem(position).getState().equals("异常")){
            text_state.setText("异常");
            text_state.setTextColor(Color.rgb(164,41,10));
            convertView.setBackgroundColor(Color.rgb(208,208,208));
        }
        ImageView icon_device = (ImageView) convertView.findViewById(R.id.icon_device_size);
        icon_device.setImageResource(getItem(position).getIcon());
        ImageView icon_hum = (ImageView) convertView.findViewById(R.id.hum_size);
//        if (getItem(position).getHum() != 0)
        icon_hum.setImageResource(getItem(position).getHum());

        ImageView icon_wind = (ImageView) convertView.findViewById(R.id.wind_size);
//        if (getItem(position).getWind() != 0)
        icon_wind.setImageResource(getItem(position).getWind());
        TextView text_temp = (TextView) convertView.findViewById(R.id.temp_size);
        text_temp.setText(getItem(position).getTemp());
        TextView text_name = (TextView) convertView.findViewById(R.id.device_name_size);
//        if (getItem(position).getName().startsWith("AC") || getItem(position).getName().startsWith("DOOR")){
//        text_name.setText(getItem(position).getName());
//        text_name.setTextSize(17);
//        }
//        else{
//         text_name.setText(getItem(position).getName());
        //处理文字长度过长直接省略
        if (getItem(position).getName().length() > 10){
            if (getItem(position).getState().equals("开") ||getItem(position).getState().equals("异常") )
              text_name.setText(getItem(position).getName().substring(0,8)+"...");
            else
              text_name.setText(getItem(position).getName());
        }
        else
            text_name.setText(getItem(position).getName());
         text_name.setTextSize(23);
//        }

        return convertView;
    }

}