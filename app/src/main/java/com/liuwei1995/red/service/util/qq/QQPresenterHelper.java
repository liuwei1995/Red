package com.liuwei1995.red.service.util.qq;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;

/**
 * Created by liuwei on 2017/9/1 10:47
 */

public class QQPresenterHelper implements QQPresenter{

    public  QQPresenter mQQPresenter;

    private static QQPresenterHelper mQQPresenterHelper;


    public void setQQPresenter(QQPresenter mQQPresenter) {
        this.mQQPresenter = mQQPresenter;
    }

    public static QQPresenterHelper newInstance() {
        if (mQQPresenterHelper == null){
            synchronized (QQPresenterHelper.class){
                if (mQQPresenterHelper == null)
                    mQQPresenterHelper = new QQPresenterHelper();
            }
        }
        return mQQPresenterHelper;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (mQQPresenter != null)
            mQQPresenter.onAccessibilityEvent(event);
    }

    @Override
    public void onServiceConnected() {
        if (mQQPresenter != null)
            mQQPresenter.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        if (mQQPresenter != null)
            mQQPresenter.onDestroy();
    }

    @Override
    public void onInterrupt() {
        if (mQQPresenter != null)
            mQQPresenter.onInterrupt();
    }

    @Override
    public Context getApplicationContext() {
        if (mQQPresenter == null)return null;
        return mQQPresenter.getApplicationContext();
    }

    @Override
    public String getPackageName() {
        if (mQQPresenter == null)return null;
        return mQQPresenter.getPackageName();
    }
}
