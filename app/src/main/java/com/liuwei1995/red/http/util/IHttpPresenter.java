package com.liuwei1995.red.http.util;

import java.util.Map;

/**
 * Created by liuwei on 2017/5/25 09:10
 */

public interface IHttpPresenter {

    void get(String url);
    void get(String url,ICallback callback);
    void get(String url,final Object tag,ICallback callback);
    void get(String url,Map<String, String> params,ICallback callback);
    void get(String url,Map<String, String> params,final Object tag,ICallback callback);

    void post(String url);
    void post(String url,ICallback callback);
    void post(String url,final Object tag,ICallback callback);
    void post(String url,Map<String, Object> params,ICallback callback);
    void post(String url,Map<String, Object> params,final Object tag,ICallback callback);
    void cancel(Object tag);

}
