package com.harmazing.intelligentpow.UI;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


/**
 * Created by JTL on 2014/9/17.
 * 主界面
 */

public class MainAty extends BaseActivity{
    private  static String TAG ="MainActivity";
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private Fragment mContent,personFragment,aboutUsFragment,settingFragment,safeSettingFragment;//定义主界面Fragment，和个人信息Fragment
    private Fragment mDeviceList;            //设备信息列表
    private ImageView personImgView;         //头像
    private TextView titleText;              //actionbar标题
    private Boolean isExit = false;          //退出提示标志旗
    private Handler mHandler;                //消息处理
    private ImageView btnAddCurve;           //添加曲线
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*获取屏幕信息做屏幕的适配*/
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
       /*初始化Fragement*/
        if (savedInstanceState != null){
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
            mDeviceList = getSupportFragmentManager().getFragment(savedInstanceState, "mDeviceList");
        }
        if (mContent == null){
            mContent =  new MainTitleFragement();
            mDeviceList = new MainDeviceListFragement();
//            mSensorList = new MainSensorListFragment();
        }
        if (mDeviceList == null)
            mDeviceList = new MainDeviceListFragement();

        personFragment = new PersonFragment();
        aboutUsFragment = new AboutUsFragment();
        settingFragment = new SettingFragment();
        safeSettingFragment = new SafeSettingFragment();
        // set the Above View
        setContentView(R.layout.content_frame);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame,mContent).commit();
//        changeFragementContent(R.id.content_frame,mContent);
        getSupportFragmentManager().beginTransaction().add(R.id.listdevice,mDeviceList).commit();
//        changeFragementContent(R.id.listdevice,mDeviceList);
//        changeFragementContent(R.id.listsensor,mSensorList);
       // set the behind View
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().add(R.id.listview, new PersonDataListFragment()).commit();
           getSupportActionBar().setDisplayShowTitleEnabled(true);
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          setSlidingActionBarEnabled(true);
          getSlidingMenu().setBehindOffset(width/3);
        ImageView imageView = (ImageView) findViewById(R.id.head);
//        imageView.setOnClickListener(personClickListener);
       /*添加BroadcastReceiver的接收器*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mReceiver,filter);
        /*处理二次点击的消息，改变标志旗*/
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
        getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                try{
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainAty.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }catch (Exception e){
                    LogUtil.v("异常", "nullPointerException");
                }
            }
        });
    }
    /*点击头像，切换personFragment*/
//    View.OnClickListener personClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            personImgView = (ImageView) findViewById(R.id.logo);
//            titleText = (TextView) findViewById(R.id.this_title);
//            if (getActionBar() != null){
//            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_person_actionbar));//更换actionbar背景
//            getActionBar().setLogo(R.drawable.icon_back_left);}
//            //将栈内可能出现上面的Fragment清除
//            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
//            if (!mContent.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mContent).commit();
//            if (!mDeviceList.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
//           //判断是否add过，如果add过直接show，没有就先add
//            if (!personFragment.isAdded())
//            getSupportFragmentManager().beginTransaction().add(R.id.content_frame,personFragment)
//                    .show(personFragment).commit();
//            else
//            getSupportFragmentManager().beginTransaction().show(personFragment).commit();
//            personImgView.setVisibility(View.INVISIBLE);
//            titleText.setText("个人中心");
//            titleText.setVisibility(View.VISIBLE);
//            getSlidingMenu().showContent();
//        }
//    };

    /**
     * 保存Fragment状态
     * @param outState 记录状态
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    /**
     * 切换主界面Fragment的方法
     * @param fragment 需要切换的Fragment
     * @param itemId   根据itemId放置Title
     */

    public void switchContent(Fragment fragment ,int itemId) {

        personImgView = (ImageView) findViewById(R.id.logo);
        titleText = (TextView) findViewById(R.id.this_title);
        btnAddCurve = (ImageView) findViewById(R.id.iv_add);
        if (fragment instanceof MainTitleFragement){
              hideAllFragment();
//            if (!personFragment.isHidden())
//            getSupportFragmentManager().beginTransaction().hide(personFragment).commit();
//            getSupportFragmentManager().beginTransaction()
//                    .hide(getSupportFragmentManager().findFragmentById(R.id.content_frame))
//                    .commit();
//            if (!settingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(settingFragment).commit();
//            if (!safeSettingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(safeSettingFragment).commit();
//            if (!aboutUsFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(aboutUsFragment).commit();
            getSupportFragmentManager().beginTransaction()
                    .show(mContent).commit();
            getSupportFragmentManager().beginTransaction()
                    .show(mDeviceList).commit();
            getSlidingMenu().showContent();
            getSlidingMenu().setSlidingEnabled(true);
//            personImgView.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.INVISIBLE);
            btnAddCurve.setVisibility(View.INVISIBLE);
            getSupportActionBar().setLogo(R.drawable.icon_person);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar_titile));
        }
        //安全设置
        if (itemId == 2){
            titleText.setText("安全设置");
            hideAllFragment();
//            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
//            if (!mDeviceList.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
//            if (!personFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(personFragment).commit();
//            if (!mContent.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mContent).commit();
//            if (!settingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(settingFragment).commit();
//            if (!aboutUsFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(aboutUsFragment).commit();
            if (!safeSettingFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame, safeSettingFragment).commit();

            getSupportFragmentManager().beginTransaction().show(safeSettingFragment).commit();

            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
            getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.icon_back_left));
