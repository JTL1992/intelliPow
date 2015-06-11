package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.db.DBManageImpl;
import com.harmazing.intelligentpow.db.DBManager;
import com.harmazing.intelligentpow.db.MyDBHelper;
import com.harmazing.intelligentpow.tools.API;
import com.harmazing.intelligentpow.tools.AppConfig;

import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.harmazing.intelligentpow.view.MyDialog;
import com.harmazing.intelligentpow.tools.NetStatus;
import com.harmazing.intelligentpow.view.OnConfirmListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
/**
 * Created by JTL on 2014/9/17.
 * 登陆界面
 */
public class LoginAty extends Activity {
    Button btnLogin;                           //登录按钮
    public static Context mainContext = null;  //用于声明对话窗口，和sharedpreference的上下文。
    EditText userName,password;                //用户名密码
    String preLogingName = null;               //上一次登陆的用户名
    String preLoginPassword = null;            // 上一次登录的密码
    String loginName,loginPassword;            //用户名密码
    ImageView logo,logoBottom;                  //logo
    RelativeLayout userNameLayout;  //  用户名整体布局
    Boolean netState = false;  //  网络标识旗
    private int rootBottom = Integer.MIN_VALUE; //底层位置
    private View root; // 最外层布局
    private  ProgressDialog mProgress;
    Button btnChangeHttpHead,btnForgetSecret;
    MyDialog myDialog;
    MyDBHelper myDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_aty);
       try {
           createDB();
           getHttpHead();
       }catch (Exception e){
           e.printStackTrace();
       }
        mainContext = this;
      if (NetStatus.getNetStatus(this) == 1)
               netState = true;
        else
          Toast.makeText(getApplicationContext(),"连接超时，请检查网络"+NetStatus.getNetStatus(this),Toast.LENGTH_LONG).show();
        userName = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        logo = (ImageView) findViewById(R.id.logo);
        btnChangeHttpHead = (Button) findViewById(R.id.btn_change);
        userNameLayout = (RelativeLayout) findViewById(R.id.user_name_login);
        root = findViewById(R.id.root);
        myDialog = new MyDialog(this);
        logoBottom = (ImageView) findViewById(R.id.zhinengyongdian_logo);
        btnForgetSecret = (Button) findViewById(R.id.btn_forget_secret);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
          preLogingName = bundle.getString("user");
          preLoginPassword = bundle.getString("password");
          userName.setText(preLogingName);
          password.setText(preLoginPassword);
        }
        myDialog.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm(MyDialog myDialog, String s) {
                LogUtil.v("form", s.split("\\.").length + "@" + s.contains(":"));
                if (!s.equals("") && s.split("\\.").length == 4 && s.contains(":")){
                    DBManager dbManager = DBManageImpl.getDBInstance(getApplicationContext());
//                    dbManager.addItem("head",s);
                    HttpHead.setHead(s);
                    dbManager.updateItem(HttpHead.forhead);
                    LogUtil.v("dialog forhead",HttpHead.forhead);
                    Toast.makeText(getApplicationContext(),"域名更改为："+HttpHead.head,Toast.LENGTH_LONG).show();
                }
                else if (s.split("\\.").length != 4 || !s.contains(":")){
                    Toast.makeText(getApplicationContext(),"请输入正确的域名格式例如：123.56.88.237:8080",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"您输入的为空，更改无效",Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                }
            }
        });
        //修改域名
        btnChangeHttpHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               myDialog.showMyDialog("重新设置域名");
            }
        });
        //忘记密码
        btnForgetSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginAty.this, ForgetSecretAty.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(loginListener);
        //判断是否有登录记录，有则直接进入。
       if (AppConfig.getInstance().isSettingsOK(this)){
           Intent intent = new Intent(LoginAty.this,MainAty.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
           finish();
       }
        //布局改变的响应
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                root.getGlobalVisibleRect(r);
                // 进入Activity时会布局，第一次调用onGlobalLayout，先记录开始软键盘没有弹出时底部的位置
                if (rootBottom == Integer.MIN_VALUE) {
                    rootBottom = r.bottom;
                    return;
                }

                if (r.bottom < rootBottom){
                    RelativeLayout.LayoutParams  lp = (RelativeLayout.LayoutParams) userNameLayout.getLayoutParams();
                    logo.setVisibility(View.INVISIBLE);
                    logoBottom.setVisibility(View.INVISIBLE);

                    if (lp.getRules()[RelativeLayout.CENTER_VERTICAL] != 0){
                     lp.addRule(RelativeLayout.CENTER_VERTICAL, 0); // 取消竖直居中
                     lp.setMargins(10,200,0,0);
                     lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);


                      }

                    userNameLayout.setLayoutParams(lp);

                }
                else{
                    RelativeLayout.LayoutParams  lp = (RelativeLayout.LayoutParams) userNameLayout.getLayoutParams();

                    logo.setVisibility(View.VISIBLE);
                    logoBottom.setVisibility(View.VISIBLE);
                    if (lp.getRules()[RelativeLayout.CENTER_VERTICAL] == 0){

                        lp.addRule(RelativeLayout.CENTER_VERTICAL);
                        lp.setMargins(10,50,0,0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);

                    }
                    userNameLayout.setLayoutParams(lp);


                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mReceiver, filter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_aty, menu);
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
    //点击登录的响应时事件
    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                loginName= userName.getText().toString();
                loginPassword = password.getText().toString();
                //判断网络连接情况和输入情况，空判断。
                if(loginName.isEmpty()||loginPassword.isEmpty()|| !netState){
                    if (!netState)
                    Toast.makeText(getApplicationContext(),"当前网络不可用，请您检查网络后重试！",Toast.LENGTH_LONG).show();
                    else
                    Toast.makeText(getApplicationContext(), "请填写用户名密码！", Toast.LENGTH_SHORT).show();
                }
                else{
                   mProgress = new ProgressDialog(mainContext);
                   mProgress.setMessage("登录中。。。");
                   mProgress.show();
                   String urlString = HttpHead.head+ API.LOGIN;
                    final RequestParams params = new RequestParams();
                    params.put("username", loginName);
                    params.put("password", loginPassword);
                    LogUtil.v("username,password",loginName+""+loginPassword);
//                    Intent intent = new Intent(LoginAty.this,MainAty.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
                   //发送网络请求，参数时登录名和密码，获取到个人信息
                   HttpUtil.get(urlString, params, new JsonHttpResponseHandler(){
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                           super.onSuccess(statusCode, headers, response);
                           Log.i("Json", "on Success");
                           mProgress.dismiss();
                           String userId,name,mobile,bizArea,eleArea,address,email,ammeter,gwId;
                           int type,status;
                           String userType;
                           try {
                               if (response.getInt("flag") == 1){
                                    status = response.getInt("status");
                                    userId = response.getString("userId");
                                    name = response.getString("name");
                                    mobile = response.getString("mobile");
                                    type = response.getInt("type");
                                    bizArea = response.getString("bizArea");
                                    eleArea = response.getString("eleArea");
                                    address = response.getString("address");
                                    email = response.getString("email");
                                    ammeter = response.getString("ammeter");
                                    gwId = response.getString("gwId");
                                    if (type == 1 ){
                                          userType = "试用";
                                    }
                                    else  {
                                         userType = "商用";
                                    }
                                   LogUtil.v("userId",userId);
                                   AppConfig.getInstance().setPassword(loginPassword);
                                   AppConfig.getInstance().setUsername(loginName);
                                   AppConfig.getInstance().setUserId(userId);
                                   AppConfig.getInstance().setName(name);
                                   AppConfig.getInstance().setMobil(mobile);

                                   Intent intent = new Intent(LoginAty.this,MainAty.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   intent.putExtra("userId",userId);
                                   intent.putExtra("mobile",mobile);
                                   intent.putExtra("type",userType);
                                   intent.putExtra("name",name);
                                   intent.putExtra("bizArea",bizArea);
                                   intent.putExtra("eleArea",eleArea);
                                   intent.putExtra("address",address);
                                   intent.putExtra("email",email);
                                   intent.putExtra("ammeter",ammeter);
                                   if (status != 0 )
                                       Toast.makeText(getApplicationContext(),"该账户被冻结！",Toast.LENGTH_LONG).show();
                                   else{
                                       startActivity(intent);
                                       finish();
                                   }
                               }
                               else {
                                   String msg = response.getString("msg");
                                   Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                   userName.setText("");
                                   password.setText("");

                                   ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LoginAty.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                               }

                           }catch (Exception e){
                               Log.e("Json",e.toString());
                           }
                          }

                       @Override
                       public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                           super.onFailure(statusCode, headers, throwable, errorResponse);
                           mProgress.dismiss();
                           Toast.makeText(getApplicationContext(), "请求服务器失败，请您稍后再试", Toast.LENGTH_LONG).show();
                           ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LoginAty.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                           LogUtil.v("Failure", "!!!!!!!!!!");

//                           Log.e("Json",errorResponse.toString());
                       }

                       @Override
                       public void onFinish() {
                           super.onFinish();
                           LogUtil.v("@@@@@@@","");
                       }
                   });
