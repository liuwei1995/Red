package com.liuwei1995.red.util.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 跳转权限设置界面
 * Created by liuwei on 2017/5/3
 */

public class SettingDialog {

    private @NonNull Context mContext;
    private  AlertDialog.Builder mBuilder;
    private RationaleListener rationale;
    private View view;

    public SettingDialog(@NonNull Context context,@NonNull RationaleListener rationale) {
        this(context,rationale,null);
    }

    public SettingDialog(@NonNull Context context, @NonNull RationaleListener rationale, @LayoutRes int resource) {
        this(context,rationale, LayoutInflater.from(context).inflate(resource,null));
    }

    public SettingDialog(@NonNull Context context, @NonNull RationaleListener rationale, View view) {
        this.mContext = context;
        this.rationale = rationale;
        this.view = view;
        mBuilder = new AlertDialog.Builder(context);
        if(view == null){
            mBuilder.setTitle("权限已被拒绝")
                    .setCancelable(false)
                    .setMessage("您已经拒绝过我们申请授权，请您同意授权，否则功能无法正常使用")
                    .setPositiveButton("开启",null)
                    .setNegativeButton("取消",null);
        }else {
            mBuilder.setView(view);
        }

    }
    @NonNull
    public SettingDialog setOnDismissListener(@NonNull DialogInterface.OnDismissListener onDismissListener) {
        if(view == null)
        mBuilder.setOnDismissListener(onDismissListener);
        return this;
    }
    @NonNull
    public SettingDialog setTitle(@NonNull String title) {
        if(view == null)
        mBuilder.setTitle(title);
        return this;
    }

    @NonNull
    public SettingDialog setTitle(@StringRes int title) {
        if(view == null)
        mBuilder.setTitle(title);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@NonNull String message) {
        if(view == null)
        mBuilder.setMessage(message);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@StringRes int message) {
        if(view == null)
        mBuilder.setMessage(message);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeButton(@NonNull String text, @Nullable DialogInterface.OnClickListener negativeListener) {
        if(view == null)
        mBuilder.setNegativeButton(text, negativeListener);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeButton(@StringRes int text, @Nullable DialogInterface.OnClickListener negativeListener) {
        if(view == null)
        mBuilder.setNegativeButton(text, negativeListener);
        return this;
    }
    @NonNull
    public SettingDialog setPositiveButton(@NonNull String text, @Nullable DialogInterface.OnClickListener negativeListener) {
        if(view == null)
        mBuilder.setPositiveButton(text, negativeListener);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveButton(@StringRes int text, @Nullable DialogInterface.OnClickListener negativeListener) {
        if(view == null)
        mBuilder.setPositiveButton(text, negativeListener);
        return this;
    }

    private AlertDialog dialog;
    public void show() {
        if(dialog == null || !dialog.isShowing()){
            synchronized (SettingDialog.class){
                if(dialog == null || !dialog.isShowing()){
                    dialog = mBuilder.show();
                }
            }
        }
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private PermissionActivity.PermissionListener permissionListener = new PermissionActivity.PermissionListener() {
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
           if(dialog != null && dialog.isShowing()){
               dialog.dismiss();
           }
            if(rationale != null) {
                rationale.settingDialogConfirmCallBack(rationale);
            }
        }
    };
    /**
     * 确定
     */
    public void confirm(){
        PermissionActivity.setSettingPermissionListener(permissionListener);
        Intent intent = new Intent(mContext, PermissionActivity.class);
        intent.setAction(PermissionActivity.ACTION_SETTING);
        intent.putExtra(PermissionActivity.KEY_PERMISSIONS_REQUESTCODE, 100);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
//
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
//        intent.setData(uri);
//        if(mContext instanceof Activity)
//        ((Activity)mContext).startActivityForResult(intent, 200);
//        else
//            mContext.startActivity(intent);
    }

    /**
     * 取消
     */
    public void cancel(){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        if (rationale != null)
            rationale.cancel();
    }
}
