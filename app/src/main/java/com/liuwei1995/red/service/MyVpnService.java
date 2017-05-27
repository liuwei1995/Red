package com.liuwei1995.red.service;

import android.content.Intent;
import android.net.VpnService;

/**
 * Created by liuwei on 2017/4/14
 */

public class MyVpnService extends VpnService {
//    Builder builder = new Builder();
//    builder.setMtu(...);
//    builder.addAddress(...);
//    builder.addRoute(...);
//    builder.addDnsServer(...);
//    builder.addSearchDomain(...);
//    builder.setSession(...);
//    builder.setConfigureIntent(...);
//
//    ParcelFileDescriptor interface = builder.establish();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
