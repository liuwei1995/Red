package com.liuwei1995.red.service.util.xiaoka.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.liuwei1995.red.service.DesktopViewService;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;


/**
 * Created by liuwei on 2017/8/4 10:22
 */

public class XiaoKaPresenter41Impl extends XiaoKaPresenterImpl{

    private static final String TAG = "XiaoKaPresenter41Impl";

    public XiaoKaPresenter41Impl(AccessibilityService mAccessibilityService) {
        super(mAccessibilityService);
    }

    public void test(){
        AccessibilityNodeInfo info = getRootInActiveWindow();
        if (info != null) {
            if (info.getChildCount() == 0) {
                Log.i(TAG, "控件名称:" + info.getClassName());
                Log.i(TAG, "控件中的值：" + info.getText());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.i(TAG, "控件的ID:" + info.getViewIdResourceName());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.i(TAG, "点击是否出现弹窗:" + info.canOpenPopup());
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        LogUtils.d(TAG,info.getChild(i));
                    }
                }
            }
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen || TextUtils.isEmpty(start_txt))return;
        AccessibilityNodeInfo source = event.getSource();
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                if (source != null){
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = source.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/edit_chat");
//                        if (accessibilityNodeInfosByViewId != null && accessibilityNodeInfosByViewId.size() > 0){
//                            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(0);
//                            if (!TextUtils.isEmpty(txt))
//                                findEditText(accessibilityNodeInfo,txt);
//                        }
//                    }
//                }
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

    private String start_txt = null;
    private String execute_txt = null;

    @Override
    protected void start(String txt) {
        if (TextUtils.isEmpty(txt))return;
        synchronized (XiaoKaPresenter41Impl.class){
            this.start_txt = txt;
            this.execute_txt = txt;
            if (mHander != null){
                mHander.removeMessages(EXECUTE_MSG_WHAT);
            }
            isExecute = false;
            isStart = true;
            function();
        }
    }

    private boolean isExecute = false;
    private boolean isStart = false;

    @Override
    protected void execute(String txt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.execute_txt = txt;
            if (!TextUtils.isEmpty(execute_txt)) {
                isExecute = true;
                isStart = false;
                if (mHander != null){
                    mHander.removeMessages(EXECUTE_MSG_WHAT);
                }
                AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                if (rootInActiveWindow != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/edit_chat");
                        if (accessibilityNodeInfosByViewId != null && accessibilityNodeInfosByViewId.size() > 0) {
                            Boolean editText = findEditText(accessibilityNodeInfosByViewId.get(0), execute_txt + "\t\t\t\t_" + (new Random().nextLong()));
                            if (editText) {
                                List<AccessibilityNodeInfo> accessibilityNodeInfos_btn_send = rootInActiveWindow.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/btn_send");
                                if (accessibilityNodeInfos_btn_send != null && accessibilityNodeInfos_btn_send.size() != 0) {
                                    AccessibilityNodeInfo accessibilityNodeInfo_btn_send = accessibilityNodeInfos_btn_send.get(0);
                                    boolean performAction = accessibilityNodeInfo_btn_send.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                                    intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY, "发送：" + performAction);
                                    sendBroadcast(intent);
                                }
                            } else {
                                Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                                intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY, "黏贴：" + editText);
                                sendBroadcast(intent);
                            }
                        } else {
                            Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                            intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY, "没有控件  请点击消息按钮");
                            sendBroadcast(intent);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void update(String txt) {
        this.start_txt = txt;
        this.execute_txt = txt;
        if (TextUtils.isEmpty(txt))return;
        if (isExecute){
            execute(txt);
        }else if (isStart){
            start(txt);
        }
    }


    @Override
    protected void pause() {
        start_txt = null;
        this.execute_txt = null;
        mHander.removeMessages(EXECUTE_MSG_WHAT);
    }

    protected static final int EXECUTE_MSG_WHAT = 1;

    public synchronized void function(){
        if (mHander != null && !TextUtils.isEmpty(execute_txt)){
            mHander.removeMessages(EXECUTE_MSG_WHAT);
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow != null){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    List<AccessibilityNodeInfo>  accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/edit_chat");
                    if (accessibilityNodeInfosByViewId != null && accessibilityNodeInfosByViewId.size() > 0){
                        Boolean editText = findEditText(accessibilityNodeInfosByViewId.get(0), execute_txt+"\t\t\t\t_"+(new Random().nextLong()));
                        if (editText){
                            List<AccessibilityNodeInfo> accessibilityNodeInfos_btn_send = rootInActiveWindow.findAccessibilityNodeInfosByViewId(XIAOKA_PACKAGENAME + ":id/btn_send");
                            if (accessibilityNodeInfos_btn_send != null && accessibilityNodeInfos_btn_send.size() != 0){
                                AccessibilityNodeInfo accessibilityNodeInfo_btn_send = accessibilityNodeInfos_btn_send.get(0);
                                boolean performAction = accessibilityNodeInfo_btn_send.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                                intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY,"发送："+performAction);
                                sendBroadcast(intent);
                            }
                        }else {
                            Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                            intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY,"黏贴："+editText);
                            sendBroadcast(intent);
                        }
                    }else {
                        Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE_RESULT);
                        intent.putExtra(DesktopViewService.TV_RESULT_TXT_KEY,"没有控件  请点击消息按钮");
                        sendBroadcast(intent);
                    }
                }
            }
//            mHander.sendEmptyMessageDelayed(EXECUTE_MSG_WHAT,10*1000);
            mHander.sendEmptyMessageDelayed(EXECUTE_MSG_WHAT,100);
//            mHander.sendEmptyMessage(EXECUTE_MSG_WHAT);
        }
    }
    @Override
    public void handleMessage(WeakReference<XiaoKaPresenterImpl> weakReference, Message msg) {
        super.handleMessage(weakReference, msg);
        if (msg.what == EXECUTE_MSG_WHAT){
            if (!TextUtils.isEmpty(execute_txt)){
                function();
            }
        }

    }
}
