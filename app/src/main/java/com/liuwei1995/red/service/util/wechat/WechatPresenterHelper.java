package com.liuwei1995.red.service.util.wechat;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;

/**
 * Created by liuwei on 2017/5/26 09:36
 */

public class WechatPresenterHelper implements WechatPresenter {


    public static  WechatPresenterHelper mWechatPresenterHelper;


    private WechatPresenter mWechatPresenter;

    public void setWechatIView(WechatPresenter mWechatPresenter) {
        this.mWechatPresenter = mWechatPresenter;
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
        if (mWechatPresenter != null)
            mWechatPresenter.onAccessibilityEvent(event);
    }

    @Override
    public void onServiceConnected() {
        if (mWechatPresenter != null)
            mWechatPresenter.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        if (mWechatPresenter != null)
            mWechatPresenter.onDestroy();
    }

    @Override
    public void onInterrupt() {
        if (mWechatPresenter != null)
            mWechatPresenter.onInterrupt();
    }
}
