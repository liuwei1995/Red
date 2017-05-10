package com.liuwei1995.red;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.accessibility.AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;


public class HongbaoService extends AccessibilityService {
    private List<AccessibilityNodeInfo> mReiceiveNode = null;
    private List<AccessibilityNodeInfo> mUnpackNode = null;

    private boolean mLuckyMoneyPicked;
    private boolean mLuckyMoneyReceived;
    private boolean mNeedUnpack;
    private boolean mNeedBack = false;
    private int mLuckyMoneyCount = 1;
    private String mNextName = "领取红包";
    private String pageName;
    /**
     * 微信的包名
     */
    static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    /**
     * 拆红包类
     */
    static final String WECHAT_RECEIVER_CALSS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    /**
     * 红包详情类
     */
    static final String WECHAT_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    /**
     * 微信主界面或者是聊天界面
     */
    static final String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            CharSequence desrc = nodeInfo.getContentDescription();
            if(desrc != null){
                if(desrc.toString().contains("当前所在页面 ")){
                    String[] split = desrc.toString().split(",");
                    if(split.length > 1){
                        pageName = split[1];
                        Log.e(TAG, "onAccessibilityEvent: "+desrc.toString()+"\t"+pageName);
                    }
                }
            }
            String className = event.getClassName().toString();
            if (className.equals(WECHAT_LAUNCHER) || className.equals(WECHAT_RECEIVER_CALSS) || className.equals(WECHAT_DETAIL)) {
                get(event,nodeInfo,false);
            }else if (className.equals("android.widget.ListView")){
//                ListViewChange(nodeInfo);
                get(event,nodeInfo,true);
            }

