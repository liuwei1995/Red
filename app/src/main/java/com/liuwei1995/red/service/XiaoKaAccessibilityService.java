package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.xiaoka.XiaoKaPresenterHelper;
import com.liuwei1995.red.service.util.xiaoka.presenter.XiaoKaPresenter;
import com.liuwei1995.red.service.util.xiaoka.presenter.impl.XiaoKaPresenter41Impl;


public class XiaoKaAccessibilityService extends AccessibilityService {

    private static final String TAG = XiaoKaAccessibilityService.class.getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        XiaoKaPresenterHelper.newInstance().onAccessibilityEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        int versionCode = queryAppInfo(); //41
        XiaoKaPresenterHelper xiaoKaPresenterHelper = XiaoKaPresenterHelper.newInstance();
        if (versionCode == 41) {
            xiaoKaPresenterHelper.setXiaoKaPresenter(new XiaoKaPresenter41Impl(this));
        } else {
            xiaoKaPresenterHelper.setXiaoKaPresenter(new XiaoKaPresenter41Impl(this));
        }
        xiaoKaPresenterHelper.onServiceConnected();
    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public int queryAppInfo() {//versionName 1.6.2.1//versionCode 41
        PackageManager pm = getApplicationContext().getPackageManager(); // 获得PackageManager对象
        try {
            PackageInfo packageInfo = pm.getPackageInfo(XiaoKaPresenter.XIAOKA_PACKAGENAME, PackageManager.GET_META_DATA);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public void onDestroy() {
        XiaoKaPresenterHelper.newInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        XiaoKaPresenterHelper.newInstance().onInterrupt();
    }

}
