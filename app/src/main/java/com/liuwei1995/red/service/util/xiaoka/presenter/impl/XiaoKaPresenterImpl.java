package com.liuwei1995.red.service.util.xiaoka.presenter.impl;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import com.liuwei1995.red.MainActivity1;
import com.liuwei1995.red.R;
import com.liuwei1995.red.handler.TaskHandler;
import com.liuwei1995.red.handler.TaskHandlerImpl;
import com.liuwei1995.red.service.util.xiaoka.presenter.XiaoKaPresenter;

import java.lang.ref.WeakReference;

/**
 * Created by liuwei on 2017/8/4 09:48
 */

public abstract class XiaoKaPresenterImpl implements XiaoKaPresenter ,TaskHandler<XiaoKaPresenterImpl>{

    private static final String TAG = "XiaoKaPresenterImpl";

    protected AccessibilityService mAccessibilityService;

    public XiaoKaPresenterImpl(@NonNull AccessibilityService mAccessibilityService) {
        this.mAccessibilityService = mAccessibilityService;
    }

    public boolean isOpen = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }
    @SuppressLint("NewApi")
    protected AccessibilityNodeInfo getRootInActiveWindow(){
        if (mAccessibilityService != null)
        return mAccessibilityService.getRootInActiveWindow();
        return null;
    }

    @CallSuper
    @Override
    public void onServiceConnected() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(open);
        filter.addAction(close);
        filter.addAction(ACTION_RECEIVER_SEND_PAUSE);
        filter.addAction(ACTION_RECEIVER_SEND_START);
        filter.addAction(ACTION_RECEIVER_EXECUTE);
        redReceiver = new RedReceiver();
        registerReceiver(redReceiver, filter);
        isOpen = true;
        mHander.sendEmptyMessage(START_FOREGROUND);
    }

    protected abstract void start(String txt);

    protected abstract void execute(String txt);

    protected abstract void pause();

    public class RedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_RECEIVER_SEND_PAUSE.equals(intent.getAction())){
                pause();
            }else if (ACTION_RECEIVER_SEND_START.equals(intent.getAction())){
                String stringExtra = intent.getStringExtra(ACTION_RECEIVER_SEND_START_KEY);
                if (!TextUtils.isEmpty(stringExtra)){
                    start(stringExtra);
                }
            }else if (ACTION_RECEIVER_EXECUTE.equals(intent.getAction())){
                String stringExtra = intent.getStringExtra(ACTION_RECEIVER_EXECUTE_KEY);
                if (!TextUtils.isEmpty(stringExtra)){
                    execute(stringExtra);
                }
            }else{
                if (remoteViews != null)
                    if (intent.getAction().equals(open)) {//开
                        isOpen = true;
                        remoteViews.setViewVisibility(R.id.tv_close, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.tv_open, View.GONE);
                        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "一直播辅助功能已开启");
                    } else if (intent.getAction().equals(close)) {//关
                        isOpen = false;
                        remoteViews.setViewVisibility(R.id.tv_close, View.GONE);
                        remoteViews.setViewVisibility(R.id.tv_open, View.VISIBLE);
                        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "一直播辅助功能已关闭");
                    }
                mHander.sendEmptyMessage(START_FOREGROUND);
            }
        }

    }

    public static final int START_FOREGROUND = 0;


    protected Handler mHander = new TaskHandlerImpl<>(this);

    @CallSuper
    @Override
    public void handleMessage(WeakReference<XiaoKaPresenterImpl> weakReference, Message msg) {
        if (msg.what == START_FOREGROUND){
            if (notification == null) {
                setSelfNotification();
            }
            if (notificationManager == null) {
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.notify(notificationId, notification);
            mAccessibilityService.startForeground(notificationId, notification);
        }
    }

    protected void sendBroadcast(Intent intent){
        if (mAccessibilityService != null && intent != null){
            mAccessibilityService.sendBroadcast(intent);
        }
    }

    private RedReceiver redReceiver;

    @CallSuper
    @Override
    public void onDestroy() {
        if (redReceiver != null) {
            unregisterReceiver(redReceiver);
        }
        if (notification != null && notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
        if (mHander != null){
            mHander.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onInterrupt() {

    }


    private RemoteViews remoteViews;

    @CallSuper
    public Context getApplicationContext() {
        return mAccessibilityService.getApplicationContext();
    }

    public String getPackageName() {
        return getApplicationContext().getPackageName();
    }

    @CallSuper
    private void registerReceiver(RedReceiver redReceiver, IntentFilter filter) {
        mAccessibilityService.registerReceiver(redReceiver, filter);
    }


    private void unregisterReceiver(RedReceiver redReceiver) {
        mAccessibilityService.unregisterReceiver(redReceiver);
    }

    private NotificationManager notificationManager;
    private Notification notification;


    //自定义通知
    private void setSelfNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view);
        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher_round);
        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "一直播辅助功能已开启");
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


}
