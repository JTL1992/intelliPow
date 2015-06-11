package com.harmazing.intelligentpow.UI;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.model.AlertItem;
import com.harmazing.intelligentpow.model.AlertItemUpdate;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;
import com.harmazing.intelligentpow.tools.GsonUtil;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.CrossWebView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * A simple {@link android.app.Fragment} subclass.
 *关于我们Fragment
 */
public class AboutUsFragment extends android.support.v4.app.Fragment{
    static int num = 0;
    static CrossWebView webView;
    ImageView btnAddCurve;
    static boolean[] flags = {false,false,false,false,false,false,false};

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null){
       //改变背景和Logo
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
        getActivity().getActionBar().setLogo(getResources().getDrawable(R.drawable.icon_back_left));
//        webView = (CrossWebView) getActivity().findViewById(R.id.web_sleep);


        getActivity().getActionBar().setTitle("舒睡曲线");

//       getActivity().getActionBar().setDisplayShowTitleEnabled(true);

        }

        return inflater.inflate(R.layout.framel_sleeping_line, null);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView = (CrossWebView) getActivity().findViewById(R.id.web_sleep);
        btnAddCurve = (ImageView) getActivity().findViewById(R.id.iv_add);
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
//        webView.loadUrl("file:///android_asset/ydqs.html?deviceId=" +deviceId+"?"+ HttpHead.getHead());
        webView.loadUrl("file:///android_asset/aa/charts.html?userId="+AppConfig.getInstance().getUserId()+"&url="+HttpHead.forhead+"&num="+num + "&isDevice=0");
        LogUtil.v("curve_uerId", "file:///android_asset/aa/charts.html?userId=" + AppConfig.getInstance().getUserId());
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        webView.setWebViewClient(new webViewClient());

        btnAddCurve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SleepSettingAty.class);
                intent.putExtra("from",SleepSettingAty.ADD);
                getActivity().startActivity(intent);
                num = 0;
                Toast.makeText(getActivity(),"添加曲线",Toast.LENGTH_LONG).show();
            }
        });
    }
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String s = URLDecoder.decode(url);
            String json = s.split("=")[1];
            LogUtil.v("url", json);
            final AlertItem alertItem = GsonUtil.json2Bean(json,AlertItem.class);
            parseJson(getActivity(),alertItem);
            return true;
        }
    }

    public static void parseJson(final Activity activity, final AlertItem alertItem) {
        Intent intent = new Intent(activity,SleepSettingAty.class);
        if (alertItem.getType().equals("node")){
            intent.putExtra("from",SleepSettingAty.EDIT);
            intent.putExtra("alertItem",alertItem);
            num = alertItem.getNum();
            LogUtil.v("url alertItem", alertItem.toString());
//            Toast.makeText(activity,alertItem.toString(),Toast.LENGTH_LONG).show();
            activity.startActivity(intent);
        }
        if (alertItem.getType().equals("rpe")){
            Toast.makeText(activity,alertItem.toString(),Toast.LENGTH_LONG).show();

            initRepeat(activity,alertItem);
        }

        if (alertItem.getType().equals("add")){
            intent.putExtra("from",SleepSettingAty.ADD);
            intent.putExtra("curveId",alertItem.getId());
            num = alertItem.getNum();
            activity.startActivity(intent);
            LogUtil.v("url_num_add",num+"");
        }
        if (alertItem.getType().equals("page")){
            num = alertItem.getNum();
            LogUtil.v("url_num",num+"");
        }
        if (alertItem.getType().equals("delete")){
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setMessage("确定要删除该曲线设置么？");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteItem(activity,alertItem);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
    public static void parseJson(final Activity activity, final AlertItem alertItem, Bundle bundle) {
        Intent intent = new Intent(activity,SleepSettingAty.class);
        intent.putExtra("allowHot",bundle.getInt("allowHot"));
        intent.putExtra("maxTemp",bundle.getInt("maxTemp"));
        intent.putExtra("minTemp",bundle.getInt("minTemp"));
        if (alertItem.getType().equals("node")){
            intent.putExtra("from",SleepSettingAty.EDIT);
            intent.putExtra("alertItem",alertItem);
            LogUtil.v("url alertItem", alertItem.toString());
            num = alertItem.getNum();
//            Toast.makeText(activity,alertItem.toString(),Toast.LENGTH_LONG).show();
            activity.startActivity(intent);
        }
        if (alertItem.getType().equals("rpe")){
//            Toast.makeText(activity,alertItem.toString(),Toast.LENGTH_LONG).show();
            initRepeat(activity,alertItem);
        }
        if (alertItem.getType().equals("add")){
            intent.putExtra("from",SleepSettingAty.ADD);
            intent.putExtra("curveId",alertItem.getId());
            num = alertItem.getNum();
            LogUtil.v("url_num_add_device",num+"");
            activity.startActivity(intent);
        }
        if (alertItem.getType().equals("page")){
            num = alertItem.getNum();
            LogUtil.v("url_num",num+"");
        }
        if (alertItem.getType().equals("delete")){
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setMessage("确定要删除该曲线设置么？");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteItem(activity,alertItem);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }

    public static void  deleteItem(final Activity activity, final AlertItem alertItem) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("正在删除。。。");
        RequestParams params = new RequestParams();
        params.put("userId", AppConfig.getInstance().getUserId());
        params.put("clocksettingid",alertItem.getId());
        HttpUtil.post(HttpHead.head + API.DEL_TIMESETTING, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!activity.isFinishing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!activity.isFinishing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("deletItem", response.toString());
                num = 0;
                if (activity instanceof AlertSleepAty)
                    webView.loadUrl(API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+((AlertSleepAty) activity).deviceId+"&url="+HttpHead.forhead + "&isDevice=1"+"&num="+0);
                else
                    webView.loadUrl("file:///android_asset/aa/charts.html?userId="
                            + AppConfig.getInstance().getUserId() + "&url=" + HttpHead.forhead + "&num=" + num + "&isDevice=0");
                Toast.makeText(activity, "删除成功！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("deletItem", responseString + statusCode);
                num = 0;
                if (activity instanceof AlertSleepAty)
                    ((AlertSleepAty) activity).webView.loadUrl(API.LOCAL_CURVES_HTML+ AppConfig.getInstance().getUserId()+"&deviceId="+((AlertSleepAty) activity).deviceId+"&url="+HttpHead.forhead + "&isDevice=1"+"&num="+0);
                else
                    webView.loadUrl("file:///android_asset/aa/charts.html?userId="
                            + AppConfig.getInstance().getUserId() + "&url=" + HttpHead.forhead + "&num=" + num + "&isDevice=0");
                Toast.makeText(activity, "删除成功！", Toast.LENGTH_LONG).show();
//                finish();
            }

        });
    }



    public static void initRepeat(final Activity activity, final AlertItem alertItem) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("请等待。。。");
        RequestParams params = new RequestParams();
        final String curveId = alertItem.getId();
        params.put("curveId", alertItem.getId());
        Bundle bundle = activity.getIntent().getExtras();
        final String deviceId = bundle.getString("deviceId");//临时用传deviceId
        params.put("deviceId",deviceId);
        LogUtil.v("initRepeat", deviceId);
        HttpUtil.post(HttpHead.head + API.INIT_REPEAT, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!activity.isFinishing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!activity.isFinishing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("initRepeat", response.toString() + "########### " + statusCode);
                try {
                    JSONArray arr = (JSONArray)response.get("result");
                    for ( int i = 0 ;i<arr.length() ; i++){
                        flags[i] = (Boolean)arr.get(i);
                    }
                    Dialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("请选择重复日期");
                    builder.setMultiChoiceItems(R.array.day, flags, null);
                    DialogInterface.OnMultiChoiceClickListener clickListener3 = new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            flags[i] = b;
                        }
                    };
                    builder.setMultiChoiceItems(R.array.day, flags, clickListener3);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String day = "";
                            for (int l = 0; l < flags.length; l++) {
                                if (flags[l]) {
                                    day += 1 + ",";
                                } else {
                                    day += 0 + ",";
                                }
                            }
                            if(day.length() > 0){
                                day = day.substring(0,day.length() - 1);

                            }
//                            Toast.makeText(activity, day, Toast.LENGTH_LONG).show();
                            AlertItem alertItems = new AlertItem();
                            alertItems.setType(deviceId);
//                            alertItems.setTemp(day);
                            alertItems.setWeek(day);
                            alertItems.setId(curveId);
//                            if (day.contains("1"))
                                updateRepeat(activity, alertItems);
//                            else
//                                Toast.makeText(activity,"请选择一个重复时间！",Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    dialog = builder.create();
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("deletItem", responseString + statusCode);
                Toast.makeText(activity, "设置失败！", Toast.LENGTH_LONG).show();
//                finish();
            }

        });
    }

    public static void updateRepeat(final Activity activity, AlertItem alertItem) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("请等待。。。");
        RequestParams params = new RequestParams();
        params.put("userId", AppConfig.getInstance().getUserId());
        params.put("curveId", alertItem.getId());
        params.put("deviceId", alertItem.getType());
        params.put("week", alertItem.getWeek());
        HttpUtil.post(HttpHead.head + API.UPDATE_REPEAT, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!activity.isFinishing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!activity.isFinishing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("initRepeat", response.toString() + "########### " + statusCode);
                Toast.makeText(activity, "设置成功！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("deletItem", responseString + statusCode);
                Toast.makeText(activity, "设置失败！", Toast.LENGTH_LONG).show();
//                finish();
            }

        });
    }

    @Override
    public void onResume() {
        LogUtil.v("onResume","");
        webView.loadUrl("file:///android_asset/aa/charts.html?userId="
                +AppConfig.getInstance().getUserId()+"&url="+HttpHead.forhead+"&num="+num+ "&isDevice=0");
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        webView.loadUrl("file:///android_asset/aa/charts.html?userId="
                +AppConfig.getInstance().getUserId()+"&url="+HttpHead.forhead+"&num="+num+ "&isDevice=0");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.v("num..",""+0);
        num = 0;
    }
}
