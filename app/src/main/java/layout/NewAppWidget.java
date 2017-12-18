package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static String setOnClickPendingIntent_ = "setOnClickPendingIntent";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //创建一个Intent对象
//        Intent intent = new Intent();7
//        intent.setAction(setOnClickPendingIntent_);
//        //设置pendingIntent的作用
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        //绑定事件
//        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
//
////        views.setOnClickPendingIntent(R.id.appwidget_text,PendingIntent.getActivity(context,0,new Intent(context, MainActivity.class),0));
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);











        Intent fullIntent=new Intent(context,MainActivity.class);
//        若要传递数据，则使用intent.putExtra()方法

        PendingIntent Pfullintent=PendingIntent.getActivity(context, 0, fullIntent,PendingIntent.FLAG_CANCEL_CURRENT);
//②若该Intent不带数据，则最后一个参数设为0
//        PendingIntent Pfullintent=PendingIntent.getActivity(context, 0, fullIntent, 0);


        views.setOnClickPendingIntent(R.id.appwidget_text,Pfullintent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(setOnClickPendingIntent_)){
            Toast.makeText(context, setOnClickPendingIntent_, Toast.LENGTH_SHORT).show();
        }
    }
}

