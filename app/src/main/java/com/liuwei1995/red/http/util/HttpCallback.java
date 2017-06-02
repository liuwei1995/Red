package com.liuwei1995.red.http.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Created by liuwei on 2017/5/25 09:00
 * <li>这个需要根据自己的业务需求来写{@link HttpCallback#onCallbackResult(Boolean, String)}</li>
 *
 * @param <Result>
 */
public abstract class HttpCallback<Result> implements ICallback {

    @Override
    public void onCallbackResult(Boolean isSuccess, String s) {
        Result result = null;
        if (isSuccess) {
            try {
                Class<?> clz = analysisClazzInfo(this);
                if (clz == JSONObject.class) {
                    onCallbackResult(true, (Result) new JSONObject(s));
                } else if (clz == JSONArray.class) {
                    onCallbackResult(true, (Result) new JSONArray(s));
                } else {
                    onCallbackResult(false, result);
                }
            } catch (JSONException e) {
                try {
                    onCallbackResult(false, (Result) new JSONObject(s));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            onCallbackResult(isSuccess, result);
        }
    }

    public abstract void onCallbackResult(Boolean isSuccess, Result result);

    private static Class<?> analysisClazzInfo(Object object) {
        Type genericSuperclass = object.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return null;
    }
}
