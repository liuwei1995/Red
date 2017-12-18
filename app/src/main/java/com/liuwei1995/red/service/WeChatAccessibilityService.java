package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.wechat.WechatPresenterHelper;
import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_10_PresenterImpl;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_13_PresenterImpl;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_16_PresenterImpl;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_23_PresenterImpl;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_7_PresenterImpl;
import com.liuwei1995.red.service.util.wechat.presenter.impl.Wechat_6_5_8_PresenterImpl;


public class WeChatAccessibilityService extends AccessibilityService {

    private static final String TAG = WeChatAccessibilityService.class.getSimpleName();

    public static final String versionName_6_5_7 = "6.5.7";

    public static final String versionName_6_5_8 = "6.5.8";

    public static final String versionName_6_5_10 = "6.5.10";

    public static final String versionName_6_5_13 = "6.5.13";

    public static final String versionName_6_5_16 = "6.5.16";

    public static final String versionName_6_5_23 = "6.5.23";

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
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_7_PresenterImpl(this));
            }
            else if (versionName_6_5_8.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_8_PresenterImpl(this));
            }
            else if (versionName_6_5_10.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_10_PresenterImpl(this));
            }
            else if (versionName_6_5_13.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_13_PresenterImpl(this));
            }
            else if (versionName_6_5_16.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_16_PresenterImpl(this));
            }
            else if (versionName_6_5_23.equals(s)){
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_23_PresenterImpl(this));
            }
            else {
                wechatPresenterHelper.setWechatIView(new Wechat_6_5_23_PresenterImpl(this));
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
