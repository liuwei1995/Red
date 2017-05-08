package com.liuwei1995.red.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;

/**
 * Created by liuwei on 2017/4/19
 */

public class RedReceiver  extends BroadcastReceiver {

    public static final String ACTION_RED_OPEN = "redNew.intent.action.Red.Open";

    public static final String ACTION_RED_CLOSE = "redNew.intent.action.Red.close";

    public static final String ACTION_QQ_RED_OPEN = "redNew.intent.action.qq.Red.Open";

    public static final String ACTION_QQ_RED_CLOSE = "redNew.intent.action.qq.Red.close";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_RED_OPEN)){
            context.sendBroadcast(new Intent(WechatPresenter.open));
        }else if (intent.getAction().equals(ACTION_RED_CLOSE)){
            context.sendBroadcast(new Intent(WechatPresenter.close));
        }
    }
}
