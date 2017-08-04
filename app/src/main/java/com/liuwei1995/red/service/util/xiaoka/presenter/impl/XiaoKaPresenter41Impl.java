package com.liuwei1995.red.service.util.xiaoka.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;


/**
 * Created by liuwei on 2017/8/4 10:22
 */

public class XiaoKaPresenter41Impl extends XiaoKaPresenterImpl {

    private static final String TAG = "XiaoKaPresenter41Impl";

    private AccessibilityService mAccessibilityService;

    public XiaoKaPresenter41Impl(AccessibilityService mAccessibilityService) {
        super(mAccessibilityService);
    }

public void g(){
    AccessibilityNodeInfo info = getRootInActiveWindow();
    if (info != null) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "控件名称:" + info.getClassName());
            Log.i(TAG, "控件中的值：" + info.getText());
            Log.i(TAG, "控件的ID:" + info.getViewIdResourceName());
            Log.i(TAG, "点击是否出现弹窗:" + info.canOpenPopup());
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {

                }
            }
        }
    }
}
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen || TextUtils.isEmpty(txt))return;
        AccessibilityNodeInfo source = event.getSource();
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (source != null){
                    List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = source.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/edit_chat");
                    if (accessibilityNodeInfosByViewId != null && accessibilityNodeInfosByViewId.size() > 0){
                        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(0);
                       if (!TextUtils.isEmpty(txt))
                        findEditText(accessibilityNodeInfo,txt);
                    }
                }
                break;

            case AccessibilityEvent.TYPE_VIEW_SCROLLED://类型视图滚动
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                handleNotification(event);
                break;
            default:
                break;
        }
    }


    private boolean findEditText(AccessibilityNodeInfo nodeInfo, String content) {
        try {
            Bundle arguments = new Bundle();
            arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                    AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
            arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                    true);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                    arguments);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            ClipData clip = ClipData.newPlainText("label", content);
            ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
    }
    private String txt = null;

    @Override
    protected void start(String txt) {
        this.txt = txt;
    }

    @Override
    protected void pause() {
        txt = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {

    }
}
