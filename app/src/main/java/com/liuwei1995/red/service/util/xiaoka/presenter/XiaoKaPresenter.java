package com.liuwei1995.red.service.util.xiaoka.presenter;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by liuwei on 2017/8/4 09:48
 */

public interface XiaoKaPresenter {

    /**
     * 一直播的包名
     */
    String XIAOKA_PACKAGENAME = "tv.xiaoka.live";


    String open = "MyAccessibilityService.open";
    String close = "MyAccessibilityService.close";

    /**修改发送文字广播*/
    String ACTION_RECEIVER_UPDATE = "ACTION_RECEIVER_UPDATE";

    /**修改发送文字广播key*/
    String ACTION_RECEIVER_UPDATE_KEY = "ACTION_RECEIVER_UPDATE_KEY";

    /**开始广播*/
    String ACTION_RECEIVER_SEND_START = "ACTION_RECEIVER_SEND_START";

    /**开始广播key*/
    String ACTION_RECEIVER_SEND_START_KEY = "ACTION_RECEIVER_SEND_START_KEY";

    /**暂停广播*/
    String ACTION_RECEIVER_SEND_PAUSE = "ACTION_RECEIVER_SEND_PAUSE";

    /**执行广播*/
    String ACTION_RECEIVER_EXECUTE = "ACTION_RECEIVER_EXECUTE";

    /**执行广播  key*/
    String ACTION_RECEIVER_EXECUTE_KEY = "ACTION_RECEIVER_EXECUTE_KEY";



    /**消息id*/
    int notificationId = 10;

    void onAccessibilityEvent(AccessibilityEvent event);


    void onServiceConnected();

    void onDestroy();

    void onInterrupt();

}
