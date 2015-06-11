package com.harmazing.intelligentpow.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by JTL on 2014/10/3.
 * 网络测试
 */
public class NetStatus {
    public final static int NET_CONNECT_OK = 1;                     //连接网络正常
    public final static int NET_CONNECT_TIMEOUT = 2;                //连接网络超时
    public final static int NET_NOT_PREPARE = 3;                    //网络未准备好
    public final static int NET_ERROR = 4;                          //网络错误
    private final static int TIMEOUT = 3000;                              //网络超时时间
    private final static String INTERNET_URL = "http://www.baidu.com";    //网络地址，用于判断网络状态

    /**
     * 获取网络状态
     * @param context
     * @return
     */
    public static int getNetStatus(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo != null) {
                    if (networkinfo.isAvailable() && networkinfo.isConnected()) {
//                        if (!connectionNetwork())
//                            return NET_CONNECT_TIMEOUT;
//                        else
                            return NET_CONNECT_OK;
            }       else {
                      return NET_NOT_PREPARE;
                         }
           }
}
        } catch (Exception e) {
        return NET_ERROR;
        }
        return NET_ERROR;
        }

/**
 * 连接Internet地址，判断网络连接状态
 * @return
 */
static public boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
        httpUrl = (HttpURLConnection) new URL(INTERNET_URL)
        .openConnection();
        httpUrl.setConnectTimeout(TIMEOUT);
        httpUrl.connect();
        result = true;
        }
        catch (IOException e) {
            Log.v("Exception","!!!!!!!");
        }
        finally {
        if (null != httpUrl)
        httpUrl.disconnect();
        httpUrl = null;
        }
//        boolean result = true;
        return result;
    }

    /**
     * 判断是否为3G网络
     * @param context
     * @return
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为WiFi网络
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为2G网络
     * @param context
     * @return
     */
    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
            return true;
        }
        return false;
    }

    /**
     * 判断WiFi是否已经打开
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 获取本机ip地址
     * @return
     */
    public static String GetHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取本机IMEI码
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取WiFi Mac地址
     * @param context
     * @return
     */
    public static String getWifiMacAddr(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

}

