package com.liuwei1995.red.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by liuwei on 2017/5/9
 */

public abstract class RedAlertDialogV7 implements DialogInterface.OnDismissListener,View.OnClickListener {

    private AlertDialog alertDialog = null;

    public RedAlertDialogV7(@NonNull Context context, @LayoutRes int resource) {
        this(context,resource,0);
    }
    public RedAlertDialogV7(@NonNull Context context, @LayoutRes int resource,@StyleRes int resId) {
        this(context,resource,resId,null);
    }
    private RedAlertDialogV7(@NonNull Context context, @LayoutRes int resource,@StyleRes int resId,@Nullable ViewGroup root) {
        this(context, LayoutInflater.from(context).inflate(resource, root),resId);
    }
    private RedAlertDialogV7(@NonNull Context context,View view) {
        this(context,view,0);
    }
    private RedAlertDialogV7(@NonNull Context context,View view,@StyleRes int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        mContentView = view;
        convertView(view);
        builder.setView(view);
        // 创建对话框
        alertDialog = builder.create();
        if(resId > 0){
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                window.setWindowAnimations(resId);
            }
        }
        alertDialog.setOnDismissListener(this);
    }
    private View mContentView;
    /**
     * 修改view的配置
     * @param view v
     */
    protected   void convertView(View view){

    }

    public  <T extends View> T getView(@IdRes int id){
        if(mContentView != null)return (T) mContentView.findViewById(id);
        return null;
    }
    protected <T extends TextView> void setText(@IdRes int id, @StringRes int resid){
        TextView view = getView(id);
        if (view != null)
            view.setText(resid);
    }
    protected <T extends TextView> void setText(@IdRes int id,CharSequence text){
        TextView view = getView(id);
        if (view != null)
            view.setText(text);
    }
    protected  void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener){
        View view = getView(id);
        if (view != null)
            view.setOnClickListener(onClickListener);
    }


    public synchronized void show(){
        if(alertDialog != null && !alertDialog.isShowing()){
            alertDialog.show();
        }
    }
    public synchronized void dismiss(){
        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {

    }
}
