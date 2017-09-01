package com.liuwei1995.red.service.util.qq.presenter;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by liuwei on 2017/4/20
 */

public interface QQPresenter {

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
       String QQ_PACKAGENAME = "com.tencent.mobileqq";




    /**
     * QQ主界面
     */
       String QQ_MAIN_INTERFACE = "com.tencent.mobileqq.activity.SplashActivity";

     static String OPEN = QQPresenter.class.getSimpleName()+".open";

     static String CLOSE = QQPresenter.class.getSimpleName()+".close";

     void onAccessibilityEvent(AccessibilityEvent event);


     void onServiceConnected();

     void onDestroy() ;

     void onInterrupt() ;

    Context getApplicationContext();

    String getPackageName();


}
