package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.wechat.WechatPresenterHelper;
import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;
import com.liuwei1995.red.service.util.wechat.presenter.Wechat_6_5_7_Presenter;
import com.liuwei1995.red.service.util.wechat.presenter.Wechat_6_5_8_Presenter;


public class WeChatAccessibilityService extends AccessibilityService {

    private static final String TAG = WeChatAccessibilityService.class.getSimpleName();

    public static final String versionName_6_5_7 = "6.5.7";

    public static final String versionName_6_5_8 = "6.5.8";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        WechatPresenterHelper.newInstance().onAccessibilityEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        String s = queryAppInfo();
        if (!TextUtils.isEmpty(s)){
            WechatPresenterHelper wechatPresenterHelper = WechatPresenterHelper.newInstance();
            if (versionName_6_5_7.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_7_Presenter(this));
            }else if (versionName_6_5_8.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_8_Presenter(this));
            }else {
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_8_Presenter(this));
            }
            WechatPresenterHelper.newInstance().onServiceConnected();
        }
    }
    // 获得所有启动Activity的信息，类似于Launch界面
    public String queryAppInfo() {
        PackageManager pm = getApplicationContext().getPackageManager(); // 获得PackageManager对象
        try {
            PackageInfo packageInfo = pm.getPackageInfo(WechatPresenter.WECHAT_PACKAGENAME, PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onDestroy() {
        WechatPresenterHelper.newInstance().onDestroy();
        super.onDestroy();
    }
    @Override
    public void onInterrupt() {
        WechatPresenterHelper.newInstance().onInterrupt();
    }

}
