package com.liuwei1995.red.service.util.qq.presenter.impl;

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
import android.support.annotation.CallSuper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.MainActivity;
import com.liuwei1995.red.handler.TaskHandler;
import com.liuwei1995.red.handler.TaskHandlerImpl;
import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by liuwei on 2017/9/1 10:57
 */

public class QQPresenterImpl implements QQPresenter ,TaskHandler<QQPresenterImpl> {

    private static final String TAG = "QQPresenterImpl";

    private AccessibilityService accessibilityService;

    public QQPresenterImpl(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    private RedReceiver redReceiver;
    @Override
    public void onServiceConnected() {
        Log.e(TAG, "onServiceConnected: " +getClass().toString());
        IntentFilter filter = new IntentFilter();
        filter.addAction(OPEN);
        filter.addAction(CLOSE);
        redReceiver = new RedReceiver();
        registerReceiver(redReceiver, filter);
        isOpen = true;
        mHander.sendEmptyMessage(RED_RECEIVER_CODE);
    }


    /**
     * 处理通知栏信息
     * <p>
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event e
     */
    protected void handleNotification(AccessibilityEvent event) {
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
                        // android 4.3
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) try {
                            Field field = Notification.class.getDeclaredField("extras");
                            bundle = (Bundle) field.get(notification);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
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
                                Log.e(TAG, "handleNotification: 不是QQ红包 ==============" + content);
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

    private void registerReceiver(RedReceiver redReceiver, IntentFilter filter) {
        if (accessibilityService != null)
        accessibilityService.registerReceiver(redReceiver,filter);
    }

    protected void unregisterReceiver(RedReceiver redReceiver) {
        if (accessibilityService != null)
        accessibilityService.unregisterReceiver(redReceiver);
    }

    @Override
    public void onDestroy() {
        if (redReceiver != null) {
            unregisterReceiver(redReceiver);
        }
        if (notification != null && notificationManager != null) {
            notificationManager.cancel(QQAccessibilityService_id);
        }
    }

    @Override
    public void onInterrupt() {

    }


    public Handler mHander = new TaskHandlerImpl<>(this);

    private int QQAccessibilityService_id = 5;

    @CallSuper
    @Override
    public void handleMessage(WeakReference<QQPresenterImpl> weakReference, Message msg) {
        if (msg.what == RED_RECEIVER_CODE){
            if (notification == null) {
                setSelfNotification();
            }
            if (notificationManager == null) {
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.notify(QQAccessibilityService_id, notification);
            accessibilityService.startForeground(QQAccessibilityService_id, notification);
        }
    }

    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews remoteViews;

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
        PendingIntent broadcast_open = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(OPEN), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_open, broadcast_open);
        PendingIntent broadcast_close = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
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

    @Override
    public Context getApplicationContext(){
        return accessibilityService.getApplicationContext();
    }

    @Override
    public String getPackageName(){
        return getApplicationContext().getPackageName();
    }
    private static final int RED_RECEIVER_CODE = 5;

    public boolean isOpen = false;

    public class RedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (remoteViews != null)
                if (intent.getAction().equals(OPEN)) {//开
                    isOpen = true;
                    remoteViews.setViewVisibility(R.id.tv_close, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.GONE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "QQ红包辅助功能已开启");
                } else if (intent.getAction().equals(CLOSE)) {//关
                    isOpen = false;
                    remoteViews.setViewVisibility(R.id.tv_close, View.GONE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "QQ红包辅助功能已关闭");
                }
            mHander.sendEmptyMessage(RED_RECEIVER_CODE);
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow(){
        if (accessibilityService == null)return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return  accessibilityService.getRootInActiveWindow();
        }
        return null;
    }
    protected boolean performGlobalAction(int action){
        if (accessibilityService != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return accessibilityService.performGlobalAction(action);
        }
        return false;
    }

}
