package com.liuwei1995.red.util.permission;

import android.support.annotation.NonNull;

/**
 * Created by liuwei on 2017/6/21 13:48
 */

public interface onRequestPermissionsResultCallback {

    /**
     * {@link PermissionActivity#onRequestPermissionsResult(int, String[], int[])}
     * @param requestCode r
     * @param permissions p
     * @param grantResults g
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
