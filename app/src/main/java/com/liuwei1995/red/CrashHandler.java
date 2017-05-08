package com.liuwei1995.red;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常管理
 * @author lw
 * 编写时间: 2016年10月12日下午5:13:40
 * 类说明 :
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

	private Context ctx;
	private static final String TAG = "CrashHandler";

	private static CrashHandler instance; // 单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

	private Map<Object, Object> crashAppLog = new HashMap<Object, Object>();

	private CrashHandler() {
	}

	public synchronized static CrashHandler getInstance() { // 同步方法，以免单例多线程环境下出现异常
		if (instance == null) {
			instance = new CrashHandler();
		}
		return instance;
	}
	private boolean isTest_ = false;
	public void init(Context ctx, boolean isTest) { // 初始化，把当前对象设置成UncaughtExceptionHandler处理器
		//获取当前程序路径
		isTest_ = isTest;
		if(isTest_){
			String extSDCardPath = getExtSDCardPath(ctx);
			File file = new File(extSDCardPath,ctx.getPackageName());
			if(!file.exists() || !file.isDirectory()){
				file.mkdirs();
			}
			exceptionPath = file.getAbsolutePath();
		}else {
			String absolutePath = ctx.getFilesDir().getAbsolutePath();
			exceptionPath = absolutePath;
		}
		Log.e(TAG, isTest_+"====init: -----------------"+exceptionPath);
		if(exceptionPath == null)return;
		///data/data/com.zhaoyao.zhaoyaoba/files
		//		Log.i(TAG,absolutePath2);
		//获取该程序的安装包路径
		///data/app/com.zhaoyao.zhaoyaoba-1/base.apk
		//		String path = ctx.getPackageResourcePath();
		//		Log.i(TAG,path);
		//获取程序默认数据库路径
		//		/data/data/com.zhaoyao.zhaoyaoba/databases/zhaoyaoba
		//		String absolutePath = ctx.getDatabasePath("zhaoyaoba").getAbsolutePath();
		//		Log.i(TAG,absolutePath);
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.ctx = ctx;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) { // 当有未处理的异常发生时，就会来到这里。。
		collectCrashLogInfo(ctx);
		writeFile(thread,ex);
		String threadName = thread.getName();
		if ("sub1".equals(threadName)) {
		} else if (true) {
			// 这里我们可以根据thread name来进行区别对待，同时，我们还可以把异常信息写入文件，以供后来分析。
		}
//		System.exit(0);
	}
	/**
	 * 获取应用信息
	 * @param mContext
	 */
	private void collectCrashLogInfo(Context mContext) {
		try {
			if (mContext == null)
				return ;

			PackageManager packageManager = mContext.getPackageManager();

			if (packageManager != null) {

				PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

				if (packageInfo != null) {

					String versionName = packageInfo.versionName;
					String versionCode = ""+packageInfo.versionCode;
					String packName = packageInfo.packageName;

					crashAppLog.put("versionName",versionName);
					crashAppLog.put("versionCode",versionCode);
					crashAppLog.put("packName",packName);

				}
			}


			/**
			 * 获取手机型号，系统版本，以及SDK版本
			 */
			crashAppLog.put("手机型号:", Build.MODEL);
			crashAppLog.put("系统版本", ""+ Build.VERSION.SDK_INT);
			crashAppLog.put("Android版本", Build.VERSION.RELEASE);

			Field[] fields = Build.class.getFields();

			if (fields != null && fields.length > 0) {

				for (Field field:fields) {

					if (field != null) {

						field.setAccessible(true);

						crashAppLog.put(field.getName(), field.get(null).toString());
					}
				}
			}

		}catch (Exception e) {
			Log.e(TAG, "collectDeviceInfo - "+e.getMessage());
		}
	}

	public  int getLine(Throwable e) {
		StackTraceElement[] trace = e.getStackTrace();
		if (trace == null || trace.length == 0)
			return -1; //
		return trace[0].getLineNumber();
	}
	public String getMethodName(Throwable e) {
		StackTraceElement[] trace = e.getStackTrace();
		if (trace == null)
			return ""; //
		return trace[0].getMethodName();
	}
	public String getClassName(Throwable e) {
		StackTraceElement[] trace = e.getStackTrace();
		if (trace == null)
			return ""; //
		return trace[0].getClassName();
	}

	private String exceptionPath = null;
	@SuppressLint("SimpleDateFormat")
	private void writeFile(Thread thread, Throwable ex) {
		File file = new File(exceptionPath+"/Exce"+"/"+new SimpleDateFormat("yyyyMMdd").format(new Date())+"/"+thread.getName());
		if(!file.exists() || !file.isDirectory())
			file.mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file.getPath()+"/"+""+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"log.txt");
			if(ex.getStackTrace() != null && ex.getStackTrace().length > 0)
			crashAppLog.put("Exception", ex+"================"+ex.getStackTrace()[0].toString()+"============");
			else
			crashAppLog.put("Exception", ex+"=============="+getClassName(ex)+"==="+getMethodName(ex)+"===="+getLine(ex)+"===========");
			JSONObject jsonObject = new JSONObject(crashAppLog);
			byte[] bytes = jsonObject.toString().getBytes();
			fos.write(bytes, 0, bytes.length);
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取内置SD卡路径
	 * @return
	 */
	public  static String getInnerSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}
	/**
	 * 获取外置sdc路径
	 * @param context
	 * @return
	 */
	public static String getExtSDCardPath(Context context){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory().getPath();
		}
		return null;
	}
}
