package com.liuwei1995.red.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by liuwei on 2017/4/20
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class NotificationService extends NotificationListenerService {

    private static final String TAG = NotificationService.class.getSimpleName();
    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if(WECHAT_PACKAGENAME.equals(packageName)) {
            Notification n = sbn.getNotification();
            // 标题和时间
            String title = "";
            if (n.tickerText != null) {
                title = n.tickerText.toString();
            }
            if (title.contains(": [微信红包]")) {
                try {
                    n.contentIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onNotificationPosted2(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if(WECHAT_PACKAGENAME.equals(packageName)){
            Notification n = sbn.getNotification();
            // 标题和时间
            String title = "";
            if (n.tickerText != null) {
                title = n.tickerText.toString();
            }
            if (title.contains(": [微信红包]")){
                try {
                    n.contentIntent.send();
                    return;
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
            long when = n.when;
            // 其它的信息存在一个bundle中，此bundle在android4.3及之前是私有的，需要通过反射来获取；android4.3之后可以直接获取
            Bundle bundle = null;
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                // android 4.3
                try {
                    Field field = Notification.class.getDeclaredField("extras");
                    bundle = (Bundle) field.get(n);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                // android 4.3之后
                bundle = n.extras;
            }
            if (bundle == null)return;
            // 内容标题、内容、副内容
            String contentTitle = bundle.getString(Notification.EXTRA_TITLE);
            if (contentTitle == null) {
                contentTitle = "";
            }
            String contentText = bundle.getString(Notification.EXTRA_TEXT);
            if (contentText == null) {
                contentText = "";
            }
            String contentSubtext = bundle.getString(Notification.EXTRA_SUB_TEXT);
            if (contentSubtext == null) {
                contentSubtext = "";
            }
            Log.e(TAG, "notify msg: title=" + title + " ,when=" + when
                    + " ,contentTitle=" + contentTitle + " ,contentText="
                    + contentText + " ,contentSubtext=" + contentSubtext);
            Log.e(TAG, "onNotificationPosted: -----------"+sbn.toString());
            Toast.makeText(this, contentTitle+":"+contentText, Toast.LENGTH_SHORT).show();
        }else{
            Log.e(TAG, "onNotificationPosted: -----------"+packageName);
        }

    }
//E/NotificationService: notify msg: title=爸: [微信红包]恭喜发财，大吉大利 ,when=1518708103029 ,contentTitle=爸 ,contentText=[2条]爸: [微信红包]恭喜发财，大吉大利 ,contentSubtext=
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if(WECHAT_PACKAGENAME.equals(packageName)){
            Log.e(TAG, "onNotificationRemoved: ---"+sbn.toString());
            return;
        }
        Log.i(TAG, "onNotificationRemoved" + "-----" + sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        super.onListenerHintsChanged(hints);
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }
}
