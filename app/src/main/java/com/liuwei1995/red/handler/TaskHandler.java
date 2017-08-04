package com.liuwei1995.red.handler;

import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by liuwei on 2017/6/30 14:20
 */

public interface TaskHandler<T> {

      void handleMessage(WeakReference<T> weakReference, Message msg);

}
