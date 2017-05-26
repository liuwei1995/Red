package com.liuwei1995.red.service.util.wechat;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.wechat.iview.WechatIView;

/**
 * Created by liuwei on 2017/5/26 09:36
 */

public class WechatPresenterHelper implements WechatIView {

    public WechatIView wechatIView = null;

    public static  WechatPresenterHelper mWechatPresenterHelper;

    public WechatIView getWechatIView() {
        return wechatIView;
    }

    public void setWechatIView(WechatIView wechatIView) {
        this.wechatIView = wechatIView;
    }

    public static WechatPresenterHelper newInstance() {
        if (mWechatPresenterHelper == null){
            synchronized (WechatPresenterHelper.class){
                if (mWechatPresenterHelper == null)
                    mWechatPresenterHelper = new WechatPresenterHelper();
            }
        }
        return mWechatPresenterHelper;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (wechatIView != null)
        wechatIView.onAccessibilityEvent(event);
    }

    @Override
    public void onServiceConnected() {
        if (wechatIView != null)
        wechatIView.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        if (wechatIView != null)
        wechatIView.onDestroy();
    }

    @Override
    public void onInterrupt() {
        if (wechatIView != null)
        wechatIView.onInterrupt();
    }
}
