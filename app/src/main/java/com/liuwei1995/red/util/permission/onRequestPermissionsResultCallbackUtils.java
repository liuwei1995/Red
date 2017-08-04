package com.liuwei1995.red.util.permission;

import android.support.annotation.NonNull;
import android.util.LruCache;

/**
 * Created by liuwei on 2017/7/25 10:44
 */

public final class onRequestPermissionsResultCallbackUtils {


    private static final int MAX_SIZE = (int)(Runtime.getRuntime().maxMemory()/8);
    private static final LruCache<String,onRequestPermissionsResultCallback> lru = new LruCache<>(MAX_SIZE);


    public static onRequestPermissionsResultCallbackUtils newInstance() {
//        if (monRequestPermissionsResultCallbackUtils == null){
//            synchronized (onRequestPermissionsResultCallbackUtils.class){
//                if (monRequestPermissionsResultCallbackUtils == null)
//                monRequestPermissionsResultCallbackUtils = new onRequestPermissionsResultCallbackUtils();
//            }
//        }
        return monRequestPermissionsResultCallbackUtils;
    }

    private static onRequestPermissionsResultCallbackUtils monRequestPermissionsResultCallbackUtils = new onRequestPermissionsResultCallbackUtils();



    private onRequestPermissionsResultCallbackUtils() {
    }

    public void putPermissionListener(onRequestPermissionsResultCallback onRequestPermissionsResultCallbackUtils){
        synchronized (lru){
            lru.put("PermissionListener",onRequestPermissionsResultCallbackUtils);
        }
    }
    public   onRequestPermissionsResultCallback getPermissionListener(){
        synchronized (lru){
            return lru.get("PermissionListener");
        }
    }


    public void putSettingPermissionListener(onRequestPermissionsResultCallback onRequestPermissionsResultCallbackUtils){
        synchronized (lru){
            lru.put("SettingPermissionListener",onRequestPermissionsResultCallbackUtils);
        }
    }
    public  onRequestPermissionsResultCallback getSettingPermissionListener(){
        synchronized (lru){
            return lru.get("SettingPermissionListener");
        }
    }
    private static void removePermissionListener(){
        synchronized (lru){
            lru.remove("PermissionListener");
        }
    }
    private static void removeSettingPermissionListener(){
        synchronized (lru){
            lru.remove("SettingPermissionListener");
        }
    }
    public static onRequestPermissionsResultCallback PermissionListener = new onRequestPermissionsResultCallback() {
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            onRequestPermissionsResultCallback permissionListener = onRequestPermissionsResultCallbackUtils.newInstance().getPermissionListener();
            if (permissionListener != null){
                permissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
                removePermissionListener();
            }
        }
    };
    public  onRequestPermissionsResultCallback SettingPermissionListener = new onRequestPermissionsResultCallback() {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            onRequestPermissionsResultCallback settingPermissionListener = onRequestPermissionsResultCallbackUtils.newInstance().getSettingPermissionListener();
            if (settingPermissionListener != null){
                settingPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
                removeSettingPermissionListener();
            }
        }
    };

}
