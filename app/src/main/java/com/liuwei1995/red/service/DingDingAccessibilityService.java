package com.liuwei1995.red.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.liuwei1995.red.MainActivity;
import com.liuwei1995.red.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by liuwei on 2017/4/24
 */

public class DingDingAccessibilityService extends AccessibilityService {

    public static final String RIMET_PACKAGE = "com.alibaba.android.rimet";
    public static final String RIMET_CHAT_INTERFACE_LISTVIEW_ID = "com.alibaba.android.rimet:id/list_view";
/**
 * 群聊天界面listview
 * 1 android.view.ViewGroup com.alibaba.android.rimet:id/swipe_layout
 * 1 子类：
 * 2 android.widget.LinearLayout com.alibaba.android.rimet:id/list_content
 * 2 子类：
 * android.widget.ListView com.alibaba.android.rimet:id/list_view
 */
    /**
     * 底部输入框
     * android.widget.FrameLayout com.alibaba.android.rimet:id/rl_bottom
     *  子类：
     * android.widget.RelativeLayout com.alibaba.android.rimet:id/input_view
     *  子类：
     * android.widget.LinearLayout com.alibaba.android.rimet:id/input_root
     *  子类：
     * 4 android.widget.LinearLayout com.alibaba.android.rimet:id/rl_input
     * 4 子类：
     * android.widget.ImageView com.alibaba.android.rimet:id/btn_voice_switcher  语音按钮
     *
     * android.widget.ImageView com.alibaba.android.rimet:id/add_app  右边加号按钮(输入框没有内容的时候显示)
     * android.widget.TextView com.alibaba.android.rimet:id/btn_send  发送按钮(输入框有内容时显示)
     *
     * android.widget.LinearLayout
         * 4-1)子类：
         * 4-1) android.widget.RelativeLayout com.alibaba.android.rimet:id/fl_sendmessage_parent
             * 4-1-0)子类：
             * 4-1-0)android.widget.EditText com.alibaba.android.rimet:id/et_sendmessage
             * 4-1-0)android.widget.FrameLayout com.alibaba.android.rimet:id/right_icon_group
     *
     */

    /**首页*/
    public static final String ACTIVITIES_HOME_ACTIVITY = "com.alibaba.android.rimet.biz.home.activity.HomeActivity";
    /**
     * 首页listview
     * com.alibaba.android.rimet:id/session_list
     */

    /**聊天界面*/
    public static final String ACTIVITIES_CHAT_MSG_ACTIVITY = "com.alibaba.android.dingtalkim.activities.ChatMsgActivity";
    /***红包详情界面*/
    public static final String ACTIVITIES_RED_PACKETS_DETAIL_ACTIVITY = "com.alibaba.android.dingtalk.redpackets.activities.RedPacketsDetailActivity";
    /**红包弹窗*/
    public static final String DIALOG_DD_PROGRESS_DIALOG= "com.alibaba.android.dingtalkbase.widgets.dialog.DDProgressDialog";
    /**拿红包活动活动*/
    public static final String ACTIVITIES_PICK_RED_PACKETS_ACTIVITY= "com.alibaba.android.dingtalk.redpackets.activities.PickRedPacketsActivity";

    private boolean isMSG_ACTIVITY = false;
    /**红包详情界面*/
    private boolean isRED_PACKETS_DETAIL_ACTIVITY = false;
    private boolean NeedReturn = false;

    private boolean isClick = false;

    private boolean isSCROLLED = false;

