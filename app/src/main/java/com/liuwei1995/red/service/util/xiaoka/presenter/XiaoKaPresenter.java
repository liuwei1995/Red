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

    String ACTION_RECEIVER_SEND_START = "ACTION_RECEIVER_SEND_START";

    String ACTION_RECEIVER_SEND_PAUSE = "ACTION_RECEIVER_SEND_PAUSE";

    String ACTION_RECEIVER_SEND_START_KEY = "ACTION_RECEIVER_SEND_START_KEY";



    /**消息id*/
    int notificationId = 10;

    void onAccessibilityEvent(AccessibilityEvent event);


    void onServiceConnected();

    void onDestroy();

    void onInterrupt();

}