//                  AppConfig.getInstance().setPassword(loginPassword);
//                  AppConfig.getInstance().setUsername(loginName);
//
//                    Intent intent = new Intent(LoginAty.this,MainAty.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                     startActivity(intent);
//                      finish();
//                    new GetDataTask().execute(url+loginName+"/"+password+"/1");
 }

//            Intent intent = new Intent(LoginAty.this,MainAty.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
        }
    };
    //网络状态的广播接收
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                Log.i("LoginActivity", "unconnect");
                netState = false;
// unconnect network
                Toast.makeText(getApplicationContext(),"当无法连接服务器，请检查您的网络，稍后再试",Toast.LENGTH_LONG).show();
            }else {
// connect network
                netState =true;
//                Toast.makeText(getApplicationContext(),"当前网络环境正常",Toast.LENGTH_LONG).show();
            }
        }     };
  //注销接收器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
        unregisterReceiver(mReceiver);
    }
    public void showDialog(Activity activity,String titleName) {
        final  AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        android.view.Window window = dialog.getWindow(); //获取窗口
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_change_title);//为窗口添加layout
        TextView title = (TextView) window.findViewById(R.id.this_title);
        title.setText("重新设置域名");
        final EditText editText = (EditText) window.findViewById(R.id.name_door_and_win);
        Button confirm = (Button) window.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newHttpHead = editText.getText().toString();
                if (!newHttpHead.equals("")){
                    HttpHead.setHead(newHttpHead);
                }
                else
                    Toast.makeText(getApplicationContext(),"您输入的为空，更改无效",Toast.LENGTH_LONG).show();
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
    public void createDB() throws Exception{
        myDBHelper = new MyDBHelper(this);
        myDBHelper.getWritableDatabase();
//        Log.i(TAG, "´´½¨³É¹¦");
    }
    public void getHttpHead() throws Exception{
        DBManager dbManager = DBManageImpl.getDBInstance(getApplicationContext());
       if (!dbManager.findByHead("head").equals("")){
           HttpHead.setHead(dbManager.findByHead("head"));
           LogUtil.v("db",HttpHead.forhead);
       }
        else {
           dbManager.addItem("head",HttpHead.forhead);
       }
    }
}
