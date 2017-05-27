package com.liuwei1995.red.util.permission.target;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by liuwei on 2017/5/3
 */

/**
 *
 * @param <T> <li>T  instanceof Context </li>
 *            <li>T  instanceof Activity</li>
 *            <li>T  instanceof android.support.v4.app.Fragment</li>
 *            <li>T  instanceof Fragment</li>
 */
public class AppTarget<T> implements Target{

    private T mContext;

    /**
     *
     * @param t <li>t  instanceof Context </li>
     *            <li>t  instanceof Activity</li>
     *            <li>t  instanceof android.support.v4.app.Fragment</li>
     *            <li>t  instanceof Fragment</li>
     */
    public AppTarget(T t) {
        this.mContext = t;
    }

    public Context getContext() {
        if (mContext instanceof Context)return (Context) mContext;
        else if (mContext instanceof android.support.v4.app.Fragment)
            return ((android.support.v4.app.Fragment)mContext).getContext();
        else if (mContext instanceof Fragment)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ((Fragment)mContext).getContext();
            }else
            return ((Fragment)mContext).getActivity();
        }
        return null;
    }

    public boolean shouldShowRationalePermissions(@NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        if(mContext instanceof Activity){
            Activity mContext = (Activity)this.mContext;
            for (String permission : permissions) {
                boolean rationale = mContext.shouldShowRequestPermissionRationale(permission);
                if (rationale) return true;
            }
        }
        return false;
    }

    public void startActivity(Intent intent) {
        if (mContext instanceof Context){
            ((Context) mContext).startActivity(intent);
        }
        else if (mContext instanceof android.support.v4.app.Fragment)
        {
            ((android.support.v4.app.Fragment) mContext).startActivity(intent);
        }
        else if (mContext instanceof Fragment)
        {
            ((Fragment) mContext).startActivity(intent);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (mContext instanceof Activity){
            ((Activity) mContext).startActivityForResult(intent,requestCode);
        }
        else if (mContext instanceof android.support.v4.app.Fragment)
        {
            ((android.support.v4.app.Fragment) mContext).startActivityForResult(intent,requestCode);
        }
        else if (mContext instanceof Fragment)
        {
            ((Fragment) mContext).startActivityForResult(intent,requestCode);
        }else if (mContext instanceof Context){
            ((Context) mContext).startActivity(intent);
        }
    }
}
