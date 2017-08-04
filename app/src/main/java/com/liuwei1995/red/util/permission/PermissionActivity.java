package com.liuwei1995.red.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限Activity  别忘记添加  PermissionActivity
 * Created by liuwei on 2017/5/3
 *  <p>{
  <activity
     android:name=".util.permission.PermissionActivity"
     android:configChanges="keyboardHidden|orientation|screenSize"
     android:theme="@android:style/Theme.Translucent.NoTitleBar"
     android:windowSoftInputMode="stateHidden|stateAlwaysHidden"
     >
     </activity>
 </p>
 */

public final class PermissionActivity extends Activity {

    public static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";

    public static final String KEY_PERMISSIONS_REQUESTCODE = "KEY_PERMISSIONS_REQUESTCODE";

    public static final String ACTION_PERMISSION = "ACTION_PERMISSION";

    public static final String ACTION_SETTING = "ACTION_SETTING";

//    private static onRequestPermissionsResultCallback mPermissionListener;
//    private static onRequestPermissionsResultCallback mSettingPermissionListener;

//    public static void setPermissionListener(onRequestPermissionsResultCallback permissionListener) {
//        mPermissionListener = permissionListener;
//    }
//
//    public static void setSettingPermissionListener(onRequestPermissionsResultCallback permissionListener) {
//        mSettingPermissionListener = permissionListener;
//    }


    public static void startPermissionActivity(Context context,String[] mDeniedPermissions,onRequestPermissionsResultCallback onRequestPermissionsResultCallback){
//        PermissionActivity.setPermissionListener(onRequestPermissionsResultCallback);
        onRequestPermissionsResultCallbackUtils.newInstance().putPermissionListener(onRequestPermissionsResultCallback);
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setAction(PermissionActivity.ACTION_PERMISSION);
        intent.putExtra(PermissionActivity.KEY_INPUT_PERMISSIONS, mDeniedPermissions);
        intent.putExtra(PermissionActivity.KEY_PERMISSIONS_REQUESTCODE, 100);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void startSettingActivity(Context context,String[] mDeniedDontRemindPermissions,onRequestPermissionsResultCallback onRequestPermissionsResultCallback){
//        PermissionActivity.setSettingPermissionListener(onRequestPermissionsResultCallback);
        onRequestPermissionsResultCallbackUtils.newInstance().putSettingPermissionListener(onRequestPermissionsResultCallback);
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setAction(PermissionActivity.ACTION_SETTING);
        intent.putExtra(PermissionActivity.KEY_INPUT_PERMISSIONS, mDeniedDontRemindPermissions);
        intent.putExtra(PermissionActivity.KEY_PERMISSIONS_REQUESTCODE, 100);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_SETTING)){
            int requestCode = getIntent().getIntExtra(KEY_PERMISSIONS_REQUESTCODE, -1);
            if (onRequestPermissionsResultCallbackUtils.newInstance().getSettingPermissionListener() == null || requestCode == -1){
//            if (mSettingPermissionListener == null || requestCode == -1){
                finish();
            }else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, requestCode);
            }
        }else if(getIntent().getAction().equals(ACTION_PERMISSION)){
            Intent intent = getIntent();
            String[] permissions = intent.getStringArrayExtra(KEY_INPUT_PERMISSIONS);
            int requestCode = intent.getIntExtra(KEY_PERMISSIONS_REQUESTCODE, -1);
            if (onRequestPermissionsResultCallbackUtils.newInstance().getPermissionListener() == null || permissions == null || requestCode == -1)
                finish();
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,permissions,requestCode);
            }else
                finish();
//            if (mPermissionListener == null || permissions == null || requestCode == -1)
//                finish();
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(this,permissions,requestCode);
//            }else
//                finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(getIntent().getAction().equals(ACTION_PERMISSION)){
            if (onRequestPermissionsResultCallbackUtils.newInstance().getPermissionListener() != null)
                onRequestPermissionsResultCallbackUtils.PermissionListener.onRequestPermissionsResult(requestCode,permissions, grantResults);
//            if (mPermissionListener != null)
//                mPermissionListener.onRequestPermissionsResult(requestCode,permissions, grantResults);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//       回调 请求权限监听onRequestPermissionsResultCallback
        if(getIntent().getAction().equals(ACTION_SETTING)){
            if (onRequestPermissionsResultCallbackUtils.newInstance().getSettingPermissionListener() != null){
//            if (mSettingPermissionListener != null){
                String[] permissions = getIntent().getStringArrayExtra(KEY_INPUT_PERMISSIONS);
                List<String> deniedDontRemindList = new ArrayList<>(1);
                if (permissions != null){
                    for (int i = 0; i < permissions.length; i++) {
                        boolean b = hasPermission(this, permissions[i]);
                        if (!b){
                            deniedDontRemindList.add(permissions[i]);
                        }
                    }
                }
                int[] grantResults = new int[deniedDontRemindList.size()];
                for (int i = 0; i < deniedDontRemindList.size(); i++) {
                    grantResults[i] = -1;
                }
                onRequestPermissionsResultCallbackUtils.newInstance().SettingPermissionListener.onRequestPermissionsResult(requestCode,deniedDontRemindList.toArray(new String[deniedDontRemindList.size()]), grantResults);
//                mSettingPermissionListener.onRequestPermissionsResult(requestCode,deniedDontRemindList.toArray(new String[deniedDontRemindList.size()]), grantResults);
            }
        }
        finish();
    }
    public boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        String op = AppOpsManagerCompat.permissionToOp(permission);
        if (op == null)return false;
        int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
        if (result == AppOpsManagerCompat.MODE_IGNORED) return false;
        result = ContextCompat.checkSelfPermission(context, permission);
        if(result != PackageManager.PERMISSION_GRANTED) return false;
//        try {
//            final PackageInfo info = context.getPackageManager().getPackageInfo(
//                    context.getPackageName(), 0);
//            int targetSdkVersion = info.applicationInfo.targetSdkVersion;
//            int checkSelfPermission = -1;
//            if (targetSdkVersion >= Build.VERSION_CODES.M) {
//                checkSelfPermission = ActivityCompat.checkSelfPermission(context, permission);
//            }else {
//                checkSelfPermission = PermissionChecker.checkSelfPermission(context, permission);
//            }
//            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        return false;
    }
}