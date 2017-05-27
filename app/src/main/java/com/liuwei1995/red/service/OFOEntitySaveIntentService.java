package com.liuwei1995.red.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.liuwei1995.red.BaseApplication;
import com.liuwei1995.red.db.impl.OFOEntityEntityDaoImpl;
import com.liuwei1995.red.entity.OFOEntity;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OFOEntitySaveIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.liuwei1995.redNew.service.action.FOO";
    private static final String ACTION_BAZ = "com.liuwei1995.redNew.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.liuwei1995.redNew.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.liuwei1995.redNew.service.extra.PARAM2";

    public OFOEntitySaveIntentService() {
        super("OFOEntitySaveIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context,OFOEntity ofoEntity) {
        Intent intent = new Intent(context, OFOEntitySaveIntentService.class);
        intent.setAction(ACTION_FOO);
        Bundle bundle = new Bundle();
        bundle.putParcelable(OFOEntity.class.getSimpleName(),ofoEntity);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, OFOEntitySaveIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
    public static  String sdCardPath = null;
    @Override
    public void onCreate() {
        super.onCreate();
        if(sdCardPath == null || TextUtils.isEmpty(sdCardPath))
         sdCardPath = SDCardUtils.getSDCardPath();
        if(sdCardPath != null && !TextUtils.isEmpty(sdCardPath)){

        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                OFOEntity ofoEntity = intent.getParcelableExtra(OFOEntity.class.getSimpleName());
                if(ofoEntity == null)return;
                synchronized (this) {
                    try {
                        FileUtils.writeFileFromString(new File(new File(sdCardPath,getPackageName()),"account.txt"),"\n"+ofoEntity.toString(),true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (ACTION_BAZ.equals(action)) {
                final String account = intent.getStringExtra(EXTRA_PARAM1);
                final String accountPassword = intent.getStringExtra(EXTRA_PARAM2);
                synchronized (this){
                    OFOEntityEntityDaoImpl impl = new OFOEntityEntityDaoImpl(this);
                    List<OFOEntity> ofoEntities = impl.rawQuery("SELECT * FROM " + OFOEntity.class.getSimpleName() + " WHERE account = '" + account + "'");
                    if(ofoEntities == null || ofoEntities.isEmpty()){
                        OFOEntity ofoEntity = new OFOEntity();
                        ofoEntity.setVersionCode(BaseApplication.versionCode);
                        ofoEntity.setVersionName(BaseApplication.versionName);
                        ofoEntity.setAccount(""+account);
                        ofoEntity.setAccountPassword(accountPassword);
                        long insert = impl.insert(ofoEntity);
                        try {
                            FileUtils.writeFileFromString(new File(new File(sdCardPath,getPackageName()),"account.txt"),ofoEntity.toString(),true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "onHandleIntent: insert:"+insert );
                    }else {
                        Log.e(TAG, "onHandleIntent: " +ofoEntities.toString());
                    }
                }
            }
        }
    }

}
