package com.liuwei1995.red.http;

import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by liuwei on 2017/3/22
 */

public class OkHttpClientUtils {

	private static OkHttpClientUtils okHttpUtils = null;

	private OkHttpClientUtils() {
	}
	private static Handler mainHandler;
	public static  OkHttpClientUtils newInstance() {
		if (okHttpUtils == null) {
			synchronized (OkHttpClientUtils.class){
				if(okHttpUtils == null){
					okHttpUtils = new OkHttpClientUtils();
					//更新UI线程
					mainHandler = new Handler(Looper.getMainLooper());
				}
			}
		}
		return  okHttpUtils;
	}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static final MediaType MIXED = MediaType.parse("multipart/mixed");
	public static final MediaType ALTERNATIVE = MediaType.parse("multipart/alternative");
	public static final MediaType DIGEST = MediaType.parse("multipart/digest");
	public static final MediaType PARALLEL = MediaType.parse("multipart/parallel");
	public static final MediaType FORM = MediaType.parse("multipart/form-data");

	/**
	 * 取消请求的时候用
	 */
	public static final Map<Object, Call> map = new HashMap<Object, Call>();



	public synchronized void post(String url,final HttpCallback httpCallback){
		postOk(url,null,null,null,httpCallback);
	}

	public synchronized void post(String url,Map<String,Object> map,final HttpCallback httpCallback){
		post(url,map,null,httpCallback);
	}
	public synchronized void post(String url,Map<String,Object> map,final Object tag,final HttpCallback httpCallback){
		FormBody.Builder builder = getFormBodyBuilder();
		if(map != null)
		for (String key : map.keySet()) {
			builder.add(key,map.get(key) != null ? map.get(key).toString():"");
		}
		postOk(url,builder,tag,null,httpCallback);
	}
	public synchronized void post(String url,FormBody.Builder builder,final HttpCallback httpCallback){
		postOk(url,builder,null,null,httpCallback);
	}
	public synchronized void post(String url,FormBody.Builder builder,final Object tag,final HttpCallback httpCallback){
		postOk(url,builder,tag,null,httpCallback);
	}
	public synchronized void post(String url,FormBody.Builder builder,final Object tag,Headers.Builder headersBuilder,final HttpCallback httpCallback){
		postOk(url,builder,tag,headersBuilder,httpCallback);
	}

