package com.harmazing.intelligentpow.UI;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * A simple {@link android.app.Fragment} subclass.
 *安全设置Fragment
 */
public class SafeSettingFragment extends android.support.v4.app.Fragment {

    private RelativeLayout btnSecret;
    ImageButton btnCircle;
    Button btnConfirm;
    EditText oldPassword,newPassword;
    private boolean isChecked = false;
    private String urlChangePassword = HttpHead.head + "changeUserPwd?";
    public SafeSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity().getActionBar() != null) {
              getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
              getActivity().getActionBar().setLogo(R.drawable.icon_back_left);
            }
         return inflater.inflate(R.layout.fragment_safe_setting, null);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       btnSecret = (RelativeLayout) getActivity().findViewById(R.id.check);
       btnCircle = (ImageButton) getActivity().findViewById(R.id.btn_check);
       btnConfirm = (Button) getActivity().findViewById(R.id.btn_confirm);
       oldPassword = (EditText) getActivity().findViewById(R.id.oldpass);
       newPassword = (EditText) getActivity().findViewById(R.id.name_door_and_win);
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LogUtil.v("oldPassword",""+oldPassword.getText().toString().equals(""));
//                LogUtil.v("newPassword",newPassword.getText().toString());
                if (oldPassword.getText().toString().equals("") || newPassword.getText().toString().equals(""))
                    Toast.makeText(getActivity(),"密码不能为空",Toast.LENGTH_LONG).show();
                else{
                    if (!oldPassword.getText().toString().equals(AppConfig.getInstance().getPassword()))
                        Toast.makeText(getActivity(),"旧密码输入错误",Toast.LENGTH_LONG).show();
                    else{
                        final RequestParams params = new RequestParams();
                        params.put("username",AppConfig.getInstance().getUsername());
                        params.put("oldpwd", oldPassword.getText().toString());
                        params.put("newpwd", newPassword.getText().toString());
                        HttpUtil.get(HttpHead.head+ API.CHANGE_SECRET, params, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                Boolean success = response.getBoolean("success");
                                String msg = response.getString("msg");
                                if (success){
                                    Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                                    AppConfig.getInstance().setPassword(newPassword.getText().toString());
                                }
                                else
                                    Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                            }catch (Exception e){
                                LogUtil.v("", "");
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            LogUtil.v("更改密码失败",responseString);
                            Toast.makeText(getActivity(),"连接超时，更改密码失败.",Toast.LENGTH_LONG).show();
                        }
                    });
                 }
                }
            }
        };
        btnConfirm.setOnClickListener(confirmListener);
       //密码的显示方式监听，可见不可见状态切换
       btnSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChecked){
                    btnCircle.setBackgroundResource(R.drawable.btn_aqsz_checked);
                    oldPassword.setInputType(0x00000091);
                    newPassword.setInputType(0x00000091);
                    isChecked = true;
                }
                else  {
                    btnCircle.setBackgroundResource(R.drawable.btn_aqsz_check);
                    oldPassword.setInputType(0x00000081);
                    newPassword.setInputType(0x00000081);
                    isChecked =false;
                }
            }
        });
//        View.OnClickListener confirmListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (oldPassword.getText().toString().equals(null) || newPassword.getText().toString().equals(null))
//                    Toast.makeText(getActivity(),"密码不能为空",Toast.LENGTH_LONG).show();
//                else{
//                     final RequestParams params = new RequestParams();
//                     params.put("oldpwd", oldPassword.getText().toString());
//                     params.put("newpwd", newPassword.getText().toString());
//                     HttpUtil.get(urlChangePassword, params, new JsonHttpResponseHandler(){
//                         @Override
//                      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        super.onSuccess(statusCode, headers, response);
//                        try {
//                            Boolean success = response.getBoolean("success");
//                            String msg = response.getString("msg");
//                            if (success)
//                                Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
//                            else
//                                Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
//                        }catch (Exception e){
//                            LogUtil.v("","");
//                        }
//
//                    }
//
//                         @Override
//                       public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        LogUtil.v("更改密码失败",responseString);
//                        Toast.makeText(getActivity(),"连接超时，更改密码失败.",Toast.LENGTH_LONG).show();
//                    }
//                  });
//                }
//            }
//        };

    }


}
