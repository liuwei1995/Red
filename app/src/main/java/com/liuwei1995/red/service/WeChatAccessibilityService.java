package com.liuwei1995.red.service;


import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.BaseApplication;
import com.liuwei1995.red.entity.AppEntity;
import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;
import com.liuwei1995.red.service.util.wechat.presenter.Wechat_6_5_7_Presenter;


public class WeChatAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(presenter != null)
        presenter.onAccessibilityEvent(event);
    }

    WechatPresenter presenter;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        String s = queryAppInfo();
        if(isStart && s != null && !TextUtils.isEmpty(s)){
//            if(presenter == null){
//                try {
//                    String[] split = s.split("\\.");
//                    String code = "_";
//                    for (int i = 0; i < split.length; i++) {
//                        code += split[i]+"_";
//                    }
//                    Class<?> aClass = Class.forName(getPackageName()+".service.util.wechat.presenter.Wechat" + code + "Presenter");
//                    Constructor<?> constructor = aClass.getConstructor(AccessibilityService.class);
//                    constructor.setAccessible(true);
//                    presenter = (WechatPresenter) constructor.newInstance(this);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
            presenter = new Wechat_6_5_7_Presenter(this);
            if(presenter != null)
                presenter.onServiceConnected();
        }
    }
    private boolean isStart = false;
    // 获得所有启动Activity的信息，类似于Launch界面
    public String queryAppInfo() {
        PackageManager pm = getApplicationContext().getPackageManager(); // 获得PackageManager对象
        try {
            PackageInfo packageInfo = pm.getPackageInfo(WechatPresenter.WECHAT_PACKAGENAME, PackageManager.GET_META_DATA);
            AppEntity appEntity = BaseApplication.WeChat_map.get(packageInfo.versionName);
            if(appEntity != null){
                isStart = packageInfo.versionCode == appEntity.getVersionCode();
            }
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onDestroy() {
        if(presenter != null)
        presenter.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onInterrupt() {
        if(presenter != null)
        presenter.onInterrupt();
    }

}
