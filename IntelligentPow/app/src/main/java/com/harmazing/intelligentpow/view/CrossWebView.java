package com.harmazing.intelligentpow.view;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by JTL on 2014/10/30.
 * 解决WebView的跨域问题，访问android WebView private 对象 WebViewCore mWebViewCore，
 * 调用 mWebViewCore的private 方法nativeRegisterURLSchemeAsLocal，
 * 把http和https弄成本地访问。 XMLHttpRequest即可自由跨域。
 */
public class CrossWebView extends WebView {
    public CrossWebView(Context context,android.util.AttributeSet attrs){
       super(context,attrs);

    }
    public CrossWebView(Context context, android.util.AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    public void enablecrossdomain41()
    {
        try{
            Field webviewclassic_field = WebView.class.getDeclaredField("mProvider");
            webviewclassic_field.setAccessible(true);
            Object webviewclassic = webviewclassic_field.get(this);
            Field webviewcore_field = webviewclassic.getClass().getDeclaredField("mWebViewCore");
            webviewcore_field.setAccessible(true);
            Object mWebViewCore = webviewcore_field.get(webviewclassic);
            Field nativeclass_field = webviewclassic.getClass().getDeclaredField("mNativeClass");
            nativeclass_field.setAccessible(true);
            Object mNativeClass = nativeclass_field.get(webviewclassic);
            Method method = mWebViewCore.getClass().getDeclaredMethod("nativeRegisterURLSchemeAsLocal",new Class[] {int.class, String.class});
            method.setAccessible(true);
            method.invoke(mWebViewCore, mNativeClass, "http");
            method.invoke(mWebViewCore, mNativeClass, "https");
        }
        catch(Exception e){
            Log.d("webView", "enablecrossdomain41 error");
            e.printStackTrace();
        }
    }
}
