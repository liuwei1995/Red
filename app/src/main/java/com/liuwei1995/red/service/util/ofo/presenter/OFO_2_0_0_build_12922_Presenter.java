package com.liuwei1995.red.service.util.ofo.presenter;

import android.accessibilityservice.AccessibilityService;
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
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import com.blankj.utilcode.util.LogUtils;
import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.MainActivity;
import com.liuwei1995.red.service.OFOAccessibilityService;
import com.liuwei1995.red.service.OFOEntitySaveIntentService;

import java.util.List;

/**
 *
 * Created by liuwei on 2017/5/26 14:44
 */

public class OFO_2_0_0_build_12922_Presenter extends OFOPresenter {

    private static final String TAG = OFO_2_0_0_build_12922_Presenter.class.getSimpleName();

    private AccessibilityService accessibilityService;
    public OFO_2_0_0_build_12922_Presenter(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    private static final int RED_RECEIVER_CODE = 5;

    private NotificationManager notificationManager;

    private RedReceiver redReceiver;

    private int id = 15;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RED_RECEIVER_CODE:
                    if (notification == null) {
                        setSelfNotification();
                    }
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) accessibilityService.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(id, notification);
                    accessibilityService.startForeground(id, notification);
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

    public static final String ofo_MainActivity = "so.ofo.labofo.activities.journey.MainActivity";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen)return;//so.ofo.labofo.activities.journey.MainActivity
        switch (event.getEventType()){//so.ofo.labofo.activities.MenuActivity
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String s = event.getClassName().toString();
                if (ofo_MainActivity.equals(s)){
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    if (rootInActiveWindow == null){
                        break;
                    }
                    List<AccessibilityNodeInfo> unlock_code_list = rootInActiveWindow.findAccessibilityNodeInfosByText("解锁码");
                    if (unlock_code_list != null && unlock_code_list.size() > 0){
                        AccessibilityNodeInfo unlock_code = unlock_code_list.get(unlock_code_list.size() - 1);
                        AccessibilityNodeInfo parent = unlock_code.getParent();
                        if (parent != null){
                            int childCount = parent.getChildCount();
                            String pwd = "";
                            for (int i = 0; i < childCount; i++) {
                                AccessibilityNodeInfo child = parent.getChild(i);
                                CharSequence text = child.getText();
                                if (TextUtils.isEmpty(text) || text.length() != 1){
                                    pwd = "";
                                    LogUtils.d(TAG,"text===="+(text == null ? null :text.toString()));
                                    continue;
                                }
                                pwd += Integer.parseInt(text.toString());
                                LogUtils.d(TAG,"pwd===="+pwd);
                                if (pwd.length() == 4){
                                    CharSequence text_unlock_code = unlock_code.getText();
                                    if(!TextUtils.isEmpty(text_unlock_code)){
                                        String account = "";
                                        String toString = text_unlock_code.toString();
                                        for (int j = 0; j < toString.length(); j++) {
                                            char c = toString.charAt(j);
                                            try {
                                                int parseInt = Integer.parseInt("" + c);
                                                account += parseInt;
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        OFOEntitySaveIntentService.startActionBaz(getApplicationContext(),account,pwd);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                Log.e(TAG, "onAccessibilityEvent: 类型窗口状态改变" +s);
                break;//so.ofo.labofo.activities.journey.MainActivity
            default://so.ofo.labofo.activities.journey.ScanQrActivity
                break;
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow(){
        return accessibilityService.getRootInActiveWindow();
    }
    private void registerReceiver(RedReceiver redReceiver, IntentFilter filter) {
        accessibilityService.registerReceiver(redReceiver,filter);
    }
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        IntentFilter filter = new IntentFilter();
        filter.addAction(open);
        filter.addAction(close);
        redReceiver = new RedReceiver();
        registerReceiver(redReceiver, filter);
        Log.e(TAG, "onServiceConnected: " + TAG);
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
    private void unregisterReceiver(RedReceiver redReceiver) {
        accessibilityService.unregisterReceiver(redReceiver);
    }
    public Context getApplicationContext(){
        return accessibilityService.getApplicationContext();
    }
    public String getPackageName(){
        return getApplicationContext().getPackageName();
    }
    @Override
    public void onInterrupt() {
    }
    public static String open = OFOAccessibilityService.class.getSimpleName()+".open";
    public static String close = OFOAccessibilityService.class.getSimpleName()+".close";
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
        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已开启");
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
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已开启");
                } else if (intent.getAction().equals(close)) {//关
                    isOpen = false;
                    remoteViews.setViewVisibility(R.id.tv_close, View.GONE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已关闭");
                }
            handler.sendEmptyMessage(RED_RECEIVER_CODE);
        }

    }


}
