package com.liuwei1995.red.util.permission;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * 权限Activity  别忘记添加  PermissionActivity
 * Created by liuwei on 2017/5/3
 *  <p>{
//  <activity
//     android:name=".util.permission.PermissionActivity"
//     android:configChanges="keyboardHidden|orientation|screenSize"
//     android:theme="@android:style/Theme.Translucent.NoTitleBar"
//     android:windowSoftInputMode="stateHidden|stateAlwaysHidden"
//     >
//     </activity>
 </p>
 */

public final class PermissionActivity extends Activity {

    public static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";

    public static final String KEY_PERMISSIONS_REQUESTCODE = "KEY_PERMISSIONS_REQUESTCODE";

    public static final String ACTION_PERMISSION = "ACTION_PERMISSION";

    public static final String ACTION_SETTING = "ACTION_SETTING";

    private static PermissionListener mPermissionListener;
    private static PermissionListener mSettingPermissionListener;

    public static void setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
    }

    public static void setSettingPermissionListener(PermissionListener permissionListener) {
        mSettingPermissionListener = permissionListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_SETTING)){
            int requestCode = getIntent().getIntExtra(KEY_PERMISSIONS_REQUESTCODE, -1);
            if (mSettingPermissionListener == null || requestCode == -1){
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
            if (mPermissionListener == null || permissions == null || requestCode == -1)
                finish();
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,permissions,requestCode);
            }else
                finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(getIntent().getAction().equals(ACTION_PERMISSION)){
            if (mPermissionListener != null)
                mPermissionListener.onRequestPermissionsResult(requestCode,permissions, grantResults);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getIntent().getAction().equals(ACTION_SETTING)){
            if (mSettingPermissionListener != null)
                mSettingPermissionListener.onRequestPermissionsResult(requestCode,null, null);
        }
        finish();
    }

    interface PermissionListener {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}