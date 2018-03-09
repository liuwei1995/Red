package com.liuwei1995.red.service.util.wechat.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by liuwei on 2017/4/20
 */
public class Wechat_6_6_3_PresenterImpl extends WechatPresenterImpl {

    private static final String TAG = Wechat_6_6_3_PresenterImpl.class.getSimpleName();

    /**关闭按钮id*/
    private static final String Close_button = "com.tencent.mm:id/bh8";

    /**红包弹窗关闭按钮*/
    private static final String Close_button_red_popupwindow = "com.tencent.mm:id/c28";

    /**
     * 红包控件id
     */
    private static final String ID_OPEN_RED_ENVELOPE_CONTROL = "com.tencent.mm:id/c4j";

    /***红包详情返回按钮*/
    private static final String return_LinearLayout_red_details = "com.tencent.mm:id/hx";


    public Wechat_6_6_3_PresenterImpl(AccessibilityService accessibilityService) {
        super(accessibilityService);
    }

    private boolean mNeedUnpack;
    private boolean mNeedBack = false;
    /**
     * 红包详情类  是否需要返回
     */
    private boolean mNeedDetaBack = false;

    private List<AccessibilityNodeInfo> mUnpackNode = null;

    private boolean IS_VIEW_SCROLLED = false;
    private boolean IS_RED_CLICK = false;

    private boolean isChat = false;

    private static final int ONE = 1;

