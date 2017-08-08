package com.liuwei1995.red.util.permission;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwei on 2017/7/25 10:44
 */

public final class onRequestPermissionsResultCallbackUtils {

//    private WeakReference<onRequestPermissionsResultCallback> PermissionListenerWeakReference;
//    private WeakReference<onRequestPermissionsResultCallback> SettingPermissionListenerWeakReference;

    private Map<String,onRequestPermissionsResultCallback> map = new HashMap<>();

    public static onRequestPermissionsResultCallbackUtils newInstance() {
        if (monRequestPermissionsResultCallbackUtils == null){
            synchronized (onRequestPermissionsResultCallbackUtils.class){
                if (monRequestPermissionsResultCallbackUtils == null)
                monRequestPermissionsResultCallbackUtils = new onRequestPermissionsResultCallbackUtils();
            }
        }
        return monRequestPermissionsResultCallbackUtils;
    }

    private static onRequestPermissionsResultCallbackUtils monRequestPermissionsResultCallbackUtils = null;


    private onRequestPermissionsResultCallbackUtils() {

    }

    public void putPermissionListener(onRequestPermissionsResultCallback onRequestPermissionsResultCallbackUtils){
        synchronized (onRequestPermissionsResultCallbackUtils.class){
            map.put("putPermissionListener",onRequestPermissionsResultCallbackUtils);
//            PermissionListenerWeakReference = new WeakReference<>(onRequestPermissionsResultCallbackUtils);
//            onRequestPermissionsResultCallback onRequestPermissionsResultCallback = PermissionListenerWeakReference.get();
//            LogUtils.d(onRequestPermissionsResultCallback);
        }
    }

    /**
     * 内部调用
     * @return  onRequestPermissionsResultCallback
     */
    public   onRequestPermissionsResultCallback getOtherPermissionListener(){
        synchronized (onRequestPermissionsResultCallbackUtils.class){
            return map.containsKey("putPermissionListener") ? map.remove("putPermissionListener") : null;
//            if (PermissionListenerWeakReference != null){
//                return PermissionListenerWeakReference.get();
//            }
//            return null;
        }
    }

    /**
     * 提供给外部调用
     * @return onRequestPermissionsResultCallback
     */
    public static onRequestPermissionsResultCallback getPermissionListener(){
        return permissionListener;
    }


    public void putSettingPermissionListener(onRequestPermissionsResultCallback onRequestPermissionsResultCallbackUtils){
        synchronized (onRequestPermissionsResultCallbackUtils.class){
//            SettingPermissionListenerWeakReference = new WeakReference<>(onRequestPermissionsResultCallbackUtils);
            map.put("putSettingPermissionListener",onRequestPermissionsResultCallbackUtils);
        }
    }

    /**
     * 内部调用
     * @return onRequestPermissionsResultCallback
     */
    private   onRequestPermissionsResultCallback getOtherSettingPermissionListener(){
        synchronized (onRequestPermissionsResultCallbackUtils.class){
            return map.containsKey("putSettingPermissionListener") ? map.remove("putSettingPermissionListener") : null;
//            if (SettingPermissionListenerWeakReference != null){
//                return SettingPermissionListenerWeakReference.get();
//            }
//            return null;
        }
    }

    /***
     * 提供给外面的调用
     * @return
     */
    protected static onRequestPermissionsResultCallback getSettingPermissionListener(){
        return settingPermissionListener;
    }

    private static onRequestPermissionsResultCallback permissionListener = new onRequestPermissionsResultCallback() {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            onRequestPermissionsResultCallback permissionListener = onRequestPermissionsResultCallbackUtils.newInstance().getOtherPermissionListener();
            if (permissionListener != null){
                permissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    };
    private static onRequestPermissionsResultCallback settingPermissionListener = new onRequestPermissionsResultCallback() {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            onRequestPermissionsResultCallback settingPermissionListener = onRequestPermissionsResultCallbackUtils.newInstance().getOtherSettingPermissionListener();
            if (settingPermissionListener != null){
                settingPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    };

}
