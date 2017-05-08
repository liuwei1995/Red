package com.liuwei1995.red.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by liuwei on 2017/5/3
 */

public class AndPermission {
    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context     {@link Context}.
     * @param permissions one or more permissions.
     * @return true, other wise is false.
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_IGNORED) return false;
            result = ContextCompat.checkSelfPermission(context, permission);
            if(result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }





    /**
     * In the Activity.
     *
     * @param activity {@link Activity}.
     * @return {@link Request}.
     */
    public static
    @NonNull
    DefaultRequest with(@NonNull Activity activity) {
        return new DefaultRequest(new AppTarget<Activity>(activity));
    }

    /**
     * In the Activity.
     *
     * @param fragment {@link android.support.v4.app.Fragment}.
     * @return {@link Request}.
     */
    public static
    @NonNull
    DefaultRequest with(@NonNull android.support.v4.app.Fragment fragment) {
        return new DefaultRequest(new AppTarget<>(fragment));
    }

    /**
     * In the Activity.
     *
     * @param fragment {@link android.app.Fragment}.
     * @return {@link Request}.
     */
    public static
    @NonNull
    DefaultRequest with(@NonNull android.app.Fragment fragment) {
        return new DefaultRequest(new AppTarget<>(fragment));
    }

    /**
     * Anywhere..
     *
     * @param context {@link Context}.
     * @return {@link Request}.
     */
    public static
    @NonNull
    Request with(@NonNull Context context) {
        return new DefaultRequest(new AppTarget<>(context));
    }

    private AndPermission() {
    }
}