    private static final int TWO = 2;

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen){
            return;
        }
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo source_type_window_state_changed = event.getSource();
                if(source_type_window_state_changed != null){
                    CharSequence contentDescription = source_type_window_state_changed.getContentDescription();
                    if(contentDescription != null){
                        isChat = false;
                        if(contentDescription.toString().contains("的聊天")){
                            isChat = true;
                            IS_RED_CLICK = false;
                        }else if(contentDescription.toString().contains("红包详情")){
                            if(IS_RED_CLICK || mNeedDetaBack){
                                mHander.removeMessages(ONE);
                                mHander.sendEmptyMessageAtTime(ONE,500);
                            }else {
                                mHander.removeMessages(ONE);
                            }
                        }
                    }
                }
                if(isChat)
                    perform();
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (!isChat){
                    break;
                }
                AccessibilityNodeInfo source = event.getSource();
                if(source != null){
                    if(source.getClassName().toString().equals("android.widget.ListView")){
                        if(IS_RED_CLICK)break;
                        int childCount = source.getChildCount();
                        if(childCount > 0){
                            AccessibilityNodeInfo child = source.getChild(childCount - 1);
                            if(child != null)
                                if(child.getClassName().toString().equals("android.widget.RelativeLayout")){
                                    List<AccessibilityNodeInfo> getRedList = child.findAccessibilityNodeInfosByText("领取红包");
                                    if (getRedList != null && !getRedList.isEmpty()){
                                        synchronized (this){
                                            if(!IS_RED_CLICK && !mNeedBack && IS_VIEW_SCROLLED){
                                                synchronized (this){
                                                    AccessibilityNodeInfo accessibilityNodeInfo = getRedList.get(getRedList.size() - 1);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                        if(accessibilityNodeInfo.isClickable()){
                                                            IS_VIEW_SCROLLED = false;
                                                            IS_RED_CLICK = true;
                                                            accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                        }else {
                                                            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                                                            while (parent != null) {
                                                                if (parent.isClickable()) {
                                                                    IS_VIEW_SCROLLED = false;
                                                                    IS_RED_CLICK = true;
                                                                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                    break;
                                                                }
                                                                parent = parent.getParent();
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                            }else{
                                                if(IS_VIEW_SCROLLED && IS_RED_CLICK && !mNeedBack && !mNeedDetaBack)
                                                    IS_RED_CLICK = false;
                                            }
                                        }
                                    }else{
                                        if(IS_RED_CLICK && !mNeedBack && !mNeedDetaBack)
                                            IS_RED_CLICK = false;
                                    }
                                }else{
                                    if(IS_RED_CLICK && !mNeedBack && !mNeedDetaBack)
                                        IS_RED_CLICK = false;
                                }
                        }
                    }
                }
                break;

            case AccessibilityEvent.TYPE_VIEW_SCROLLED://类型视图滚动
                if(isChat)
                    synchronized (this){
                        if(!IS_RED_CLICK){
                            IS_VIEW_SCROLLED = true;
                        }
                        else
                        {
                            Log.e(TAG, "onAccessibilityEvent: IS_RED_CLICK:"+true +"\t类型视图滚动\tIS_VIEW_SCROLLED:"+IS_VIEW_SCROLLED);
                        }
                    }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotification(event);
                break;
            default:
                break;
        }
    }

    /**
     * 红包详情界面返回
     */
    private void redDetailsBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if(rootInActiveWindow != null){
                List<AccessibilityNodeInfo> gv_LinearLayout = rootInActiveWindow.findAccessibilityNodeInfosByViewId(return_LinearLayout_red_details);
                if(gv_LinearLayout != null && !gv_LinearLayout.isEmpty()){
                    for (int i = 0; i < gv_LinearLayout.size(); i++) {
                        AccessibilityNodeInfo accessibilityNodeInfo = gv_LinearLayout.get(i);
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
            mNeedDetaBack = false;
        }
    }

    @Override
    public void handleMessage(WeakReference<WechatPresenterImpl> weakReference, Message msg) {
        super.handleMessage(weakReference, msg);
        if (msg.what == ONE){
            redDetailsBack();
        }else if (msg.what == TWO){
            boolean b = false;
            Log.e(TAG, "perform:前 " + "IS_RED_CLICK:" + IS_RED_CLICK + "\tmNeedBack:" + true + "\tIS_VIEW_SCROLLED:" + IS_VIEW_SCROLLED + "\tperformAction:" + false);
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(Close_button);
                        if (!accessibilityNodeInfosByViewId.isEmpty()) {
                        for (int i = 0; i < accessibilityNodeInfosByViewId.size(); i++) {
                            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(i);
                            b = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
            Log.e(TAG, "perform:后 " + "IS_RED_CLICK:" + IS_RED_CLICK + "\tmNeedBack:" + false + "\tIS_VIEW_SCROLLED:" + IS_VIEW_SCROLLED + "\tperformAction:" + b);
        }
        mNeedBack = false;
    }


    private synchronized void perform() {
        mUnpackNode = null;
        retrunJieMian();
        if (mNeedUnpack && (mUnpackNode != null)) {
            int size = mUnpackNode.size();
            if (size > 0) {
                AccessibilityNodeInfo cellNode = mUnpackNode.get(size - 1);
                boolean b = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    b = cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                Log.e(TAG, "onAccessibilityEvent: cellNode.performAction==" + b);
                if (b) {
                    mNeedDetaBack = true;
                }
                mNeedUnpack = false;
            }
        }
        if (mNeedBack) {
            mHander.removeMessages(TWO);
            mHander.sendEmptyMessageAtTime(TWO,500);
        }
    }

    private void retrunJieMian() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            List<AccessibilityNodeInfo> id_open_red_envelope_control = nodeInfo.findAccessibilityNodeInfosByViewId(ID_OPEN_RED_ENVELOPE_CONTROL);
            if (id_open_red_envelope_control != null && id_open_red_envelope_control.size() > 0){
                AccessibilityNodeInfo accessibilityNodeInfo = id_open_red_envelope_control.get(id_open_red_envelope_control.size() - 1);
                if (accessibilityNodeInfo.isClickable()){
                    IS_VIEW_SCROLLED = false;
                    IS_RED_CLICK = true;
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else {
                    AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                    while (parent != null){
                        if (parent.isClickable()){
                            IS_VIEW_SCROLLED = false;
                            IS_RED_CLICK = true;
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
                return;
            }
        }
        if (IS_RED_CLICK){
            List<AccessibilityNodeInfo> node2 = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                node2 = nodeInfo.findAccessibilityNodeInfosByViewId(Close_button_red_popupwindow);
            }
            if (node2 != null && !node2.isEmpty()) {
                mUnpackNode = node2;
                mNeedUnpack = true;
            }
        }
    }

}
