package com.liuwei1995.red.service.util.xiaoka;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.xiaoka.presenter.XiaoKaPresenter;

/**
 * 一直播
 * Created by liuwei on 2017/8/4 09:49
 */

public class XiaoKaPresenterHelper implements XiaoKaPresenter{


    /***
     * com.yixia.live.activity.IndexActivity
     *
     * tv.xiaoka.play.activity.VideoPlayActivity
     *
     *
     */
    private static XiaoKaPresenterHelper mXiaoKaPresenterHelper;

    public static XiaoKaPresenterHelper newInstance() {
        if (mXiaoKaPresenterHelper == null){
            synchronized (XiaoKaPresenterHelper.class){
                if (mXiaoKaPresenterHelper == null)
                    mXiaoKaPresenterHelper = new XiaoKaPresenterHelper();
            }
        }
        return mXiaoKaPresenterHelper;
    }

    private XiaoKaPresenter mXiaoKaPresenter;

    public void setXiaoKaPresenter(XiaoKaPresenter mXiaoKaPresenter) {
        this.mXiaoKaPresenter = mXiaoKaPresenter;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(mXiaoKaPresenter != null)mXiaoKaPresenter.onAccessibilityEvent(event);
    }

    @Override
    public void onServiceConnected() {
        if(mXiaoKaPresenter != null)mXiaoKaPresenter.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        if(mXiaoKaPresenter != null)mXiaoKaPresenter.onDestroy();
    }

    @Override
    public void onInterrupt() {
        if(mXiaoKaPresenter != null)mXiaoKaPresenter.onInterrupt();
    }

}
