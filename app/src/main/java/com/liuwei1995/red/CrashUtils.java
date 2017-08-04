package com.liuwei1995.red;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.blankj.utilcode.util.CloseUtils;
import com.liuwei1995.red.util.permission.PermissionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <pre>
 *     author: liuwei
 *     desc  : 崩溃相关工具类
 * </pre>
 */
public final class CrashUtils implements UncaughtExceptionHandler {

    private volatile static CrashUtils mInstance;


    private boolean mInitialized;
    private String crashDir;
    public static String versionName;
    public static int  versionCode;
    private UncaughtExceptionHandler mUncaughtExceptionHandler;

    private CrashUtils() {
    }

    /**
     * 获取单例
     * <p>在Application中初始化{@code CrashUtils.getInstance().init(this);}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>}</p>
     *
     * @return 单例
     */
    public static CrashUtils getInstance() {
        if (mInstance == null) {
            synchronized (CrashUtils.class) {
                if (mInstance == null) {
                    mInstance = new CrashUtils();
                }
            }
        }
        return mInstance;
    }
    private Context ctx ;
    /**
     * 初始化
     *
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
    public boolean init(Context ctx) {
        if (mInitialized) return true;
        this.ctx = ctx;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File baseCache = ctx.getExternalCacheDir();
            if (baseCache == null) return false;
            crashDir = baseCache.getPath() + File.separator + "crash" + File.separator;
        } else {
            File baseCache = ctx.getCacheDir();
            if (baseCache == null) return false;
            crashDir = baseCache.getPath() + File.separator + "crash" + File.separator;
        }
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        return mInitialized = true;
    }

    private static final String TAG = "CrashUtils";
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        boolean permissionProhibit = PermissionUtils.isPermissionProhibit(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!permissionProhibit)return;
        String now = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(new Date());
        final String fullPath = crashDir + now + ".txt";
        if (!createOrExistsFile(fullPath)) return;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                PrintWriter pw = null;
//                try {
//                    pw = new PrintWriter(new FileWriter(fullPath, false));
//                    pw.write(getCrashHead());
//                    throwable.printStackTrace(pw);
//                    Throwable cause = throwable.getCause();
//                    while (cause != null) {
//                        cause.printStackTrace(pw);
//                        cause = cause.getCause();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    CloseUtils.closeIO(pw);
//                }
//            }
//        }).start();


//        ----------------------------------------------------------------------
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(fullPath, false));
            pw.write(getCrashHeadMobileInfo());
            throwable.printStackTrace(pw);
            Throwable cause = throwable.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(pw);
        }
        mUncaughtExceptionHandler.uncaughtException(thread, throwable);
    }

    private String obtainExceptionInfo(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return sw.toString();
    }


    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private String getCrashHead() {
        return "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商
                "\nDevice Model       : " + Build.MODEL +// 设备型号
                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }

    private String getCrashHeadMobileInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n************* Crash Log Head ****************");
        sb.append("\nApp VersionName    : " + versionName);
        sb.append("\nApp VersionCode    : " + versionCode);
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())){
                continue;
            }
            field.setAccessible(true);
            try {
                Object o = field.get(null);
                String name = field.getName();
                sb.append("\n"+name+"    : "+(o == null ? null : o.toString())+"");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.append("\n************* Crash Log Head ****************\n\n");
        return sb.toString();
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String filePath) {
        File file = new File(filePath);
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }
}
