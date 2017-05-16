package com.liuwei1995.red.util;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;

/** 网络判断 */
public class NetWorkUtils {
	private NetWorkUtils(){}
	/**
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		// 提示对话框
		Builder builder = new Builder(context);
		builder.setTitle("网络设置提示")
				.setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		return isWifiConnected(context) || isMobileConnected(context);
	}


	/**
	 * 判断WIFI网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			//检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
				//获得ConnectivityManager对象
				ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

				//获取ConnectivityManager对象对应的NetworkInfo对象
				//获取WIFI连接的信息
				NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if(wifiNetworkInfo != null){
					return wifiNetworkInfo.isAvailable();
				}
			}else{
				//API大于23时使用下面的方式进行网络监听
				//获得ConnectivityManager对象
				ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

				//获取所有网络连接的信息
				Network[] networks = connMgr.getAllNetworks();
				//通过循环将网络信息逐个取出来
				for (int i=0; i < networks.length; i++){
					//获取ConnectivityManager对象对应的NetworkInfo对象
					NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
					if(networkInfo.getType()== ConnectivityManager.TYPE_WIFI){
						return networkInfo.isAvailable();
					}
				}
			}
		}
		return false;
	}


	/**
	 * 判断MOBILE网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			//检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
				//获得ConnectivityManager对象
				ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

				//获取ConnectivityManager对象对应的NetworkInfo对象
				//获取移动数据连接的信息
				NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(dataNetworkInfo != null){
					return dataNetworkInfo.isAvailable();
				}else {
					return false;
				}
			}else{
				//API大于23时使用下面的方式进行网络监听
				//获得ConnectivityManager对象
				ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				//获取所有网络连接的信息
				Network[] networks = connMgr.getAllNetworks();
				//通过循环将网络信息逐个取出来
				for (int i=0; i < networks.length; i++){
					//获取ConnectivityManager对象对应的NetworkInfo对象
					NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
					if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
						return networkInfo.isAvailable();
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前的网络状态
	 *
	 * @param context
	 * @return 没有网络0：WIFI网络1：3G网络2：2G网络3
	 */
	public static int getAPNType(Context context) {
		int netType = 0;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;// wifi
		} else if (nType == ConnectivityManager.TYPE_MOBILE) {
			int nSubType = networkInfo.getSubtype();
			TelephonyManager mTelephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
					&& !mTelephony.isNetworkRoaming()) {
				netType = 2;// 3G
			} else {
				netType = 3;// 2G
			}
		}
		return netType;
	}

	/** 判断GPS是否打开 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = lm.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/** 判断WIFI是否打开 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

}
