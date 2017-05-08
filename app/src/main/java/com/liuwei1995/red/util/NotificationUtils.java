package com.liuwei1995.red.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.liuwei1995.red.R;


/**
 * Created by linxins on 17-4-15.
 */

public class NotificationUtils {

    public static Notification createNotification(Context mContext, Class cls, int requestCode, CharSequence title, CharSequence text, CharSequence subText, long when) {
        Intent intent = new Intent(mContext, cls);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, 0);
        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(subText)
                .setWhen(when)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
//                收到消息时播放声音并震动
//                .setDefaults(NotificationCompat.DEFAULT_SOUND)//默认通知声音
//        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg")))
//                .setVibrate(new long[]{0, 1000, 1000, 1000})
                .setLights(Color.RED, 1000, 5000)
//               收到消息时播放声音并震动  根据手机当前环境使用默认
        .setDefaults(NotificationCompat.DEFAULT_ALL)
//         通知内容可以支持长文本
//        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
//         设置大图  类是京东
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher)))
//                PRIORITY_MAX表示最高的通知，这类通知必须要让用户立刻看到，甚至需要用户作出响应操作
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        return notification;
    }
    public static void notify(Context mContext, Notification notification){
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
    public static void createCustomNotification(Context mContext, @LayoutRes int custom_notification_layout) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), custom_notification_layout);
        Notification notification = new NotificationCompat.Builder(mContext)
                //切记，一定要设置 ，否则通知显示不出来
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setTicker("Custom Notification")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    /**
     * 注意：  自定义通知布局的可用高度取决于通知视图。普通视图布局限制为64dp，扩展视图布局限制为256dp
     */
}
