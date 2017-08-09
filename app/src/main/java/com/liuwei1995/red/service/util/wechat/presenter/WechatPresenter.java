package com.liuwei1995.red.service.util.wechat.presenter;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by liuwei on 2017/4/20
 */

public interface WechatPresenter {

    void onAccessibilityEvent(AccessibilityEvent event);


    void onServiceConnected();

    void onDestroy();

    void onInterrupt();

    /**
     * 微信的包名
     */
    String WECHAT_PACKAGENAME = "com.tencent.mm";
    /**
     * 拆红包类
     */
    String WECHAT_RECEIVER_CALSS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    /**
     * 红包详情类
     */
    String WECHAT_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    /**
     * 微信主界面或者是聊天界面
     */
    String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    String open = "MyAccessibilityService.open";
    String close = "MyAccessibilityService.close";
}
