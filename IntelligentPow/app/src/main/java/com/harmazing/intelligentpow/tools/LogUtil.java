package com.harmazing.intelligentpow.tools;

import android.util.Log;

import com.harmazing.intelligentpow.BuildConfig;

/**
 * Created by Administrator on 2015/4/20.
 */
public class LogUtil {
    public static boolean isDebug = BuildConfig.DEBUG;

    public static void v(String tag, Object o){
        if(isDebug){
            Log.v(tag, "" + o)  ;
        }
    }
    public static void d(String tag, Object o){
        if(isDebug){
            Log.d(tag, ""+o)  ;
        }
    }
    public static void i(String tag, Object o){
        if(isDebug){
            Log.i(tag, ""+o)  ;
        }
    }
    public static void w(String tag, Object o){
        if(isDebug){
            Log.w(tag, ""+o)  ;
        }
    }
    public static void e(String tag, Object o){
        if(isDebug){
            Log.e(tag, ""+o)  ;
        }
    }

}
