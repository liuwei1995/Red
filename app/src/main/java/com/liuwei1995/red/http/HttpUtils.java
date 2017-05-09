package com.liuwei1995.red.http;

import java.util.Map;

import okhttp3.FormBody;

/**
 * Created by liuwei on 2017/3/22
 */

public class HttpUtils {

    public static FormBody.Builder getFormBodyBuilder() {
        return new FormBody.Builder();
    }


    public static void getJinRiTouTiao(HttpCallback httpcallback){
        String url = "http://is.snssdk.com/api/news/feed/v51/?category=news_hot&refer=1&count=20&lac=11&cid=8474&cp=5e89e148593a6q1&iid=9171255918&device_id=34948327666";
        OkHttpClientUtils.newInstance().get(url,httpcallback);
    }

    public static final String IP = "http://139.201.126.215:8080";
    //    public static final String IP = "http://192.168.0.103:8080";
    public static final String http = IP+"/MiaoMiaoServer/app/user/";
//    public static final String http = "http://192.168.0.103:8080"+"/MiaoMiaoServer/app/user/";

    public static final String userLogin = "ofoLogin";
    public static final String ofoSaveAccountPassword = "ofoSaveAccountPassword";
    public static final String ofoSearchAccountPassword = "ofoSearchAccountPassword";

    public static final String ofoGetAccountPassword = "ofoGetAccountPassword";

    public static void userLogin(Map<String,Object> map,HttpCallback httpcallback){
        OkHttpClientUtils.newInstance().post(http+userLogin,map,httpcallback);
    }

    public static void ofoSearchAccountPassword(Map<String,Object> map,HttpCallback httpcallback){
        OkHttpClientUtils.newInstance().post(http+ofoSearchAccountPassword,map,httpcallback);
    }
    public static <T>void ofoGetAccountPassword(Map<String,Object> map,HttpCallback<T> httpcallback){
        OkHttpClientUtils.newInstance().post(http+ofoGetAccountPassword,map,httpcallback);
    }


    public static <T>void saveAccountPassword(Map<String,Object> map,HttpCallback<T> httpcallback){
        OkHttpClientUtils.newInstance().post(http+ofoSaveAccountPassword,map,httpcallback);
    }

}
