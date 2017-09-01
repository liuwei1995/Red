package com.liuwei1995.red.service.util.qq.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by liuwei on 2017/9/1 11:20
 */

public class QQ_7_0_0_PresenterImpl extends QQPresenterImpl {

    private static final String TAG = "QQ_6_7_1_PresenterImpl";

    public QQ_7_0_0_PresenterImpl(AccessibilityService accessibilityService) {
        super(accessibilityService);
    }

    /**
     * 聊天界面listview id
     */
    String QQ_CHAT_INTERFACE_LISTVIEW_ID = "com.tencent.mobileqq:id/listView1";

    /**
     * 聊天界面listview 包名
     */
    String QQ_CHAT_INTERFACE_AbsListView = "android.widget.AbsListView";


    private boolean is_QQ_MAIN_INTERFACE = false;

    private boolean isClick = false;
    /**
     * 是否需要返回
     */
    private boolean mNeedClickReturn = false;

    /**详情返回 */
    private static final int DETAILS_TO_RETURN = 1;
    /**点开红包*/
    private static final int OPENS_RED = 2;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen){
            return;
        }
        switch (event.getEventType()){//com.tencent.mobileqq.activity.PayBridgeActivity
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String s = event.getClassName().toString();
                if(s.equals(QQPresenter.QQ_MAIN_INTERFACE)){
                    if(!isClick && !mNeedClickReturn) {
                        Log.e(TAG, "onAccessibilityEvent: 进入判断是否需要点击");
                        judgeChatInterface();
                    }else{
                        Log.e(TAG, "onAccessibilityEvent: 回来已经点击过了");
                    }
                    isClick = false;
                    is_QQ_MAIN_INTERFACE = true;
                }else if (event.getClassName().toString().equals("cooperation.qwallet.plugin.QWalletPluginProxyActivity")){
                    is_QQ_MAIN_INTERFACE = false;
                    if (isClick) {
                        sendEmptyMessageDelayed(OPENS_RED);
                    }
                }else {
                    is_QQ_MAIN_INTERFACE = false;
                    Log.e(TAG, "onAccessibilityEvent: 类型窗口状态改变"+s);
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if(is_QQ_MAIN_INTERFACE){
                    if(event.getClassName().toString().equals("android.widget.AbsListView")){
                        synchronized (this){
                            if(!isClick){
                                judgeChatInterface();
                            }else {
                                Log.e(TAG, "onAccessibilityEvent: 点开中-----" + event.getClassName().toString());
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.e(TAG, "onAccessibilityEvent: 手势检测结束");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                Log.e(TAG, "onAccessibilityEvent: 开始触摸交互类型");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e(TAG, "onAccessibilityEvent: TYPE_VIEW_SCROLLED  类型视图滚动");
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e(TAG, "onAccessibilityEvent: TYPE_NOTIFICATION_STATE_CHANGED 通知状态改变");
                handleNotification(event);
                break;

            default:
                break;
        }
    }
    private synchronized void sendEmptyMessageDelayed(int what){
        mHander.removeMessages(what);
        mHander.sendEmptyMessageDelayed(what,500);
    }

    /**
     * 判断聊天界面  需要返回
     */
    public boolean judgeChatInterface(){
        long start = System.currentTimeMillis();
        AccessibilityNodeInfo rootInActiveWindow = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootInActiveWindow = getRootInActiveWindow();
        }
        if (rootInActiveWindow == null)return false;
        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(QQ_CHAT_INTERFACE_LISTVIEW_ID);
        }
        if(accessibilityNodeInfosByViewId != null && !accessibilityNodeInfosByViewId.isEmpty()){
            AccessibilityNodeInfo AbsListViewAccessibilityNodeInfo = accessibilityNodeInfosByViewId.get(accessibilityNodeInfosByViewId.size() - 1);
            if(AbsListViewAccessibilityNodeInfo.getClassName().toString().equals(QQ_CHAT_INTERFACE_AbsListView)){
                int childCount = AbsListViewAccessibilityNodeInfo.getChildCount();
                if(childCount >= 1){
                    AccessibilityNodeInfo child = AbsListViewAccessibilityNodeInfo.getChild(childCount - 1);
                    List<AccessibilityNodeInfo> qqRedPa1cketPersonalityVersionList = child.findAccessibilityNodeInfosByText("QQ红包个性版");
                    if(!qqRedPa1cketPersonalityVersionList.isEmpty()){
                        AccessibilityNodeInfo qqRedPacketPersonalityVersion = qqRedPa1cketPersonalityVersionList.get(qqRedPa1cketPersonalityVersionList.size() - 1);//QQ红包
                        AccessibilityNodeInfo parentPersonalityVersion = qqRedPacketPersonalityVersion.getParent();
                        if(parentPersonalityVersion != null){
                            if(parentPersonalityVersion.isClickable()){
                                isClick = true;
                                mNeedClickReturn = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    parentPersonalityVersion.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                                Log.e(TAG, "judgeChatInterface:  点击了qq红包个性版 "+ parentPersonalityVersion.toString());
                            }else {
                                isClick = false;
                                Log.e(TAG, "judgeChatInterface: 不能点击"+ parentPersonalityVersion.toString());
                            }
                        }
                    }else{
                        List<AccessibilityNodeInfo> qqRedPacketList = child.findAccessibilityNodeInfosByText("QQ红包");
                        if(!qqRedPacketList.isEmpty()) {
                            AccessibilityNodeInfo qqRedPacket = qqRedPacketList.get(qqRedPacketList.size() - 1);//QQ红包
                            AccessibilityNodeInfo parent = qqRedPacket.getParent();
                            if(parent != null){
                                List<AccessibilityNodeInfo> clickOpen = parent.findAccessibilityNodeInfosByText("点击拆开");
                                if(!clickOpen.isEmpty()){
                                    if(parent.isClickable()){
                                        isClick = true;
                                        mNeedClickReturn = true;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        }
                                        Log.e(TAG, "judgeChatInterface:  点击了 "+ parent.toString());
                                    }else {
                                        isClick = false;
                                        Log.e(TAG, "judgeChatInterface: 不能点击"+ parent.toString());
                                    }
                                }else {
                                    if(!parent.findAccessibilityNodeInfosByText("已拆开").isEmpty() || !parent.findAccessibilityNodeInfosByText("口令红包已拆开").isEmpty()){
                                        if(parent.isClickable()){
                                            isClick = true;
                                            mNeedClickReturn = true;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                            }
//                                        sendEmptyMessageDelayed(DETAILS_TO_RETURN);
                                            Log.e(TAG, "judgeChatInterface:  已经点击过了 "+ parent.toString());
                                        }else {
                                            isClick = false;
                                            Log.e(TAG, "judgeChatInterface: "+ parent.toString());
                                        }
                                    }else if(!parent.findAccessibilityNodeInfosByText("口令红包").isEmpty()){
                                        if(parent.getChildCount() > 0){
                                            AccessibilityNodeInfo parent_child = parent.getChild(0);
                                            CharSequence text = parent_child.getText();
                                            if(text != null){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    List<AccessibilityNodeInfo> input_list = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/input");
                                                    if(!input_list.isEmpty()){
                                                        AccessibilityNodeInfo input = input_list.get(0);
                                                        Bundle arguments = new Bundle();
                                                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text.toString());
                                                        boolean b = input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                                                        if(b){
                                                            List<AccessibilityNodeInfo> fun_btn = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/fun_btn");
                                                            if(!fun_btn.isEmpty()){
                                                                AccessibilityNodeInfo accessibilityNodeInfo = fun_btn.get(fun_btn.size() - 1);
                                                                if(accessibilityNodeInfo.isClickable()){
                                                                    isClick = true;
                                                                    mNeedClickReturn = true;
                                                                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                }else {
                                                                    if(accessibilityNodeInfo.getParent() != null && accessibilityNodeInfo.getParent().isClickable()){
                                                                        isClick = true;
                                                                        mNeedClickReturn = true;
                                                                        accessibilityNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                Log.e(TAG, "onAccessibilityEvent: true  不是红包 time:"+(System.currentTimeMillis() - start));
            }
            return true;
        }else {
            Log.e(TAG, "onAccessibilityEvent: false time:"+(System.currentTimeMillis() - start));
            return false;
        }
    }


    @Override
    public void handleMessage(WeakReference<QQPresenterImpl> weakReference, Message msg) {
        super.handleMessage(weakReference, msg);
        if (msg.what == DETAILS_TO_RETURN){//详情返回
            if(mNeedClickReturn){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    if(rootInActiveWindow != null){
                        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/ivTitleBtnLeft");{
                            if(accessibilityNodeInfosByViewId != null)
                                for (int i = 0; i < accessibilityNodeInfosByViewId.size(); i++) {
                                    AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(i);
                                    if(accessibilityNodeInfo.isClickable()){
                                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                    }else {
                                        AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                                        if(parent != null && parent.isClickable()){
                                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        }
                                    }
                                }
                        }
                    }
                }
                mNeedClickReturn = false;
            }
        }else if (msg.what == OPENS_RED){
            if(!is_QQ_MAIN_INTERFACE){
                if(mNeedClickReturn){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                        if(rootInActiveWindow != null){
                            List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(QQ_CHAT_INTERFACE_LISTVIEW_ID);
                            if(accessibilityNodeInfosByViewId.isEmpty()){
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            }
                        }else {
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                    }
                    mNeedClickReturn = false;
                }
            }
        }
    }
}