//            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_head_banner));
            personImgView.setVisibility(View.INVISIBLE);
            titleText.setVisibility(View.VISIBLE);
            btnAddCurve.setVisibility(View.INVISIBLE);
            getSlidingMenu().showContent();
            getSlidingMenu().setSlidingEnabled(true);
        }
        //舒睡曲线
        if (itemId == 4){
            titleText.setText("舒睡曲线");
            hideAllFragment();
//            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
//            if (!mDeviceList.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
//            if (!personFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(personFragment).commit();
//            if (!mContent.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mContent).commit();
//            if (!settingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(settingFragment).commit();
//            if (!safeSettingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(safeSettingFragment).commit();
            if (!aboutUsFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame, aboutUsFragment).commit();
            getSupportFragmentManager().beginTransaction().show(aboutUsFragment).commit();
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
            getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.icon_back_left));
//            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_head_banner));
            personImgView.setVisibility(View.INVISIBLE);
            titleText.setVisibility(View.VISIBLE);
            btnAddCurve.setVisibility(View.VISIBLE);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().showContent();
        }
        //设置
        if (itemId == 5){
            titleText.setText("设      置");
            hideAllFragment();
//            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
//            if (!mDeviceList.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
//            if (!personFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(personFragment).commit();
//            if (!mContent.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mContent).commit();
//            if (!aboutUsFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(aboutUsFragment).commit();
//            if (!safeSettingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(safeSettingFragment).commit();
            if (!settingFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame,settingFragment).commit();
            getSupportFragmentManager().beginTransaction().show(settingFragment).commit();
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_aboutus_title));
            getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.icon_back_left));
//            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_head_banner));
            personImgView.setVisibility(View.INVISIBLE);
            titleText.setVisibility(View.VISIBLE);
            btnAddCurve.setVisibility(View.INVISIBLE);
            getSlidingMenu().showContent();
            getSlidingMenu().setSlidingEnabled(true);
        }
        //个人中心
        if (itemId == 1){
            personImgView = (ImageView) findViewById(R.id.logo);
            titleText = (TextView) findViewById(R.id.this_title);
            if (getActionBar() != null){
                getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_person_actionbar));//更换actionbar背景
                getActionBar().setLogo(R.drawable.icon_back_left);}
            hideAllFragment();
//            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
//            if (!mDeviceList.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
//            if (!mContent.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(mContent).commit();
//            if (!aboutUsFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(aboutUsFragment).commit();
//            if (!safeSettingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(safeSettingFragment).commit();
//            if (!settingFragment.isHidden())
//                getSupportFragmentManager().beginTransaction().hide(settingFragment).commit();
            if (!personFragment.isAdded())
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame,personFragment)
                        .show(personFragment).commit();
            else
                getSupportFragmentManager().beginTransaction().show(personFragment).commit();
            personImgView.setVisibility(View.INVISIBLE);
            titleText.setText("个人中心");
            titleText.setVisibility(View.VISIBLE);
            btnAddCurve.setVisibility(View.INVISIBLE);
            getSlidingMenu().showContent();
            getSlidingMenu().setSlidingEnabled(true);
        }

}

    /**
     * 键盘监听返回键
     * @param keyCode  按下的键盘码
     * @param event  键盘活动
     * @return   true 搞定 false 传播
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mContent.isVisible() && !getSlidingMenu().isMenuShowing())
               exit();
            else
              toggle();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

            return true;

    }

    /**
     * 注册一个广播接受系统对网络情况的监听，返回网络状况
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                Log.i(TAG, "unconnect");
// unconnect network
                Toast.makeText(getApplicationContext(),"当无法连接服务器，请检查您的网络，稍后再试",Toast.LENGTH_LONG).show();
            }else {
// connect network
//                Toast.makeText(getApplicationContext(),"当前网络环境正常",Toast.LENGTH_LONG).show();
            }
        }     };

    /**
     * Destroy解除消息接收器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    /**
     * 键盘的退出键，点击两次后退出。
     */
    public void exit(){
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }
    private void hideAllFragment(){
        getSupportFragmentManager().beginTransaction().hide(personFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .hide(getSupportFragmentManager().findFragmentById(R.id.content_frame))
                .commit();
        getSupportFragmentManager().beginTransaction().hide(settingFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(safeSettingFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(aboutUsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(mDeviceList).commit();
        getSupportFragmentManager().beginTransaction().hide(mContent).commit();
    }
}