	private synchronized static <T extends JSONObject> void postOk(String url,FormBody.Builder builder,final Object tag,Headers.Builder headersBuilder,final HttpCallback<T> httpCallback) {

		Request request = null;
		if(builder != null){
			if(headersBuilder == null){
				request = new Builder().url(url).post(builder.build()).build();
			}else {
				request = new Builder().url(url).post(builder.build()).headers(headersBuilder.build()).build();
			}
		}else
		{
			if(headersBuilder == null){
				request = new Builder().url(url).build();
			}else {
				request = new Builder().url(url).headers(headersBuilder.build()).build();
			}
		}
		OkHttpClient.Builder b = new OkHttpClient.Builder();

		b.cookieJar(new CookiesManager());

		OkHttpClient client = b.build();

		Builder newBuilder = request.newBuilder();
		final PersistentCookieStore persistentCookieStore = new PersistentCookieStore(Utils.getContext());
		List<Cookie> cookies = persistentCookieStore.getCookies();
		if (!cookies.isEmpty()) {
			newBuilder.header("Cookie", cookieHeader(cookies));
		}
		Call call = client.newCall(request);
		if(tag != null){
			addTag(tag,call);
		}
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				 remove(tag);
				if(httpCallback == null)return;
				try {
					String string = response.body().string();
					final JSONObject jsonObject = new JSONObject(string);
					if(response.isSuccessful()){
						mainHandler.post(new Runnable() {
							@Override
							public void run() {
								httpCallback.onResponse(true, (T) jsonObject);
							}
						});
					}else {
						mainHandler.post(new Runnable() {
							@Override
							public void run() {
								httpCallback.onResponse(false,(T) jsonObject);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							httpCallback.onResponse(false,null);
						}
					});
				}
			}
			@Override
			public void onFailure(Call call, IOException e) {
				 remove(tag);
				if(httpCallback == null)return;
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						httpCallback.onResponse(false, null);
					}
				});
			}
		});
	}
	private static String cookieHeader(List<Cookie> cookies) {
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
//	public void receiveHeaders(Headers headers) throws IOException {
//		if (client.cookieJar() == CookieJar.NO_COOKIES) return;
//
//		List<Cookie> cookies = Cookie.parseAll(userRequest.url(), headers);
//		if (cookies.isEmpty()) return;
//
//		client.cookieJar().saveFromResponse(userRequest.url(), cookies);
//	}

	public synchronized static void postExecute(String url, Map<String, Object> map,final HttpCallback httpCallback) {
		postExecute(url, map,null,httpCallback);
	}
	public synchronized static void postExecute(String url, Map<String, Object> map,Object tag,final HttpCallback httpCallback) {
		postExecute(url, new JSONObject(map).toString(),tag,httpCallback);
	}
	public static Headers.Builder getHeadersBuilder() {
		return  new Headers.Builder();
	}
	public synchronized static void postExecute(String url, String json,Object tag,final HttpCallback httpCallback) {
		RequestBody body = RequestBody.create(JSON, json);
		postExecute(url,body,tag,null,httpCallback);
	}
	public synchronized static void postEnqueue(String url,FormBody.Builder body,final HttpCallback httpCallback) {
		postExecute(url, body.build(),null,null,httpCallback);
	}
	public synchronized static void postExecute(String url,FormBody.Builder body,Object tag,final HttpCallback httpCallback) {
		postExecute(url, body.build(),tag,null,httpCallback);
	}

	public synchronized static <T>void postExecute(String url,RequestBody body,Object tag,Headers.Builder headersBuilder,final HttpCallback<T> httpCallback) {
		OkHttpClient client = new OkHttpClient();
			try {
				Builder builder = new Builder().url(url);
				if(headersBuilder != null)
				builder.headers(headersBuilder.build());
			if(body != null)
				builder.post(body);
			Request request = builder.build();
			Call call = client.newCall(request);
			if(tag != null){
				addTag(tag,call);
			}
			Response response = call.execute();
			if(httpCallback == null)return;
			try {
				if(response.isSuccessful()){
                     remove(tag);
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    httpCallback.onResponse(true,(T) jsonObject);
                }else {
                     remove(tag);
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    httpCallback.onResponse(false,(T) jsonObject);
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			 remove(tag);
			if(httpCallback != null)
				httpCallback.onResponse(false,null);
		}
	}


	public  void get(String url,final HttpCallback httpCallback) {
		get(url,null,null,null,httpCallback);
	}

	public  void get(String url,Map<String, String> parm) {
		get(url, parm, null,null);
	}
	public  void get(String url,Map<String, String> parm,Headers.Builder headersBuilder) {
		get(url, parm, null,headersBuilder,null);
	}
	public  void get(String url,Map<String, String> parm,final HttpCallback httpCallback) {
		get(url, parm, null,httpCallback);
	}
	public  void get(String url,Map<String, String> parm,Object tag,final HttpCallback httpCallback) {
		get(url, parm, tag,httpCallback);
	}
	public  void get(String url,Map<String, String> parm,Headers.Builder headersBuilder,final HttpCallback httpCallback) {
		get(url, parm, null,headersBuilder,httpCallback);
	}
	public  void get(String url,Headers.Builder headersBuilder,final HttpCallback httpCallback) {
		get(url, null, null,headersBuilder,httpCallback);
	}
	public  void get(String url,Object tag,final HttpCallback httpCallback) {
		get(url, null, tag,null,httpCallback);
	}
	public  void get(String url,Object tag,Headers.Builder headersBuilder,final HttpCallback httpCallback) {
		get(url, null, tag,headersBuilder,httpCallback);
	}

	public  <T>void get(String url,final Map<String, String> parm,final Object tag,Headers.Builder headersBuilder,final HttpCallback<T> httpCallback) {
		if(parm != null && !parm.isEmpty()){
			if(url.trim().endsWith("/")){
				url = url.trim().substring(0, url.length()-1)+"?";
			}else {
				url = url.trim()+"?";
			}
			for (String key : parm.keySet()) {
				String value = parm.get(key);
				url += key+"="+value+"&";
			}
			url = url.substring(0, url.length()-1);
		}
		Request request = null;
		if(headersBuilder == null){
			request = new Builder().url(url).build();
		}else {
			request = new Builder().url(url).headers(headersBuilder.build()).build();
		}
		OkHttpClient client = new OkHttpClient.Builder().build();
		Call call = client.newCall(request);
		if(tag != null){
			addTag(tag,call);
		}
			call.enqueue(new Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					 remove(tag);
					if(httpCallback == null)return;
					try {
						final JSONObject jsonObject = new JSONObject(response.body().string());
						if(response.isSuccessful()){
							mainHandler.post(new Runnable() {
								@Override
								public void run() {
									httpCallback.onResponse(true,(T) jsonObject);
								}
							});
                        }else {
							mainHandler.post(new Runnable() {
								@Override
								public void run() {
									httpCallback.onResponse(false,(T) jsonObject);
								}
							});
                        }
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(Call call, IOException e) {
					 remove(tag);
					if(httpCallback == null)return;
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							httpCallback.onResponse(false,null);
						}
					});
				}
			});
	}
	public static void getExecute(String url) {
		getExecute(url, null,null,null);
	}
	public static void getExecute(String url,final HttpCallback httpCallback) {
		getExecute(url, null,null,httpCallback);
	}
	public static void getExecute(String url,Map<String, String> parm,final HttpCallback httpCallback) {
		getExecute(url, parm, null,httpCallback);
	}
	public static void getExecute(String url,Object tag,final HttpCallback httpCallback) {
		getExecute(url, null, tag,httpCallback);
	}
	/**
	 * get同步
	 * @param url
	 * @param parm
	 * @param tag
	 * @param httpCallback
	 */
	public static <T>void getExecute(String url,Map<String, String> parm,Object tag,final HttpCallback<T> httpCallback) {
		if(parm != null && !parm.isEmpty()){
			if(url.trim().endsWith("/")){
				url = url.trim().substring(0, url.length()-1)+"?";
			}else {
				url = url.trim()+"?";
			}
			for (String key : parm.keySet()) {
				String value = parm.get(key);
				url += key+"="+value+"&";
			}
			url = url.substring(0, url.length()-1);
		}
		OkHttpClient client = new OkHttpClient();
		try {
			Request request = new Builder().url(url).get().build();
			Call call = client.newCall(request);
			if(tag != null){
				addTag(tag,call);
			}
			Response response = call.execute();
			if(httpCallback == null)return;
			if(response.isSuccessful()){
				remove(tag);
				try {
					JSONObject jsonObject = new JSONObject(response.body().string());
					httpCallback.onResponse(true, (T) jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				remove(tag);
				httpCallback.onResponse(false, null);
			}
		} catch (IOException e) {
			remove(tag);
			if(httpCallback != null)
			httpCallback.onResponse(false, null);
		}
	}
	private static void addTag(Object key, Call value){
		synchronized (OkHttpClientUtils.class){
			map.put(key,value);
		}
	}
	public static void cancel(Object tag) {
		synchronized (OkHttpClientUtils.class){
			Call call = map.remove(tag);
			if(call != null){
				call.cancel();
			}
		}
	}
	private  static void remove(Object tag){
		synchronized (OkHttpClientUtils.class){
			if(tag != null)
				map.remove(tag);
		}
	}
	
	/**
	 * 添加数据
	 * @return Builder  add("key", "value")
	 */
	public static FormBody.Builder getFormBodyBuilder() {
		return new FormBody.Builder();
	}
}
