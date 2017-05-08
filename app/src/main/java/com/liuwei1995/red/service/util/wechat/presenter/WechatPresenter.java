package com.liuwei1995.red.service.util.wechat.presenter;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.wechat.iview.Wechat_6_5_7_IView;

/**
 * Created by liuwei on 2017/4/20
 */

public class WechatPresenter implements Wechat_6_5_7_IView {
    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    /**
     * 拆红包类
     */
    public static final String WECHAT_RECEIVER_CALSS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    /**
     * 红包详情类
     */
    public static final String WECHAT_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    /**
     * 微信主界面或者是聊天界面
     */
    public  static final String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
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
