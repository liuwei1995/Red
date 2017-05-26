package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.ofo.OFOPresenterHelper;
import com.liuwei1995.red.service.util.ofo.presenter.OFOOtherPresenter;
import com.liuwei1995.red.service.util.ofo.presenter.OFOPresenter;
import com.liuwei1995.red.service.util.ofo.presenter.OFO_2_0_0_build_12922_Presenter;


public class OFOAccessibilityService extends AccessibilityService {

    private static final String TAG = OFOAccessibilityService.class.getSimpleName();

    public static final String versionName_2_0_0_build_12922 = "2.0.0 (build 12922)";
    public static final String versionName_2_0_1_build_13006 = "2.0.1 (build 13006)";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        OFOPresenterHelper.newInstance().onAccessibilityEvent(event);
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        String s = queryAppInfo();
        if (!TextUtils.isEmpty(s)){
            if (versionName_2_0_0_build_12922.equals(s) || versionName_2_0_1_build_13006.equals(s)){
                OFOPresenterHelper.newInstance().setOFOIView(new OFO_2_0_0_build_12922_Presenter(this));
            }else {
                OFOPresenterHelper.newInstance().setOFOIView(new OFOOtherPresenter(this));
            }
            OFOPresenterHelper.newInstance().onServiceConnected();
        }
    }
    // 获得所有启动Activity的信息，类似于Launch界面
    public String queryAppInfo() {
        PackageManager pm = getApplicationContext().getPackageManager(); // 获得PackageManager对象
        try {
            PackageInfo packageInfo = pm.getPackageInfo(OFOPresenter.OFO_PACKAGENAME, PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        OFOPresenterHelper.newInstance().onDestroy();
    }
    @Override
    public void onInterrupt() {
        OFOPresenterHelper.newInstance().onInterrupt();
    }
}
