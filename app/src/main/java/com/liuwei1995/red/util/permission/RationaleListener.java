package com.liuwei1995.red.util.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * 主要监听没有同意且不再提示的权限
 * Created by dell on 2017/5/3
 */

public interface RationaleListener {

    /**
     * 确认跳转权限设置界面  ，如果调用的是默认的显示默认的Dialog 既{@link SettingDialog} 就不用考虑这个方法
     * @param onRequestPermissionsResultCallback {@link onRequestPermissionsResultCallback}
     * @param mDeniedDontRemindList  没有同意且不再提示的权限数组
     */
     void confirm(onRequestPermissionsResultCallback onRequestPermissionsResultCallback, String... mDeniedDontRemindList);

    /**
     * 确认跳转权限设置界面  ，如果调用的是默认的Dialog 既{@link SettingDialog} 就不用考虑这个方法
     * @param onRequestPermissionsResultCallback {@link onRequestPermissionsResultCallback}
     * @param mDeniedDontRemindList  没有同意且不再提示的权限集合
     */
     void confirm(onRequestPermissionsResultCallback onRequestPermissionsResultCallback, List<String> mDeniedDontRemindList);

    /**
     * 取消跳转权限设置界面 ，如果调用的是默认的Dialog 既{@link SettingDialog} 就不用考虑这个方法
     * Cancel the operation.
     */
     void cancel();

    /**
     * 显示默认的Dialog {@link SettingDialog}
     * @param context c
     * @param rationale  {@link PermissionListener#onFailed(Context, int, List, List, RationaleListener)}
     * @param deniedDontRemindList 没有同意且不再提示的权限集合
     */
     void showSettingDialog(@NonNull Context context, @NonNull RationaleListener rationale, List<String> deniedDontRemindList);

    /**
     * 跳转权限设置界面回来的回调
     * {@link SettingDialog}
     * @param requestCode r
     * @param deniedDontRemindList 没有同意且不再提示的权限数组
     */
     void settingDialogCallBack(int requestCode, String[] deniedDontRemindList);

}
