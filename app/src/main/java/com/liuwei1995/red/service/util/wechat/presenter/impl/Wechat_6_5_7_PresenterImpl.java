package com.liuwei1995.red.service.util.wechat.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.liuwei1995.red.R;
import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by liuwei on 2017/4/20
 */

public class Wechat_6_5_7_PresenterImpl extends WechatPresenterImpl  {

    private static final String TAG = "Wechat_6_5_7_Presenter";

    private AccessibilityService accessibilityService;


    private boolean mNeedUnpack;
    private boolean mNeedBack = false;
    /**
     * 红包详情类  是否需要返回
     */
    private boolean mNeedDetaBack = false;

    private List<AccessibilityNodeInfo> mUnpackNode = null;
    /**
     * 红包控件id
     */
    private static final String ID_RED_ENVELOPE_CONTROL = "com.tencent.mm:id/a5c";
    private boolean IS_VIEW_SCROLLED = false;
    private boolean IS_RED_CLICK = false;

    private RedReceiver redReceiver;
    private boolean isWECHAT_DETAIL = false;
    private boolean isChat = false;
    private boolean isNotification = false;

    private boolean mLuckyMoneyPicked;

//    private Map<String, Integer> data = new HashMap<>();
    private NotificationManager notificationManager;
    private Notification notification;
    private int id = 0;

