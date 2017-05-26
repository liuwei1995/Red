package com.liuwei1995.red.service.util.ofo.presenter;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.ofo.iview.OFOIView;

/**
 * Created by liuwei on 2017/4/20
 */

public class OFOPresenter implements OFOIView {

    /**
     * OFO的包名
     */
    public static final String OFO_PACKAGENAME = "so.ofo.labofo";


    /**
     * 聊天界面listview 包名
     */
    public static final String QQ_CHAT_INTERFACE_AbsListView = "android.widget.AbsListView";


    /**
     * 聊天界面listview id
     */
    public static final String QQ_CHAT_INTERFACE_LISTVIEW_ID = "com.tencent.mobileqq:id/listView1";

    /**
     * QQ主界面
     */
    public static final String QQ_MAIN_INTERFACE = "com.tencent.mobileqq.activity.SplashActivity";

    public static String open = "MyAccessibilityService.open";
    public static String close = "MyAccessibilityService.close";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onInterrupt() {

    }
}
