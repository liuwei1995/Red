package com.liuwei1995.red.service.util.qq.presenter;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.qq.iview.QQ_6_5_7_IView;

/**
 * Created by liuwei on 2017/4/20
 */

public class QQPresenter implements QQ_6_5_7_IView {

    /**
     * android.widget.RelativeLayout com.tencent.mobileqq:id/name
     * android.widget.AbsListView com.tencent.mobileqq:id/recent_chat_list
     */
    /**
     * 聊天界面的左边的消息id   android.widget.RelativeLayout com.tencent.mobileqq:id/name
     * android.widget.AbsListView com.tencent.mobileqq:id/listView1
     */

    /***
     * qq红包详情
     * android.widget.TextView com.tencent.mobileqq:id/ivTitleBtnLeft
     */

    /**
     *红包图片和文字提示 android.widget.RelativeLayout com.tencent.mobileqq:id/chat_item_content_layout
     *  child:
     *  android.widget.RelativeLayout com.tencent.mobileqq:id/name
     *   android.widget.TextView com.tencent.mobileqq:id/name
     */
    public static final String QQ_RED_TEXT_AND_IMAGES_ID = "com.tencent.mobileqq:id/chat_item_content_layout";
    /**
     * QQ的包名
     */
    public static final String QQ_PACKAGENAME = "com.tencent.mobileqq";

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