    public Wechat_6_5_7_PresenterImpl(AccessibilityService accessibilityService) {
        super(accessibilityService);
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        /**
         * 首页listview com.tencent.mm:id/bld
         *
         */
        /**
         * com.tencent.mm:id/a5c android.widget.LinearLayout
         * com.tencent.mm:id/a6a android.widget.TextView 领取红包
         */
        /**
         * android.widget.FrameLayout com.tencent.mm:id/a2h
         * 聊天界面android.widget.ListView com.tencent.mm:id/a2i
         */
        /**
         * android.widget.RelativeLayout com.tencent.mm:id/bh4
         * 给你发了一个红包 com.tencent.mm:id/bh6  android.widget.TextView
         */
        /**
         * 红包详情
         * 恭喜发财，大吉大利 android.widget.TextView com.tencent.mm:id/bfu
         */
        if(!isOpen){
            return;
        }
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo source_type_window_state_changed = event.getSource();
                if(source_type_window_state_changed != null){
                    CharSequence contentDescription = source_type_window_state_changed.getContentDescription();
                    if(contentDescription != null){
                        isWECHAT_DETAIL = false;
                        isChat = false;
                        if(contentDescription.toString().contains("的聊天")){
                            isChat = true;
                            IS_RED_CLICK = false;
                        }else if(contentDescription.toString().contains("红包详情")){
                            isWECHAT_DETAIL = true;
                            if(IS_RED_CLICK || mNeedDetaBack){
                                boolean b = false;
                                try {
                                    Thread.sleep(500);
                                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                                    if(rootInActiveWindow != null){
                                        List<AccessibilityNodeInfo> gv_LinearLayout = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/gv");
                                        if(!gv_LinearLayout.isEmpty()){
                                            for (int i = 0; i < gv_LinearLayout.size(); i++) {
                                                AccessibilityNodeInfo accessibilityNodeInfo = gv_LinearLayout.get(i);
                                                b = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                            }
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mNeedDetaBack = false;
                            }
                        }
                    }
                }
                if(isChat)
                    perform();
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                AccessibilityNodeInfo source = event.getSource();
                if(source != null){
                    if(source.getClassName().toString().equals("android.widget.ListView")){
                        if(IS_RED_CLICK)break;
                        if(!isWECHAT_DETAIL){
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
                                                        Log.e(TAG, "onAccessibilityEvent: 领取红包 child:"+child.getClassName().toString()
                                                                +"\n\tIS_RED_CLICK:"+IS_RED_CLICK+"\tmNeedBack:"+mNeedBack+"\tIS_VIEW_SCROLLED:"+IS_VIEW_SCROLLED
                                                        );
                                                        if(accessibilityNodeInfo.isClickable()){
                                                            IS_VIEW_SCROLLED = false;
                                                            IS_RED_CLICK = true;
                                                            mLuckyMoneyPicked = true;
                                                            accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                        }else {
                                                            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                                                            while (parent != null) {
                                                                if (parent.isClickable()) {
                                                                    List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = null;
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                                        accessibilityNodeInfosByViewId = parent.findAccessibilityNodeInfosByViewId(ID_RED_ENVELOPE_CONTROL);
                                                                    }
                                                                    if(accessibilityNodeInfosByViewId != null && accessibilityNodeInfosByViewId.size() > 0){
                                                                        boolean b = false;
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                            b = parent.canOpenPopup();
                                                                        }
                                                                        if(b){
                                                                        }else{
                                                                            IS_VIEW_SCROLLED = false;
                                                                            IS_RED_CLICK = true;
                                                                            mLuckyMoneyPicked = true;
                                                                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                        }
                                                                        parent = null;
                                                                    }else {
                                                                        parent = parent.getParent();
                                                                        continue;
                                                                    }
                                                                    break;
                                                                }
                                                                parent = parent.getParent();
                                                            }
                                                        }
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
                    }else{
//                        if(IS_RED_CLICK && !mNeedBack)
//                            IS_RED_CLICK = false;
                    }
                    if (isChat)
                        perform();
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
                            Log.e(TAG, "onAccessibilityEvent: IS_RED_CLICK:"+IS_RED_CLICK +"\t类型视图滚动\tIS_VIEW_SCROLLED:"+IS_VIEW_SCROLLED);
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


    @Override
    public void onInterrupt() {

    }


    private synchronized void perform() {
        mUnpackNode = null;
        retrunJieMian();
        if (mNeedUnpack && (mUnpackNode != null)) {
            int size = mUnpackNode.size();
            if (size > 0) {
                AccessibilityNodeInfo cellNode = mUnpackNode.get(size - 1);
                boolean b = cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "onAccessibilityEvent: cellNode.performAction==" + b);
                if (b) {
                    mNeedDetaBack = true;
                }
                mNeedUnpack = false;
            }
        }

        if (mNeedBack) {
            boolean b = false;
            try {
                Log.e(TAG, "perform:前 " + "IS_RED_CLICK:" + IS_RED_CLICK + "\tmNeedBack:" + mNeedBack + "\tIS_VIEW_SCROLLED:" + IS_VIEW_SCROLLED + "\tperformAction:" + b);
                Thread.sleep(500);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    com.tencent.mm:id/bh8   关闭按钮id
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    if (rootInActiveWindow != null) {
                        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bh8");
                        if (!accessibilityNodeInfosByViewId.isEmpty()) {
                            for (int i = 0; i < accessibilityNodeInfosByViewId.size(); i++) {
                                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(i);
                                b = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
//                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mNeedBack = false;
            Log.e(TAG, "perform:后 " + "IS_RED_CLICK:" + IS_RED_CLICK + "\tmNeedBack:" + mNeedBack + "\tIS_VIEW_SCROLLED:" + IS_VIEW_SCROLLED + "\tperformAction:" + b);
        }
    }

    public void retrunJieMian() {
        AccessibilityNodeInfo nodeInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            nodeInfo = getRootInActiveWindow();
        }
        if (nodeInfo == null) return;
        List<AccessibilityNodeInfo> node2 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            node2 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bjj");
        }
        if (node2 != null && !node2.isEmpty()) {
            mUnpackNode = node2;
            mNeedUnpack = true;
            return;
        }
//        mLuckyMoneyPicked = mLuckyMoneyPicked?mLuckyMoneyPicked:!nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bh8").isEmpty();
        if (mLuckyMoneyPicked) {
            boolean isEmpty;
            if (isEmpty = nodeInfo.findAccessibilityNodeInfosByText("红包详情").isEmpty()) {
                if (isEmpty = nodeInfo.findAccessibilityNodeInfosByText("手慢了").isEmpty()) {
                    //该红包已超过24小时。如已领取，可在“我的红包”中查看  com.tencent.mm:id/bji  android.widget.TextView
                    if (isEmpty = nodeInfo.findAccessibilityNodeInfosByText("该红包已超过24小时。如已领取，可在“我的红包”中查看").isEmpty()) {
                        final List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bji");
                        if (isEmpty = (accessibilityNodeInfosByViewId == null || accessibilityNodeInfosByViewId.isEmpty())) {
                            //                                谁谁谁   给你发了一个红包 com.tencent.mm:id/bh6
                            List<AccessibilityNodeInfo> bh6 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bh6");
                            if (isEmpty = (bh6 == null || bh6.isEmpty())) {
                                return;
                            } else {
                                for (int i = 0; i < bh6.size(); i++) {
                                    AccessibilityNodeInfo accessibilityNodeInfo = bh6.get(i);
                                    CharSequence text = accessibilityNodeInfo.getText();
                                    if (text != null && text.toString().contains("给你发了一个红包")) {
                                        isEmpty = false;
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < accessibilityNodeInfosByViewId.size(); i++) {
                                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(i);
                                CharSequence text = accessibilityNodeInfo.getText();
                                if (text != null && text.toString().contains("该红包已超过24小时")) {
                                    isEmpty = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                mNeedDetaBack = true;
                return;
            }
            if (!isEmpty) {
//                ++number;
                mNeedBack = true;
                mLuckyMoneyPicked = false;
            }
        }
    }
}
