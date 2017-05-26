package com.liuwei1995.red.service.util.ofo;

import android.view.accessibility.AccessibilityEvent;

import com.liuwei1995.red.service.util.ofo.iview.OFOIView;


/**
 * Created by liuwei on 2017/5/26 09:36
 */

public class OFOPresenterHelper implements OFOIView {

    public OFOIView OFOIView = null;

    public static OFOPresenterHelper mWechatPresenterHelper;

    public OFOIView getOFOIView() {
        return OFOIView;
    }

    public void setOFOIView(OFOIView ofoIView) {
        this.OFOIView = ofoIView;
    }

    public static OFOPresenterHelper newInstance() {
        if (mWechatPresenterHelper == null){
            synchronized (OFOPresenterHelper.class){
                if (mWechatPresenterHelper == null)
                    mWechatPresenterHelper = new OFOPresenterHelper();
            }
        }
        return mWechatPresenterHelper;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (OFOIView != null)
        OFOIView.onAccessibilityEvent(event);
    }

    @Override
    public void onServiceConnected() {
        if (OFOIView != null)
        OFOIView.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        if (OFOIView != null)
        OFOIView.onDestroy();
    }

    @Override
    public void onInterrupt() {
        if (OFOIView != null)
        OFOIView.onInterrupt();
    }
}
