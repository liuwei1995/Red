package com.liuwei1995.red.http;

/**
 * Created by liuwei on 2017/5/5
 */

public interface HttpCallback<T> {
    /**
     *
     * @param isSuccess  是否成功
     * @param result  isSuccess = false  body = null
     */
    void onResponse(Boolean isSuccess, T result);
}
