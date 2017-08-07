package com.liuwei1995.red.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.AlerDesktopViewActivity;
import com.liuwei1995.red.service.util.xiaoka.presenter.XiaoKaPresenter;

import java.util.List;

/**
 * Created by liuwei on 2017/8/4 15:17
 */

public class DesktopViewService extends Service implements View.OnTouchListener{

    public static void startService(Context context) {
       Intent intent = new Intent(context,DesktopViewService.class);
        context.startService(intent);
    }

    public static final String ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_SHOW = "ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_SHOW";

    public static final String ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_HIDE = "ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_HIDE";

    public static final String ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE = "ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE";


    public static final String TV_CONTENT_TXT_KEY = "TV_CONTENT_TXT_KEY";



    public class DesktopViewServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_SHOW.equals(intent.getAction())){
                showDesktopView();
            }else if (ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_HIDE.equals(intent.getAction())){
                hideDesktopView();
            }else if (ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE.equals(intent.getAction())){
                String stringExtra = intent.getStringExtra(TV_CONTENT_TXT_KEY);
                if (!TextUtils.isEmpty(stringExtra)){
                    update(stringExtra);
                }
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DesktopViewServiceReceiver mDesktopViewServiceReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_SHOW);
        filter.addAction(ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_HIDE);
        filter.addAction(ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE);
        mDesktopViewServiceReceiver = new DesktopViewServiceReceiver();
        registerReceiver(mDesktopViewServiceReceiver,filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initDesktopView();
        return super.onStartCommand(intent, flags, startId);
    }

    public void showDesktopView(){
        initDesktopView();
    }

    public void hideDesktopView(){
        if (wm != null && desktopView != null)
            wm.removeView(desktopView);
    }

    public void update(String tv_content_txt){
        if (tv_content != null){
            tv_content.setText(tv_content_txt);
        }
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mDesktopViewServiceReceiver);
        if (wm != null && desktopView != null)
        wm.removeView(desktopView);
        super.onDestroy();

    }

    private WindowManager wm;
    private WindowManager.LayoutParams layoutParams;

    private View desktopView;

    private Button btn_send;
    private Button btn_close;
    private Button btn_alter;
    private TextView tv_content;
    private String tv_content_txt = "";
    private String btn_send_txt = "开始";

    public void initDesktopView(){
        if (desktopView == null){
            desktopView = LayoutInflater.from(this).inflate(R.layout.layout_desktop, null);
            tv_content = (TextView) desktopView.findViewById(R.id.tv_content);
            btn_send = (Button) desktopView.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(tv_content.getText().toString().trim())){
                        synchronized (DesktopViewService.class){
                            if (!TextUtils.isEmpty(tv_content.getText().toString().trim())){
                                AccessibilityManager accessibilityManager =(AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
                                List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
                                if(accessibilityServices != null) {
                                    boolean XiaoKaServiceEnabled = false;
                                    for (AccessibilityServiceInfo info : accessibilityServices) {
                                        if (info.getId().contains(XiaoKaAccessibilityService.class.getSimpleName())){
                                            XiaoKaServiceEnabled = true;
                                        }
                                    }
                                    if (!XiaoKaServiceEnabled){
                                        return;
                                    }
                                    Button bt = (Button) v;
                                    if (bt.getText().toString().equals("开始")){
                                        bt.setText("暂停");
                                        btn_send_txt = "暂停";
                                        Intent intent = new Intent(XiaoKaPresenter.ACTION_RECEIVER_SEND_START);
                                        intent.putExtra(XiaoKaPresenter.ACTION_RECEIVER_SEND_START_KEY,tv_content.getText().toString().trim());
                                        sendBroadcast(intent);
                                    }else {
                                        bt.setText("开始");
                                        btn_send_txt = "开始";
                                        sendBroadcast(new Intent(XiaoKaPresenter.ACTION_RECEIVER_SEND_PAUSE));
                                    }
                                }else {

                                }
                            }
                        }
                    }
                }
            });

            btn_close = (Button) desktopView.findViewById(R.id.btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(XiaoKaPresenter.ACTION_RECEIVER_SEND_PAUSE));
                    hideDesktopView();
                }
            });
            btn_alter = (Button) desktopView.findViewById(R.id.btn_alter);
            btn_alter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlerDesktopViewActivity.startActivity(v.getContext());
                }
            });

            wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            //设置TextView的属性
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //这里是关键，使控件始终在最上方
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //这个Gravity也不能少，不然的话，下面"移动歌词"的时候就会出问题了～ 可以试试[官网文档有说明]
            layoutParams.gravity = Gravity.LEFT|Gravity.TOP;

            //监听 OnTouch 事件 为了实现"移动歌词"功能
            desktopView.setOnTouchListener(this);

            wm.addView(desktopView, layoutParams);
        }else {
            wm.removeView(desktopView);
            wm.addView(desktopView, layoutParams);
        }
        if (tv_content != null){
            tv_content.setText(tv_content_txt);
        }
        if (btn_send != null){
            btn_send.setText(btn_send_txt);
        }
    }

    private float rawX;
    private float rawY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                rawX = event.getRawX();
                rawY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //getRawX/Y 是获取相对于Device的坐标位置 注意区别getX/Y[相对于View]
                layoutParams.x = layoutParams.x + (int) (event.getRawX() - rawX);
                layoutParams.y = layoutParams.y + (int) (event.getRawY() - rawY);
                rawX = event.getRawX();
                rawY = event.getRawY();
                //更新"桌面歌词"的位置
                wm.updateViewLayout(desktopView,layoutParams);
                //下面的removeView 可以去掉"桌面歌词"
                //wm.removeView(myView);
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.x = layoutParams.x + (int) (event.getRawX() - rawX);
                layoutParams.y = layoutParams.y + (int) (event.getRawY() - rawY);
                rawX = event.getRawX();
                rawY = event.getRawY();
                wm.updateViewLayout(desktopView,layoutParams);
                break;
        }
        return false;
    }
}
