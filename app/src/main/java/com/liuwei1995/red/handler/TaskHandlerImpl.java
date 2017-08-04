package com.liuwei1995.red.handler;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 *  Handler 所有
 * Created by liuwei on 2017/6/30 14:11
 */
public class TaskHandlerImpl<T extends TaskHandler<T>> extends Handler {


    WeakReference<T> weakReference;

    public TaskHandlerImpl(T object) {
        weakReference = new WeakReference<>(object);
    }

    /**
     * 消息接受处理
     */
    @Override
    public void handleMessage(Message msg) {
        T object = weakReference.get();
        if (object != null) {
            object.handleMessage(weakReference,msg);
        }
    }
}