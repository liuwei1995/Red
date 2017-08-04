package com.liuwei1995.red.service.util.xiaoka.iview;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by liuwei on 2017/8/4 09:51
 */

public interface IXiaoKaView {
     void onAccessibilityEvent(AccessibilityEvent event);


     void onServiceConnected();

     void onDestroy() ;

     void onInterrupt() ;
}
