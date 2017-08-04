package com.liuwei1995.red.util.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 刘伟 on 2017/3/6
 */

public class PermissionUtils {

    public static final String TAG = PermissionUtils.class.getSimpleName();

    /**
     * 判断权限是否被禁止
     * @param context 上下文
     * @param permission 权限名字
     * @return 是否禁止
     */
    public static boolean isPermissionProhibit(@NonNull Context context, @NonNull String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(targetSdkVersion < 0){
                try {
                    final PackageInfo info = context.getPackageManager().getPackageInfo(
                            context.getPackageName(), 0);
                    targetSdkVersion = info.applicationInfo.targetSdkVersion;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            int checkSelfPermission = -1;
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                checkSelfPermission = ActivityCompat.checkSelfPermission(context, permission);
            }else {
                checkSelfPermission = PermissionChecker.checkSelfPermission(context, permission);
            }
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        }
        return  true;
    }

    /**
     * 用户是否同意权限
     * @param grantResults 回调结果
     * @return true
     */
    public static boolean onRequestPermissionsResult(@NonNull int[] grantResults){
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    /**
     * 用户是否同意权限
     * @param permissions 回调回来的权限集合
     * @param grantResults 回调回来的状态码集合
     * @return  返回权限没有同意的集合
     */
    public static List<String> onRequestPermissionsResult(@NonNull String[] permissions,@NonNull int[] grantResults){
        List<String> list = new ArrayList<>();
        if(permissions.length < 0 ||grantResults.length < 0 || permissions.length != grantResults.length)return list;
        for (int i = 0; i < grantResults.length; i++) {
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                list.add(permissions[i]);
            }
        }
        return list;
    }
    public static abstract class shouldShowRequest{

       public abstract boolean isShould(@NonNull Activity activity, @NonNull String permission, @NonNull int i);
//        同意
        protected void agreement(boolean isAgreement,@NonNull Activity activity,@NonNull int i){

        }
    }
    public static int targetSdkVersion = -1;
    /**
     *
     *一次请求多个权限
     * @param activity 请求的activity
     * @param requestCode  请求的状态码
     * @param should  <li>should == null or should != null<li/>
     *                 <li>if should != null {@link shouldShowRequest#isShould(Activity,String, int)} see {@link HealthyPopupWindow}</li>
     * @param permission  请求的权限名字 {@link android.Manifest.permission}
     */
    public static void startRequestPermissions(@NonNull Activity activity,final int requestCode,shouldShowRequest should,@NonNull String ...permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            List<String> list = new ArrayList<>();
            for (int i = 0; i < permission.length; i++) {
                String p =  permission[i];
                if(targetSdkVersion < 0){
                    try {
                        final PackageInfo info = activity.getPackageManager().getPackageInfo(
                                activity.getPackageName(), 0);
                        targetSdkVersion = info.applicationInfo.targetSdkVersion;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                int checkSelfPermission = -1;
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    checkSelfPermission = ActivityCompat.checkSelfPermission(activity, p);
                }else {
                    checkSelfPermission = PermissionChecker.checkSelfPermission(activity, p);
                }
                if(checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
                        list.add(p);
                    }else {
                        if(should != null){
                            if(should.isShould(activity,p,i)){

                            }
                        }
                    }
                }
            }
            if(!list.isEmpty()){
                ActivityCompat.requestPermissions(activity,list.toArray(new String[list.size()]), requestCode);
            }else{
                if(should != null){
                    should.agreement(true,activity,requestCode);
                }
            }
        }
    }
    /**
     *一次请求一个权限
     * @param activity 请求的activity
     * @param requestCode  请求的状态码
     * @param permission  请求的权限名字 {@link android.Manifest.permission}
     */
    public static void startRequestPermission(@NonNull Activity activity,final int requestCode,String message,@NonNull String permission,final DialogInterfaceCall dialogInterfaceCall){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int checkSelfPermission = ActivityCompat.checkSelfPermission(activity, permission);
            if(checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    shouldShowRationale(activity, requestCode, message,dialogInterfaceCall,permission);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                }
            }else {
                if(dialogInterfaceCall != null)
                    dialogInterfaceCall.onClick(null,-1);
            }
        }
    }
    private static void shouldShowRationale(final Activity activity, final int requestCode,String message,final DialogInterfaceCall dialogInterfaceCall, final String ...requestPermission) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("确认", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(activity,
                                requestPermission,
                                requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(dialogInterfaceCall != null)
                        dialogInterfaceCall.onClick(dialogInterface,-1);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确认", okListener)
                .setNegativeButton("取消", okListener)
                .setCancelable(false)
                .create()
                .show();

    }
    public interface DialogInterfaceCall{
        /**
         *
         * @param dialog
         * @param which
         */
        void onClick(DialogInterface dialog, int which);
    }

    public static void openSettingActivity(final Activity activity,final int requestCode, String message,final DialogInterfaceCall dialogInterfaceCall) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("确认", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivityForResult(intent,requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(dialogInterfaceCall != null)
                        dialogInterfaceCall.onClick(dialogInterface,-1);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
