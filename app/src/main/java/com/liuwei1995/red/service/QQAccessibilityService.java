package com.liuwei1995.red.service;


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
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.MainActivity;
import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;

import java.lang.reflect.Field;
import java.util.List;


public class QQAccessibilityService extends AccessibilityService {

    private static final String TAG = QQAccessibilityService.class.getSimpleName();

    /**
     * 是否在聊天界面
     */
//    private boolean is_CHAT_INTERFACE = true;

    private boolean is_QQ_MAIN_INTERFACE = false;
//    /**
//     * 是否判断
//     */
//    private boolean is_JUDGE = false;

    private boolean WINDOW_STATE_CHANGED = false;

    private boolean isClick = false;
    /**
     * 是否需要返回
     */
    private boolean mNeedClickReturn = false;

    private boolean isAbsListView = false;



    /**
     * 判断聊天界面  需要返回
     */
    public boolean judgeChatInterface(){
        long start = System.currentTimeMillis();
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(QQPresenter.QQ_CHAT_INTERFACE_LISTVIEW_ID);
        if(!accessibilityNodeInfosByViewId.isEmpty()){
            AccessibilityNodeInfo AbsListViewAccessibilityNodeInfo = accessibilityNodeInfosByViewId.get(accessibilityNodeInfosByViewId.size() - 1);
            if(AbsListViewAccessibilityNodeInfo.getClassName().toString().equals(QQPresenter.QQ_CHAT_INTERFACE_AbsListView)){
                int childCount = AbsListViewAccessibilityNodeInfo.getChildCount();
                if(childCount > 2){
                    AccessibilityNodeInfo child = AbsListViewAccessibilityNodeInfo.getChild(childCount - 2);
                    List<AccessibilityNodeInfo> qqRedPa1cketPersonalityVersionList = child.findAccessibilityNodeInfosByText("QQ红包个性版");
                    if(!qqRedPa1cketPersonalityVersionList.isEmpty()){
                        AccessibilityNodeInfo qqRedPacketPersonalityVersion = qqRedPa1cketPersonalityVersionList.get(qqRedPa1cketPersonalityVersionList.size() - 1);//QQ红包
                        AccessibilityNodeInfo parentPersonalityVersion = qqRedPacketPersonalityVersion.getParent();
                        if(parentPersonalityVersion != null){
                            if(parentPersonalityVersion.isClickable()){
                                isClick = true;
                                mNeedClickReturn = true;
                                parentPersonalityVersion.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
                                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
                                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
            }else{
                Log.e(TAG, "onAccessibilityEvent: true  不是红包 time:"+(System.currentTimeMillis() - start));
            }
            return true;
        }else {
            Log.e(TAG, "onAccessibilityEvent: false time:"+(System.currentTimeMillis() - start));
            return false;
        }
    }
    /**详情返回 */
    private static final int DETAILS_TO_RETURN = 1;
    /**点开红包*/
    private static final int OPENS_RED = 2;

    private static final int SHURU = 3;

    private static final int RED_RECEIVER_CODE = 5;

    private NotificationManager notificationManager;

    private int id = 5;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DETAILS_TO_RETURN:
                    if(mNeedClickReturn){
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
                        mNeedClickReturn = false;
                    }
                    break;
                case OPENS_RED:
                    if(!is_QQ_MAIN_INTERFACE){
                        if(mNeedClickReturn){
                        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                            if(rootInActiveWindow != null){
                                List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(QQPresenter.QQ_CHAT_INTERFACE_LISTVIEW_ID);
                                if(accessibilityNodeInfosByViewId.isEmpty()){
                                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                }
                            }else {
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            }
                            mNeedClickReturn = false;
                         }
                    }
                    break;
                case RED_RECEIVER_CODE:
                    if (notification == null) {
                        setSelfNotification();
                    }
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(id, notification);
                    startForeground(id, notification);
                    break;
                default:
                    break;
            }
        }
    };
    private synchronized void sendEmptyMessageDelayed(int what){
        handler.removeMessages(what);
        handler.sendEmptyMessageDelayed(what,500);
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen)return;
        switch (event.getEventType()){//com.tencent.mobileqq.activity.PayBridgeActivity
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                WINDOW_STATE_CHANGED = true;
//                is_CHAT_INTERFACE = false;
                String s = event.getClassName().toString();
                if(s.equals(QQPresenter.QQ_MAIN_INTERFACE)){
                        if(!isClick && !mNeedClickReturn) {
                            Log.e(TAG, "onAccessibilityEvent: 进入判断是否需要点击");
//                            is_CHAT_INTERFACE =
                                    judgeChatInterface();
                        }else{
                            Log.e(TAG, "onAccessibilityEvent: 回来已经点击过了");
                        }
                    isClick = false;
                    isAbsListView = false;
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
                            isAbsListView = true;
                            if(!isClick){
//                                is_CHAT_INTERFACE =
                                        judgeChatInterface();
                            }else {
                                Log.e(TAG, "onAccessibilityEvent: 点开中-----" + event.getClassName().toString());
                            }
                        }
                    }
                    else {
//                        Log.e(TAG, "onAccessibilityEvent: =============" + event.getClassName().toString());
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
    /**
     * 处理通知栏信息
     * <p>
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains("[QQ红包]")) {
//                    isNotification = true;
                    String[] split = content.split(":");
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        Bundle bundle = null;
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            // android 4.3
                            try {
                                Field field = Notification.class.getDeclaredField("extras");
                                bundle = (Bundle) field.get(notification);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            // android 4.3之后
                            bundle = notification.extras;
                        }
                        if (bundle != null) {
                            if (content.length() > (split[0] + ": [QQ红包]").length()) {
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "handleNotification: 不是微信红包==============" + content);
                            }
                        }

//                        PendingIntent pendingIntent = notification.contentIntent;
//                        try {
//                            pendingIntent.send();
//                        } catch (PendingIntent.CanceledException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        }
    }
    private RedReceiver redReceiver;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected: " +getClass().toString());
        IntentFilter filter = new IntentFilter();
        filter.addAction(open);
        filter.addAction(close);
        redReceiver = new RedReceiver();
        registerReceiver(redReceiver, filter);
        Log.e(TAG, "onServiceConnected: " + TAG);
        /**
         *  versionName:6.5.7
         versionCode:1041
         */
        isOpen = true;
        handler.sendEmptyMessage(RED_RECEIVER_CODE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (redReceiver != null) {
            unregisterReceiver(redReceiver);
        }
        if (notification != null && notificationManager != null) {
            notificationManager.cancel(id);
        }
    }
    @Override
    public void onInterrupt() {
    }
    public static String open = QQAccessibilityService.class.getSimpleName()+".open";
    public static String close = QQAccessibilityService.class.getSimpleName()+".close";
    private Notification notification;
    RemoteViews remoteViews;
    //自定义通知
    protected void setSelfNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view);
        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher_round);
        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "QQ红包辅助功能已开启");
        remoteViews.setOnClickPendingIntent(R.id.icon, pendingIntent);
        PendingIntent broadcast_open = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(open), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_open, broadcast_open);
        PendingIntent broadcast_close = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(close), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_close, broadcast_close);
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContent(remoteViews)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setTicker("来了一条消息")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(false)//false 自己维护通知的消失  true  点击后消失
                .build();
//        notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
    }
    private boolean isOpen = false;
    public class RedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (remoteViews != null)
                if (intent.getAction().equals(open)) {//开
                    isOpen = true;
                    remoteViews.setViewVisibility(R.id.tv_close, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.GONE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "QQ红包辅助功能已开启");
                } else if (intent.getAction().equals(close)) {//关
                    isOpen = false;
                    remoteViews.setViewVisibility(R.id.tv_close, View.GONE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "QQ红包辅助功能已关闭");
                }
            handler.sendEmptyMessage(RED_RECEIVER_CODE);
        }

    }
}
