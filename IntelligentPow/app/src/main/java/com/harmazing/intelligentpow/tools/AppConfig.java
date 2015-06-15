package com.harmazing.intelligentpow.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.harmazing.intelligentpow.UI.LoginAty;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JTL on 2014/10/3.
 * 存储app记录
 */
public class AppConfig {
    public static final String	 KEY_PASSWORD = "password"; //密码
    public static final String	 KEY_USER = "user"; // 用户账号
    private static final String TAG = "AppConfig"; //
    private static final String USER_ID = "userId"; //用户ID
    private static final String USER_NAME = "userName"; //用户名
    private static final String MOBIL = "mobile";//手机
    private static final String ADDRESS = "address";//地址
    private static final String EMAIL = "email";//邮箱
    private static final String ELEAREA = "eleArea";//用电区域
    private static final String BIZAREA = "bizArea";//业务区域
    private static final String TYPE = "type";//用户类型
    private static final String AMMETER ="ammeter";//电表号
    public static final String PREFERENCES_FILE_NAME = "Preferences";//
    //账户状态
    private final int NORMAL = 0;//
    private final int COLD_ACOUNT = 2;//
    private final int DEL_ACOUNT = 1;//
    private static final boolean D = false;
    private static AppConfig mySelf = null;
    SharedPreferences preferences = null;
    public static AppConfig getInstance(){
        if (mySelf == null) {
            mySelf = new AppConfig();
        }
        return mySelf;
    }
    private AppConfig(){
        try {
            preferences = LoginAty.mainContext.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        } catch (Exception ex) {
            if (D) {
                Log.e(TAG, ex.getMessage());
            }

        }
    }

    public void setUserId(String userId){preferencesSetString(USER_ID, userId);}
    public void setUsername(String username) {
        preferencesSetString(KEY_USER, username);
    }
    public void setPassword(String password) {
        preferencesSetString(KEY_PASSWORD, password);
    }
    public String getUsername() {
        return preferencesGetString(KEY_USER, "");
    }

    public  String getUserId() {
        return preferencesGetString(USER_ID, "");
    }

    public void setAddress(String address) {
       preferencesSetString(ADDRESS, address);
    }
    public void setName(String name){
        preferencesSetString(USER_NAME, name);
    }
    public  void setAmmeter(String ammeter) {
        preferencesSetString(AMMETER, ammeter);
    }

    public  void setBizarea(String bizarea) {
        preferencesSetString(BIZAREA, bizarea);
    }

    public void setElearea(String elearea) {
        preferencesSetString(ELEAREA,elearea);
    }

    public  void setEmail(String email) {
        preferencesSetString(EMAIL, email);
    }

    public void setMobil(String mobil) {
        preferencesSetString(MOBIL, mobil);
    }

    public void setType(String type) {
        preferencesSetString(TYPE, type);
    }

    public void setUserName(String userName) {
        preferencesSetString(USER_NAME, userName);
    }

    public String getPassword() {
        return preferencesGetString(KEY_PASSWORD, "");

    }

    public String getAddress() {
        return preferencesGetString(ADDRESS, "");
    }

    public String getAmmeter() {
        return preferencesGetString(AMMETER, "");
    }

    public String getBizarea() {
        return preferencesGetString(BIZAREA, "");
    }

    public  String getElearea() {
        return preferencesGetString(ELEAREA, "");
    }

    public  String getEmail() {
        return preferencesGetString(EMAIL, "");
    }

    public String getMobil() {
        return preferencesGetString(MOBIL, "");
    }

    public String getType() {
        return preferencesGetString(TYPE, "");
    }

    public String getName() {
        return preferencesGetString(USER_NAME, "");
    }

    public boolean isSettingsOK(Context ct) {
        String username = getUsername();
        String password = getPassword();

        if (username == null || password == null ||
                username.equals("") || password.equals(""))
            return false;
        else{
            return checkAcountState(username,password,ct);
        }
    }

    private boolean checkAcountState(String username, String password,final Context ct) {
        final boolean[] flag = {false};
        String urlString = HttpHead.head+ API.LOGIN;
        final RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        HttpUtil.get(urlString, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("status");
                    switch (status){
                        case NORMAL: flag[0] = true;
                            break;
                        case COLD_ACOUNT: flag[0] = false;
                            Toast.makeText(ct,"您的账户已经被冻结！",Toast.LENGTH_LONG).show();
                            break;
                        case DEL_ACOUNT: flag[0] = false;
                            Toast.makeText(ct,"您的账户已经被删除！",Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(ct, "请求服务器失败，请您稍后再试", Toast.LENGTH_LONG).show();
                LogUtil.v("Failure", "!!!!!!!!!!");
//                           Log.e("Json",errorResponse.toString());
            }

        });
        return flag[0];
    }

    public void clear(){
        setUserId(null);
        setUsername(null);
        setPassword(null);
        setAddress(null);
        setAmmeter(null);
        setBizarea(null);
        setElearea(null);
        setEmail(null);
        setType(null);
        setName(null);
        setMobil(null);
    }
    private void preferencesSetString (String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private String preferencesGetString (String key, String defValue) {
        return preferences.getString(key, defValue);
    }
}
