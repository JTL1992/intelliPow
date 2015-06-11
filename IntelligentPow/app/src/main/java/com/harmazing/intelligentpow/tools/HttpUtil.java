package com.harmazing.intelligentpow.tools;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

/**
 * Created by JTL on 2014/10/14.
 * Async -http的辅助工具类
 */
public class HttpUtil {
    private static AsyncHttpClient client =new AsyncHttpClient();    //实例话对象
    static
    {
        client.setTimeout(1800000);   //设置链接超时，如果不设置，默认为10s
    }
    public static void get(String urlString,AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,AsyncHttpResponseHandler res)   //url里面带参数
    {
        client.get(urlString, params,res);
    }
    public static void get(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client.get(urlString, params,res);
    }
    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        client.get(uString, bHandler);
    }
    public static void post(String urlString,RequestParams params,JsonHttpResponseHandler res){
        client.post(urlString, params,res);
    }
    public static void post(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.post(urlString, res);
    }
    public static void removeAllHeader(){
//        client.removeHeader("X-HTTP-Method-Override");
        client.removeAllHeaders();
    }

    public static void addClientHeader()
    {
        HttpUtil.getClient().addHeader("Content-type","application/json");
    }
    public static void post(Context context, String url, String bodyAsJson, AsyncHttpResponseHandler responseHandler){
        try {
            ByteArrayEntity entity = new ByteArrayEntity(bodyAsJson.getBytes("UTF-8"));
            client.post(context, url, entity, "application/json", responseHandler);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static AsyncHttpClient getClient()
    {
        return client;
    }
}