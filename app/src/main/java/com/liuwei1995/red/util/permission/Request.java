package com.liuwei1995.red.util.permission;

import android.support.annotation.NonNull;

/**
 * Created by liuwei on 2017/5/3
 */

public interface Request<T extends Request> {

    /**
     * Here to fill in all of this to apply for permission, can be a, can be more.
     *
     * @param permissions one or more permissions.
     * @return {@link Request}.
     */
    @NonNull
    T setPermission(String... permissions);

    /**
     * Request code.
     *
     * @param requestCode int, the first parameter in callback {@code onRequestPermissionsResult(int, String[],
     *                    int[])}}.
     * @return {@link Request}.
     */
    @NonNull
    T setRequestCode(int requestCode);

    /**
     * Set the callback object.
     *
     * @return {@link Request}.
     */
    T setCallback(PermissionListener callback);

    /**
     * Request permission.
     */
    void start();
//    /**
//     * With user privilege refused many times, the Listener will be called back, you can prompt the user
//     * permissions role in this method.
//     *
//     * @param listener {@link RationaleListener}.
//     * @return {@link Request}.
//     */
//    @NonNull
//    T setRationale(RationaleListener listener);
}
