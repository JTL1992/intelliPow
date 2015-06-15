package com.harmazing.intelligentpow.UI;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.MyDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link android.app.Fragment} subclass.
 * 设置添加设备，打开网络
 */
public class SettingFragment extends android.support.v4.app.Fragment  {
    RelativeLayout layoutAddDevice,layoutAboutUs,layoutOpenNetwork;
    MyDialog myDialog;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    String url = HttpHead.head + "openGWNetwork?";
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity().getActionBar() != null){
            //改变背景和Logo
            getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
            getActivity().getActionBar().setLogo(getResources().getDrawable(R.drawable.icon_back_left));
            getActivity().getActionBar().setTitle("设置");

//       getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        }
        return inflater.inflate(R.layout.fragment_setting, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
           initView();
        //关于我们
        layoutAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AboutUsAty.class);
                getActivity().startActivity(intent);
            }
        });
        //添加设备
        layoutAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                myDialog.showMyDialog("请输入设备序列号");
                Intent intent = new Intent();
                intent.setClass(getActivity(), MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });
        //打开网络
        layoutOpenNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOpenNet();
            }
        });
    }

    private void sendOpenNet() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在打开网络。。。。");
        HttpUtil.post(HttpHead.head+API.OPEN_NET+AppConfig.getInstance().getUserId(),new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                if (!getActivity().isFinishing()){
                    progressDialog.show();
                }
                super.onStart();
            }

            @Override
            public void onFinish() {
                if (!getActivity().isFinishing()){
                    progressDialog.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("open_net_work",statusCode+responseString);
                if (responseString.equals("true"))
                    Toast.makeText(getActivity(),"网络打开成功！",Toast.LENGTH_LONG).show();
//                else
//                    Toast.makeText(getActivity(),responseString,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initView(){
        layoutAboutUs = (RelativeLayout) getActivity().findViewById(R.id.layout_about_us);
        layoutAddDevice = (RelativeLayout) getActivity().findViewById(R.id.layout_add_device);
        layoutOpenNetwork = (RelativeLayout) getActivity().findViewById(R.id.layout_open_network);
//        myDialog = new MyDialog(getActivity());
//        myDialog.setOnConfirmListener(new OnConfirmListener() {
//            @Override
//            public void onConfirm(MyDialog myDialog, String s) {
//                if (!s.equals("")){
//
//                }
//                else{
//                    Toast.makeText(getActivity(),"您输入的设备号为空，请重新输入。",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }
//    @Override
//    public void onClick(View view) {
//        int id =  view.getId();
//       switch (id){
//           case R.id.layout_add_device :
//                myDialog.showMyDialog("请输入设备序列号");
//               break;
//           case R.id.layout_about_us :
//               Intent intent = new Intent(getActivity(),AboutUsAty.class);
//               getActivity().startActivity(intent);
//               break;
//       }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    sendToHttp(result);
//                    Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
//                    mTextView.setText(bundle.getString("result"));
//                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
            break;
        }
    }

    public void sendToHttp(String result){
        RequestParams params = new RequestParams();
        params.put("usrId", AppConfig.getInstance().getUserId());
        params.put("deviceMac",result);
        LogUtil.v("PARAM",params.toString());
        HttpUtil.post(HttpHead.head+ API.BIND_NET_GATE,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("deviceMacBindsucc",response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                LogUtil.v("deviceMacBindsucc", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("deviceMacBindfail",statusCode+responseString);
                if (responseString.equals("ok"))
                    Toast.makeText(getActivity(),"绑定成功",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(),responseString,Toast.LENGTH_LONG).show();
            }
        });
    }
}



