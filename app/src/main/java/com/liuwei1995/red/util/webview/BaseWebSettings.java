package com.liuwei1995.red.util.webview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.liuwei1995.red.BaseApplication;


/**
 * Created by liuwei on 2017/4/7
 */

public class BaseWebSettings {

    public static void setCacheMode(WebView mWebView,boolean userCache){
        if (userCache || !BaseApplication.NETWORK_IS_AVAILABLE) {
            //优先使用缓存：
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        }else {
            //不使用缓存：
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        }
    }
    public static void WebSettings(@NonNull final Context context,@NonNull final WebView mWebView){
        synchronized (BaseWebSettings.class){
            WebSettings settings = mWebView.getSettings();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
//            TODO  图片延迟加载
//            mWebView.getSettings().setBlockNetworkImage(true);

            if (BaseApplication.NETWORK_IS_AVAILABLE) {
                //不使用缓存：
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
            }else {
                //优先使用缓存：
                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
            }


            mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            // 支持多窗口
            mWebView.getSettings().setSupportMultipleWindows(true);

            // 开启 DOM storage API 功能
            settings.setDomStorageEnabled(true);

            //开启 database storage API 功能
            settings.setDatabaseEnabled(true);
            final String dbPath = context.getDir("db", Context.MODE_PRIVATE).getPath();
            //设置数据库缓存路径
            settings.setDatabasePath(dbPath);


            // 开启 Application Caches 功能
            settings.setAppCacheEnabled(true);
            final String cachePath = context.getDir("cache",Context.MODE_PRIVATE).getPath();
            settings.setAppCachePath(cachePath);
            settings.setAppCacheMaxSize(5*1024*1024);


            if(Build.VERSION.SDK_INT >= 19){
                settings.setLoadsImagesAutomatically(true);
            }else {
                settings.setLoadsImagesAutomatically(false);
            }
//硬件加速
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//不能播放视频
//                mWebView.setLayerType(View.LAYER_TYPE_NONE,null);
//                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//            }

            //可不要------------------------------------
            mWebView.setInitialScale(25);//为25%，最小缩放等级
            // 设置可以支持缩放
            settings.setSupportZoom(true);
            // 设置出现缩放工具
//        settings.setBuiltInZoomControls(true);
            //扩大比例的缩放


            //设置自适应屏幕，两者合用
            settings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
            settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
            // 设置加载进来的页面自适应手机屏幕
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//支持内容重新布局




            mWebView.setWebChromeClient(new WebChromeClient()); // chrom

            settings.setPluginState(WebSettings.PluginState.ON);

            settings.supportMultipleWindows();//多窗口

            settings.setAllowFileAccess(true);  //设置可以访问文件
            settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
            settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
            settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
            settings.setDefaultTextEncodingName("utf-8");//设置编码格式
//支持获取手势焦点，输入用户名、密码或其他
            mWebView.requestFocusFromTouch();
            settings.setJavaScriptEnabled(true);  //支持js
        }
    }
}