    @Override
    public void onCreate() {
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()){//
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String s = event.getClassName().toString();
                isRED_PACKETS_DETAIL_ACTIVITY = false;
                isMSG_ACTIVITY = false;
                if(s.equals(ACTIVITIES_CHAT_MSG_ACTIVITY)){
                    isMSG_ACTIVITY = true;
                    if(!isClick){
                        isClick =  judgeChatInterface();//
                        if(isClick)
                            NeedReturn = true;
                        else{
                            NeedReturn = false;
                        }
                    }else {
                        isClick = false;
                        NeedReturn = false;
                    }
                }
                else if (s.equals(ACTIVITIES_RED_PACKETS_DETAIL_ACTIVITY)){
                    isRED_PACKETS_DETAIL_ACTIVITY = true;
                    if(isClick && NeedReturn){
                        sendEmptyMessageDelayed(GO_BACK);
                    }else {
                        isClick = true;
                    }
                }
                else if(s.equals(ACTIVITIES_PICK_RED_PACKETS_ACTIVITY)){
                    if(isClick){
                        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                        if(rootInActiveWindow != null){
                            List<AccessibilityNodeInfo> ll_pick_NodeInfos_list = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/ll_pick");
                            if (ll_pick_NodeInfos_list != null && !ll_pick_NodeInfos_list.isEmpty()){
                                AccessibilityNodeInfo ll_pick_NodeInfos = ll_pick_NodeInfos_list.get(ll_pick_NodeInfos_list.size() - 1);
                                if(ll_pick_NodeInfos.getChildCount() > 0){
                                    AccessibilityNodeInfo child = ll_pick_NodeInfos.getChild(0);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        boolean canOpenPopup = child.canOpenPopup();
                                        Log.e(TAG, "onAccessibilityEvent: canOpenPopup\t"+canOpenPopup);
                                    }
                                    if(child.isClickable()){//[95,1252][975,1462]
                                        // 可以不用在 Activity 中增加任何处理，各 Activity 都可以响应
                                        Message message = handler.obtainMessage();
                                        message.obj = child;
                                        message.what = SEND_POINTER_SYNC;
                                        handler.removeMessages(SEND_POINTER_SYNC);
                                        handler.sendMessage(message);
//                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                                            Bundle extras = child.getExtras();
//                                            boolean b = child.performAction(AccessibilityNodeInfo.ACTION_CLICK,extras);
//                                            Log.e(TAG, "onAccessibilityEvent: 拆红包=====："+b);
//                                        }else {
//                                            boolean b = child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                                        }
//                                        boolean b =  child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                                        performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP);
//                                        Log.e(TAG, "onAccessibilityEvent: 拆红包=====："+b);
                                        Log.e(TAG, "onAccessibilityEvent: 拆红包=====："+true);
                                    }else{
                                        Log.e(TAG, "onAccessibilityEvent: 拆红包=====："+false);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    isMSG_ACTIVITY = false;
                }
                Log.e(TAG, "onAccessibilityEvent: TYPE_WINDOW_STATE_CHANGED\t"+s);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if(isMSG_ACTIVITY){
                    if(isSCROLLED && !isClick){
                        isClick =  judgeChatInterface();//
                        if(isClick)
                            NeedReturn = true;
                        else{
                            NeedReturn = false;
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
                if (isMSG_ACTIVITY){
                    synchronized (this){
                        if(!isClick){
                            isSCROLLED = true;
                        }else {
                            isSCROLLED = false;
                        }
                    }
                    Log.e(TAG, "onAccessibilityEvent: TYPE_VIEW_SCROLLED  类型视图滚动");
                }
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
        handler.removeMessages(what);
        handler.sendEmptyMessageDelayed(what,500);
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

    private static final String TAG = "DingDingAccessibilitySe";

    private static final String CHATTING_CONTENT = "com.alibaba.android.rimet:id/chatting_content_view_stub";
    /**
     * 右边的红包
     * 1 android.widget.FrameLayout com.alibaba.android.rimet:id/chatting_content_view_stub
     * 1 子类：
     *   1-1）android.widget.TextView com.alibaba.android.rimet:id/tv_redpackets_type  个人红包
     *   1-0）android.widget.RelativeLayout
     *   1-0）子类：
     *      1-0）-0）android.widget.ImageView com.alibaba.android.rimet:id/icon_redpackets
     *      1-0）-1）android.widget.TextView com.alibaba.android.rimet:id/redpackets_desc  恭喜发财，大吉大利！
     *      1-0）-2）android.widget.TextView com.alibaba.android.rimet:id/redpackets_status  查看红包
     */

    /***
     * android.widget.FrameLayout com.alibaba.android.rimet:id/fr_content
     * 子类：
     * android.widget.LinearLayout com.alibaba.android.rimet:id/ll_pick  拆红包按钮
     *
     * android.widget.ImageView com.alibaba.android.rimet:id/redpackets_close  左上角关闭按钮
     */
    /**个人红包*/
    private String personage_red_envelopes = "个人红包";
    private String seeRedEnvelope = "查看红包";
    /**查看红包  id*/
    private String redpackets_status = "com.alibaba.android.rimet:id/redpackets_status";
    /**
     * 判断聊天界面  需要返回
     */
    public synchronized boolean judgeChatInterface() {
        long start = System.currentTimeMillis();
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(RIMET_CHAT_INTERFACE_LISTVIEW_ID);
        if (!accessibilityNodeInfosByViewId.isEmpty()) {
            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(accessibilityNodeInfosByViewId.size() - 1);
            if(accessibilityNodeInfo.getClassName().equals("android.widget.ListView")){
                int childCount = accessibilityNodeInfo.getChildCount();
                if(childCount > 0){
                    AccessibilityNodeInfo ListView_child = accessibilityNodeInfo.getChild(childCount - 1);
                    List<AccessibilityNodeInfo> redPacketClickList = ListView_child.findAccessibilityNodeInfosByViewId(CHATTING_CONTENT);
                    if(redPacketClickList != null && !redPacketClickList.isEmpty()){
                        AccessibilityNodeInfo redPacketClick = redPacketClickList.get(redPacketClickList.size() - 1);
                        List<AccessibilityNodeInfo> redEnvelopeslist = redPacketClick.findAccessibilityNodeInfosByText(personage_red_envelopes);
                        if(redEnvelopeslist != null && !redEnvelopeslist.isEmpty()){
                            List<AccessibilityNodeInfo> seeRedEnvelopeList = redPacketClick.findAccessibilityNodeInfosByViewId(redpackets_status);//查看红包
                            if(seeRedEnvelopeList != null && !seeRedEnvelopeList.isEmpty()){
                                AccessibilityNodeInfo seeRedEnvelopeNodeInfo = seeRedEnvelopeList.get(seeRedEnvelopeList.size() - 1);
                                if(seeRedEnvelopeNodeInfo.getText() != null){
                                    if(seeRedEnvelopeNodeInfo.getText().equals(seeRedEnvelope)){
                                        List<AccessibilityNodeInfo> chatting_unreadcount_tv1_list = ListView_child.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/chatting_unreadcount_tv1");
                                        if(chatting_unreadcount_tv1_list != null && !chatting_unreadcount_tv1_list.isEmpty()){
                                            AccessibilityNodeInfo chatting_unreadcount_tv1 = chatting_unreadcount_tv1_list.get(chatting_unreadcount_tv1_list.size());
                                            if(chatting_unreadcount_tv1.getText() != null){
                                                if(chatting_unreadcount_tv1.getText().toString().equals("已读") || chatting_unreadcount_tv1.getText().toString().equals("未读")){
                                                    Log.e(TAG, "judgeChatInterface: 自己发的红包："+chatting_unreadcount_tv1.getText().toString());
                                                }else{
                                                    Log.e(TAG, "judgeChatInterface: 自己发的红包："+chatting_unreadcount_tv1.getText().toString());
                                                }
                                            }
                                        }else if(redPacketClick.isClickable()){
                                            boolean b
                                                    = redPacketClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                                             = sendPointerSync(redPacketClick);
                                            if(b){
                                                isSCROLLED = false;
                                                isClick = true;
                                                Log.e(TAG, "judgeChatInterface:  true\t"+(System.currentTimeMillis() - start));
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            Log.e(TAG, "judgeChatInterface: 1\t"+(System.currentTimeMillis() - start));
        }else {
            Log.e(TAG, "judgeChatInterface: 2\t"+(System.currentTimeMillis() - start));
        }
        return false;
    }
    public synchronized boolean sendPointerSync(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null)return false;
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int centerX = rect.centerX();
        int centerY = rect.centerY();

        Instrumentation inst = new Instrumentation();
        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, centerX, centerY, 0));
        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, centerX, centerY, 0));
        return true;
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
                if (content.contains("["+ding_red_envelopes+"]")) {
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
                            if (content.length() > (split[0] + ": ["+ding_red_envelopes+"]").length()) {
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
    private static final int GO_BACK = 0;
    private static final int RED_RECEIVER_CODE = 5;
    private static final int SEND_POINTER_SYNC = 10;//  sendPointerSync(child);

    private String ding_red_envelopes = "钉钉红包";

    private String id_up = "android:id/up";
    private NotificationManager notificationManager;
    private int id = 10;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_BACK:
                    if(isRED_PACKETS_DETAIL_ACTIVITY){
                        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                        if(rootInActiveWindow != null){
                            List<AccessibilityNodeInfo> action_bar_title_list = rootInActiveWindow.findAccessibilityNodeInfosByViewId("android:id/action_bar_title");
                            if(action_bar_title_list != null && !action_bar_title_list.isEmpty()){
                                AccessibilityNodeInfo action_bar_title = action_bar_title_list.get(action_bar_title_list.size() - 1);
                                if (action_bar_title.getText() != null && action_bar_title.getText().toString().equals(ding_red_envelopes)){
                                    AccessibilityNodeInfo parent = action_bar_title.getParent();
                                    if(parent != null){
                                        List<AccessibilityNodeInfo> id_up_NodeInfos = parent.findAccessibilityNodeInfosByViewId(id_up);
                                        if (id_up_NodeInfos != null && !id_up_NodeInfos.isEmpty()){
                                            if(parent.isClickable()){
                                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                            }else if(parent.getParent() != null){
                                                if(parent.getParent().isClickable()){
                                                    parent.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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
                case SEND_POINTER_SYNC:
//                    sendPointerSync((AccessibilityNodeInfo) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    public static String open = DingDingAccessibilityService.class.getSimpleName()+".open";
    public static String close = DingDingAccessibilityService.class.getSimpleName()+".close";
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
        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "钉钉红包辅助功能已开启");
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
