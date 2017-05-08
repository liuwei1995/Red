package com.liuwei1995.red.service.util.wechat.iview;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by dell on 2017/4/20
 */

public interface WechatIView {
    public void onAccessibilityEvent(AccessibilityEvent event);


    public void onServiceConnected();

    public void onDestroy() ;

    public void onInterrupt() ;
}
