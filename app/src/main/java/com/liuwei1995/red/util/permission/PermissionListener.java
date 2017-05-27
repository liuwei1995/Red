package com.liuwei1995.red.util.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by dell on 2017/5/3
 */

public abstract class PermissionListener {
    /**
     * 权限全部同意的时候回调
     * @param context 上下文
     * @param requestCode 请求的代码
     * @param grantPermissions 授予的权限
     */
     protected void onSucceed(Context context, int requestCode, @NonNull List<String> grantPermissions){

     }

    /**
     *  权限被拒绝的时候回调
     * @param context 上下文
     * @param requestCode 请求的代码
     * @param deniedPermissionsList 否认权限列表
     * @param deniedDontRemindList 否认不提醒列表
     * @param rationale  {@link RationaleListener#showSettingDialog(Context, RationaleListener)}
     */
    protected void  onFailed(@NonNull Context context,int requestCode, @NonNull List<String> deniedPermissionsList,@NonNull List<String> deniedDontRemindList,@NonNull RationaleListener rationale){

     }

    /**
     * {@link RationaleListener#showSettingDialog(Context, RationaleListener)}  这个方法点击取消之后回调
     * @param context  上下文
     * @param requestCode 请求的代码
     * @param deniedPermissionsList 否认权限列表
     * @param deniedDontRemindList 否认不提醒列表
     */
    protected void  onCancel(Context context, int requestCode,@NonNull List<String> deniedPermissionsList,List<String> deniedDontRemindList){

     }

    protected void settingDialogConfirmCallBack(@NonNull RationaleListener rationale){

    }
}
