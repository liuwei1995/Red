package com.liuwei1995.red.http;

import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.Utils;
import com.liuwei1995.red.http.util.ICallback;
import com.liuwei1995.red.http.util.IHttpPresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;


/**
 * Created by liuwei on 2017/5/25 09:33
 */

public final class OkHttpPresenter implements IHttpPresenter {


//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    public static final MediaType MIXED = MediaType.parse("multipart/mixed");
//    public static final MediaType ALTERNATIVE = MediaType.parse("multipart/alternative");
//    public static final MediaType DIGEST = MediaType.parse("multipart/digest");
//    public static final MediaType PARALLEL = MediaType.parse("multipart/parallel");
//    public static final MediaType FORM = MediaType.parse("multipart/form-data");

    /**
     * 取消请求的时候用
     */
    public static final Map<Object, Call> map = new HashMap<>();

    private OkHttpPresenter() {
    }

    private static OkHttpPresenter okHttpUtils = null;
    private static Handler mainHandler;
    public static OkHttpPresenter newInstance() {
        if (okHttpUtils == null) {
            synchronized (OkHttpPresenter.class){
                if(okHttpUtils == null){
                    okHttpUtils = new OkHttpPresenter();
                    //更新UI线程
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return  okHttpUtils;
    }

    @Override
    public void get(String url) {
        get(url,null);
    }

    @Override
    public void get(String url, ICallback callback) {
        get(url,null,null,callback);
    }

    @Override
    public void get(String url, Object tag, ICallback callback) {
        get(url,null,tag,callback);
    }

    @Override
    public void get(String url, Map<String, String> params, ICallback callback) {
        get(url,params,null,callback);
    }
    public void get(String url, Map<String, String> params, final Object tag, final ICallback callback) {
        if(params != null && !params.isEmpty()){
            if(url.trim().endsWith("/")){
                url = url.trim().substring(0, url.length()-1)+"?";
            }else {
                url = url.trim()+"?";
            }
            for (String key : params.keySet()) {
                Object value = params.get(key);
                url += key+"="+(value == null?null:value.toString())+"&";
            }
            url = url.substring(0, url.length()-1);
        }

        Headers.Builder headersBuilder = new Headers.Builder();
        Request request = new Builder().url(url).headers(headersBuilder.build()).build();

        Builder newBuilder = request.newBuilder();

        PersistentCookieStore persistentCookieStore = new PersistentCookieStore(Utils.getContext());
        List<Cookie> cookies = persistentCookieStore.getCookies();
        if (!cookies.isEmpty()) {
            newBuilder.header("Cookie", cookieHeader(cookies));
        }

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.cookieJar(new CookiesManager());
        OkHttpClient client = b.build();

        Call call = client.newCall(request);
        if(tag != null){
            addTag(tag,call);
        }
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                remove(tag);
                if(callback == null)return;
                final String string = response.body().string();
                if(response.isSuccessful()){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCallbackResult(true,string);
                        }
                    });
                }else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCallbackResult(false,string);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                remove(tag);
                if(callback == null)return;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCallbackResult(false,null);
                    }
                });
            }
        });
    }
    @Override
    public void post(String url) {
        post(url,null);
    }

    @Override
    public void post(String url, ICallback callback) {
        post(url,null,callback);
    }

    @Override
    public void post(String url, Object tag, ICallback callback) {
        post(url,null,tag,callback);
    }

    @Override
    public void post(String url, Map<String, Object> params, final ICallback callback) {
        post(url,params,null,callback);
    }

    public void post(String url, Map<String, Object> params, final Object tag, final ICallback callback) {
        FormBody.Builder builder = null;
        if(params != null){
            builder = getFormBodyBuilder();
            for (String key : params.keySet()) {
                builder.add(key,params.get(key) != null ? params.get(key).toString():"");
            }
        }
        Builder requestBuilder;
        if(builder != null){
            requestBuilder = new Builder().url(url).post(builder.build());
        }else {
            requestBuilder = new Builder().url(url);
        }

        Headers.Builder headersBuilder = new Headers.Builder();
        requestBuilder = requestBuilder.headers(headersBuilder.build());

        Request request = requestBuilder.build();

        Builder newBuilder = request.newBuilder();
        PersistentCookieStore persistentCookieStore = new PersistentCookieStore(Utils.getContext());
        List<Cookie> cookies = persistentCookieStore.getCookies();
        if (!cookies.isEmpty()) {
            newBuilder.header("Cookie", cookieHeader(cookies));
        }
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.cookieJar(new CookiesManager());
        OkHttpClient client = b.build();

        Call call = client.newCall(request);
        if(tag != null){
            addTag(tag,call);
        }
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                remove(tag);
                if(callback == null)return;
                    final String string = response.body().string();
                    if(response.isSuccessful()){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCallbackResult(true,string);
                            }
                        });
                    }else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCallbackResult(false,string);
                            }
                        });
                    }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                remove(tag);
                if(callback == null)return;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCallbackResult(false,null);
                    }
                });
            }
        });
    }

    /**
     * 添加数据
     * @return Builder  add("key", "value")
     */
    private FormBody.Builder getFormBodyBuilder() {
        return new FormBody.Builder();
    }
    public static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
    }

    private static void addTag(Object key, Call value){
        synchronized (OkHttpPresenter.class){
            map.put(key,value);
        }
    }
    public  void cancel(Object tag) {
        synchronized (OkHttpPresenter.class){
            Call call = map.remove(tag);
            if(call != null){
                call.cancel();
            }
        }
    }
    private  static void remove(Object tag){
        synchronized (OkHttpPresenter.class){
            if(tag != null)
                map.remove(tag);
        }
    }

}