            if (mNeedBack) {
                try {
                    Thread.sleep(3000);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mNeedBack = false;
            }
        }else if (eventType == TYPE_NOTIFICATION_STATE_CHANGED){
                Log.e(TAG, "onAccessibilityEvent: TYPE_NOTIFICATION_STATE_CHANGED  ");
                handleNotification(event);
        }
    }

    private void get(AccessibilityEvent event,AccessibilityNodeInfo nodeInfo,boolean isListView) {
        if (null != nodeInfo) {
            mReiceiveNode = null;
            mUnpackNode = null;
            checkNodeInfo(event,isListView);
            if (mLuckyMoneyReceived && !mLuckyMoneyPicked && (mReiceiveNode != null)) {
                int size = mReiceiveNode.size();
                if (size > 0) {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < mReiceiveNode.size(); i++) {
                        sb.append(mReiceiveNode.get(i).getClassName()+"\t"+mReiceiveNode.get(i).hashCode()+"\n");
                    }
                    Log.e(TAG, "onAccessibilityEvent: sb===="+sb.toString());
                    AccessibilityNodeInfo cellNode = mReiceiveNode.get(size-1);
                    boolean b = cellNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.e(TAG, "onAccessibilityEvent: cellNode.getParent().performAction=="+b );
                    if(b){
                        map.put(mReiceiveNode.hashCode(),mReiceiveNode);
                    }
                    mLuckyMoneyReceived = false;
                    mLuckyMoneyPicked = true;
                }
            }
            if (mNeedUnpack && (mUnpackNode != null)) {
                int size = mUnpackNode.size();
                if (size > 0) {
                    AccessibilityNodeInfo cellNode = mUnpackNode.get(size - 1);
                    boolean b = cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.e(TAG, "onAccessibilityEvent: cellNode.performAction=="+b );
                    mNeedUnpack = false;
                }
            }
        }
    }

    /**
     * 处理通知栏信息
     *
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains("[微信红包]")) {
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
                        if(bundle != null){
                            String contentTitle = bundle.getString(Notification.EXTRA_TEXT);
                            if(content.length() > ("1"+": [微信红包]").length()){
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Log.e(TAG, "handleNotification: 不是微信红包=============="+ content);
                            }
                        }
                    }
                }
            }
        }
    }
    private static final String TAG = "HongbaoService";

    private List<Integer> list = new ArrayList<>();

    private Map<Integer,List<AccessibilityNodeInfo>> map = new HashMap<>();

    /**
     *消息列表 com.tencent.mm:id/aft
     * 红包控件 com.tencent.mm:id/a5c
     * 红包开button控件 com.tencent.mm:id/bjj
     *  红包弹窗关闭按钮   左上角叉  com.tencent.mm:id/bh8
     *  红包详情返回   com.tencent.mm:id/gw
     * @param event
     */
    private void checkNodeInfo(AccessibilityEvent event,boolean isListView) {
        AccessibilityNodeInfo nodeInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            nodeInfo = getRootInActiveWindow();
        }

        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> node1 = nodeInfo.findAccessibilityNodeInfosByText(mNextName);
            if (!node1.isEmpty()) {
//                if(map.containsKey(node1.hashCode())){
//                    Log.e(TAG, "checkNodeInfo: com.tencent.mm:id/bjj==node1size=="+node1.size()+"==size===="+map.size()+"\t"+ +map.get(node1.hashCode()).hashCode());
//                    return;
//                }

//                if(isListView){
//                    ListViewChange(event.getSource(),true);
//                    return;
//                }else {
//                    mLuckyMoneyReceived = true;
//                    mReiceiveNode = node1;
//                    mLuckyMoneyCount++;
//                }
                mLuckyMoneyReceived = true;
                mReiceiveNode = node1;
                mLuckyMoneyCount++;
//                Log.e(TAG, "checkNodeInfo: com.tencent.mm:id/bjj==node1size=="+node1.size()+"==size===="+map.size()+"\t");
//                mNextName = "红包" + mLuckyMoneyCount;
                return;
            }
            List<AccessibilityNodeInfo> node2 = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                node2 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bjj");
            }
            if (node2 != null && !node2.isEmpty()) {
                Log.e(TAG, "checkNodeInfo: com.tencent.mm:id/bjj====" +node2.hashCode());
                mUnpackNode = node2;
                mNeedUnpack = true;
                return;
            }

            if (mLuckyMoneyPicked) {
                List<AccessibilityNodeInfo> node3 = nodeInfo.findAccessibilityNodeInfosByText("红包详情");
                List<AccessibilityNodeInfo> node4 = nodeInfo.findAccessibilityNodeInfosByText("手慢了");
                if (!node3.isEmpty() || !node4.isEmpty()) {
                    mNeedBack = true;
                    mLuckyMoneyPicked = false;
                }
            }
        }

    }
    /**
     *  变化
     * @param source
     */
    private void ListViewChange(AccessibilityNodeInfo source,boolean islast){
        AccessibilityNodeInfo parent = source.getParent();
        if(parent != null){
            if(islast){
                if(parent.getClassName().toString().equals("android.widget.LinearLayout")) {
                    int FrameLayout_FrameLayout_LinearLayout_childCount = parent.getChildCount();
                    for (int k = 0; k < FrameLayout_FrameLayout_LinearLayout_childCount; k++) {
                        AccessibilityNodeInfo FrameLayout_FrameLayout_LinearLayout_child = parent.getChild(k);
                        if (FrameLayout_FrameLayout_LinearLayout_child.getClassName().toString().equals("android.widget.ListView")) {
                            //      TODO  获得好多条消息
                            int FrameLayout_FrameLayout_LinearLayout_ListView_childCount = FrameLayout_FrameLayout_LinearLayout_child.getChildCount();
                            AccessibilityNodeInfo FrameLayout_FrameLayout_LinearLayout_ListView_child = FrameLayout_FrameLayout_LinearLayout_child.getChild(FrameLayout_FrameLayout_LinearLayout_ListView_childCount-1);
                            List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId =
                                    FrameLayout_FrameLayout_LinearLayout_ListView_child
                                            .findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a5c");
                            if(accessibilityNodeInfosByViewId == null || accessibilityNodeInfosByViewId.isEmpty())return;
                            mLuckyMoneyReceived = true;
                            mReiceiveNode = accessibilityNodeInfosByViewId;
                            mLuckyMoneyCount++;//Android Device Monitor is already launched
                            return;
                        }
                    }
                }
            }else
            if(parent.getClassName().toString().equals("android.widget.LinearLayout")){
                int FrameLayout_FrameLayout_LinearLayout_childCount = parent.getChildCount();
                for (int k = 0; k < FrameLayout_FrameLayout_LinearLayout_childCount; k++) {
                    AccessibilityNodeInfo FrameLayout_FrameLayout_LinearLayout_child = parent.getChild(k);
                    if(FrameLayout_FrameLayout_LinearLayout_child.getClassName().toString().equals("android.widget.ListView")){
//      TODO  获得好多条消息
                        int FrameLayout_FrameLayout_LinearLayout_ListView_childCount = FrameLayout_FrameLayout_LinearLayout_child.getChildCount();
                        for (int l = 0; l < FrameLayout_FrameLayout_LinearLayout_ListView_childCount; l++) {
                            AccessibilityNodeInfo FrameLayout_FrameLayout_LinearLayout_ListView_child = FrameLayout_FrameLayout_LinearLayout_child.getChild(l);
                            if(FrameLayout_FrameLayout_LinearLayout_ListView_child.getClassName().toString().equals("android.widget.RelativeLayout")){
                                int FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_childCount = FrameLayout_FrameLayout_LinearLayout_ListView_child.getChildCount();
                                for (int m = 0; m < FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_childCount; m++) {
                                    AccessibilityNodeInfo FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child = FrameLayout_FrameLayout_LinearLayout_ListView_child.getChild(m);
                                    if(FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child.getClassName().equals("android.widget.TextView")){
                                        CharSequence text = FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child.getText();
                                        Log.e(TAG, "getPacket:FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child "+FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child.getClassName().toString()
                                                +"\n"+(text == null?null:text.toString()));
                                        if(text != null && !TextUtils.isEmpty( text.toString())){
//                                            TODO      ---------------------------------------------
                                        }else {
                                            Log.e(TAG, "getPacket:FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child \t"+k+"\t"+FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child.getClassName().toString()
                                                    +"\n");
                                        }
                                    }else {
                                        Log.e(TAG, "getPacket:FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child \t"+k+"\t"+FrameLayout_FrameLayout_LinearLayout_ListView_RelativeLayout_child.getClassName().toString()
                                                +"\n");
                                    }
                                }
                            }

                        }
                    }else if ("android.widget.TextView".equals(FrameLayout_FrameLayout_LinearLayout_child.getClassName().toString())){
                        CharSequence text = FrameLayout_FrameLayout_LinearLayout_child.getText();
                        if (text != null){

                        }
                    }else {

                    }
                }
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        setNotification();
    }

    /**
     * 添加常驻通知
     */
    public void setNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();
        Intent intent = new Intent(this, MainActivity1.class);
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("刘伟").setContentText("红包功能已经打开").setTicker("Ticker").setWhen(when)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contextIntent).build();
        //使得服务能挂在通知栏
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(ONE, notification);
    }

    public static final int ONE = 0x100;

    // 取消通知
    public static  void cancelNotification(@NonNull Context mContext, int id) {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    @Override
    public void onInterrupt() {

    }

}
