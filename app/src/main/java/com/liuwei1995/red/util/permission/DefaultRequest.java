package com.liuwei1995.red.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuwei on 2017/5/3
 */

public class DefaultRequest implements Request,PermissionActivity.PermissionListener ,RationaleListener {

    private AppTarget mTarget;
    private int mRequestCode = 1;
    private String[] mPermissions;
    private PermissionListener mCallback;

    private String[] mDeniedPermissions;

    DefaultRequest(AppTarget target) {
        if (target == null)
            throw new IllegalArgumentException("The target can not be null.");
        this.mTarget = target;
        if(targetSdkVersion < 0){
            try {
                final PackageInfo info = mTarget.getContext().getPackageManager().getPackageInfo(
                        mTarget.getContext().getPackageName(), 0);
                targetSdkVersion = info.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public DefaultRequest setPermission(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    @NonNull
    @Override
    public DefaultRequest setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    @Override
    public DefaultRequest setCallback(PermissionListener callback) {
        this.mCallback = callback;
        return this;
    }

    @Override
    public void start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callbackSucceed();
        } else {
            mDeniedPermissions = getDeniedPermissions(mTarget.getContext(), mPermissions);
            // Denied mPermissions size > 0.
            if (mDeniedPermissions.length > 0) {
                    resume();
            } else { // All permission granted.
                callbackSucceed();
            }
        }
    }

    private  String[] getDeniedPermissions(Context mContext, @NonNull String... permissions) {
        List<String> deniedList = new ArrayList<>(1);
        for (String permission : permissions){
            int i = -5;
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                i = ActivityCompat.checkSelfPermission(mContext, permission);
            }else {
                i = PermissionChecker.checkSelfPermission(mContext, permission);
            }
            if (i != PackageManager.PERMISSION_GRANTED)
                deniedList.add(permission);
        }
        return deniedList.toArray(new String[deniedList.size()]);
    }
    public static int targetSdkVersion = -1;

    @Override
    public void cancel() {
        int[] results = new int[mPermissions.length];
        for (int i = 0; i < mPermissions.length; i++){
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                results[i] = ActivityCompat.checkSelfPermission(mTarget.getContext(), mPermissions[i]);
            }else {
                results[i] = PermissionChecker.checkSelfPermission(mTarget.getContext(), mPermissions[i]);
            }
        }
        onRequestPermissionsResult(-1,mPermissions, results);
    }

    @Override
    public void showSettingDialog(@NonNull Context context, @NonNull RationaleListener rationale) {
        SettingDialog mSettingDialog = new SettingDialog(context,rationale);
        mSettingDialog.show();
    }

    @Override
    public void settingDialogConfirmCallBack(@NonNull RationaleListener rationale) {
        if (mCallback != null) {
            mCallback.settingDialogConfirmCallBack(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void resume() {
        PermissionActivity.setPermissionListener(this);
        Intent intent = new Intent(mTarget.getContext(), PermissionActivity.class);
        intent.setAction(PermissionActivity.ACTION_PERMISSION);
        intent.putExtra(PermissionActivity.KEY_INPUT_PERMISSIONS, mDeniedPermissions);
        intent.putExtra(PermissionActivity.KEY_PERMISSIONS_REQUESTCODE, 100);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mTarget.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> deniedList = new ArrayList<>();
        List<String> deniedDontRemindList = new ArrayList<>(1);
        for (int i = 0; i < permissions.length; i++)
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Context context = mTarget.getContext();
                    if(context instanceof Activity){
                        boolean b = ((Activity) context).shouldShowRequestPermissionRationale(permissions[i]);
                        if (!b){
                            deniedDontRemindList.add(permissions[i]);
                            continue;
                        }
                    }
                }
                deniedList.add(permissions[i]);
            }
        if(requestCode == -1){
            callbackCancel(deniedList,deniedDontRemindList);
        }else {
            if (deniedList.isEmpty() && deniedDontRemindList.isEmpty())
                callbackSucceed();
            else {
                callbackFailed(deniedList,deniedDontRemindList);
            }
        }
    }

    private void callbackSucceed() {
        if (mCallback != null) {
            mCallback.onSucceed(mTarget.getContext(),mRequestCode, Arrays.asList(mPermissions));
        }
    }

    private void callbackFailed(List<String> deniedList,List<String> deniedDontRemindList) {
        if (mCallback != null) {
            mCallback.onFailed(mTarget.getContext(),mRequestCode, deniedList,deniedDontRemindList,this);
        }
    }
    private void callbackCancel(List<String> deniedList,List<String> deniedDontRemindList) {
        if (mCallback != null) {
            mCallback.onCancel(mTarget.getContext(),mRequestCode, deniedList,deniedDontRemindList);
        }
    }

}
