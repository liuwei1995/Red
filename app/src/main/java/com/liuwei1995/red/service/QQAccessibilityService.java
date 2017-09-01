package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.qq.QQPresenterHelper;
import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;
import com.liuwei1995.red.service.util.qq.presenter.impl.QQ_6_7_1_PresenterImpl;
import com.liuwei1995.red.service.util.qq.presenter.impl.QQ_7_0_0_PresenterImpl;


public class QQAccessibilityService extends AccessibilityService {

    private static final String TAG = QQAccessibilityService.class.getSimpleName();

    public static final String versionName_6_7_1 = "6.7.1";
    public static final String versionName_7_0_0 = "7.0.0";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        QQPresenterHelper.newInstance().onAccessibilityEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        String s = queryAppInfo();
        if (!TextUtils.isEmpty(s)){
            QQPresenterHelper qqPresenterHelper = QQPresenterHelper.newInstance();
            if (versionName_6_7_1.equals(s)){
                qqPresenterHelper.setQQPresenter(new QQ_6_7_1_PresenterImpl(this));
            }else if (versionName_7_0_0.equals(s)){
                qqPresenterHelper.setQQPresenter(new QQ_7_0_0_PresenterImpl(this));
            }else {
                qqPresenterHelper.setQQPresenter(new QQ_7_0_0_PresenterImpl(this));
            }
            qqPresenterHelper.onServiceConnected();
        }
    }
    @Override
    public void onDestroy() {
        QQPresenterHelper.newInstance().onDestroy();
        super.onDestroy();
    }
    @Override
    public void onInterrupt() {
        QQPresenterHelper.newInstance().onInterrupt();
    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public String queryAppInfo() {
        PackageManager pm = getApplicationContext().getPackageManager(); // 获得PackageManager对象
        try {
            PackageInfo packageInfo = pm.getPackageInfo(QQPresenter.QQ_PACKAGENAME, PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
