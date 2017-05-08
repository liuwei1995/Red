package com.liuwei1995.red.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SharedPreferencesUtil {
	interface save{
		void Object(Object object);
	}
	/**
	 *
	 * @param name  "zhaoyaobabase64"
	 * @param key
	 * @param context
	 */

	public static synchronized void saveBitmapByUrl(final Context context ,final String name,final String key, final String bturl) {
			final save save = new save() {
				@Override
				public void Object(Object object) {
					SharedPreferences preferences = context.getSharedPreferences(
							name, Activity.MODE_PRIVATE);
					Editor editor = preferences.edit();
					// 第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					((Bitmap)object).compress(CompressFormat.PNG, 80, byteArrayOutputStream);
					// 第二步:利用Base64将字节数组输出流中的数据转换成字符串String
					byte[] byteArray = byteArrayOutputStream.toByteArray();
					String imageString = new String(Base64.encode(byteArray,
							Base64.DEFAULT));
					editor.putString(key, imageString);
					editor.apply();
				}
			};
			downloadPictures(save, bturl);
	}
	public static synchronized void saveInt(final int value,final String name, final String key,final  Context context) {
		getSharedPreferences(context,name).edit().putInt(key,value).apply();
	}
	public static synchronized void saveObject(final Object object,final String name, final String key,final  Context context) {
		if (object==null)return;
		SharedPreferences mSharedPreferences = context.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			 baos = new ByteArrayOutputStream();
			 oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			String personBase64 = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			Editor editor = mSharedPreferences.edit();
			editor.putString(key, personBase64);
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				baos.close();
				oos.close();
			} catch (IOException e) {
				Log.i("SharedPreferencesUtil", "SharedPreferencesUtil---------"+e);
				e.printStackTrace();
			}

		}
	}

	/**
	 * 通过流保存图片
	 *
	 * @param context
	 * @param in
	 * @param PreferencesName
	 *            getSharedPreferences(PreferencesName, context.MODE_PRIVATE);
	 * @param Key
	 *            putString(Key, imageString);
	 */
	public static void saveBitmapToSharedPreferences(Context context,
			InputStream in, String PreferencesName, String Key) {
		Bitmap bitmap = BitmapFactory.decodeStream(in);
		// 第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 80, byteArrayOutputStream);
		// 第二步:利用Base64将字节数组输出流中的数据转换成字符串String
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String imageString = new String(
				Base64.encode(byteArray, Base64.DEFAULT));
		// 第三步:将String保持至SharedPreferences
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PreferencesName, context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(Key, imageString);
		editor.commit();
	}

	/**
	 * 通过key 得到图片
	 * 
	 * @param context
	 * @param PreferencesName
	 *            getSharedPreferences(PreferencesName, context.MODE_PRIVATE);
	 * @param Key
	 *            getString(Key, imageString);
	 * @return
	 */
	public static Bitmap getBitmapFromSharedPreferences(Context context,
			String PreferencesName, String Key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PreferencesName, Context.MODE_PRIVATE);
		// 第一步:取出字符串形式的Bitmap
		String imageString = sharedPreferences.getString(Key, "");
		if (imageString.trim().length() != 0) {
			// 第二步:利用Base64将字符串转换为ByteArrayInputStream
			byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					byteArray);
			// 第三步:利用ByteArrayInputStream生成Bitmap
			Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
			// mImageView.setImageBitmap(bitmap);
			return bitmap;
		}
		return null;
	}

	private static final String HEADPORTRAIT = "HeadPortrait";

	/**
	 * 使用SharedPreferences保存对象类型的数据 先将对象类型转化为二进制数据，然后用特定的字符集编码成字符串进行保存
	 * 
	 * @param object
	 *            要保存的对象
	 * @param context
	 * @param shaPreName
	 *            保存的文件名
	 */
	public static void saveListObject(Object object, Context context,String key,
			String shaPreName) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				shaPreName, Activity.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		List<Object> list = (List<Object>) getListObject(context, shaPreName);
		if (null == list) {
			list = new ArrayList<Object>();
		}
		list.add(object);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(list);
			String strList = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString(key, strList);
			editor.commit();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized static SharedPreferences getSharedPreferences(Context context,String shaPreName){
		return context.getSharedPreferences(shaPreName, Activity.MODE_PRIVATE);
	}
	public synchronized static float getFloat(Context context, String shaPreName, String key ) {
		return getSharedPreferences(context,shaPreName).getFloat(key,0);
	}

	public synchronized static void saveString(@NonNull Context context,@NonNull String value,String shaPreName, String key ) {
		getSharedPreferences(context,shaPreName).edit().putString(key,value).apply();
	}
	public synchronized static void saveBase64String(@NonNull Context context,@NonNull String value,String shaPreName, String key ) {
		String strBase64 = Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
		getSharedPreferences(context,shaPreName).edit().putString(key,strBase64).apply();
	}
	public synchronized static String getString(Context context, String shaPreName, String key ) {
		return getSharedPreferences(context,shaPreName).getString(key,"");
	}

	/**
	 * 读取Base64转换为可见的String
	 * @param context
	 * @param shaPreName
	 * @param key
     * @return
     */
	public synchronized static String getBase64String(Context context, String shaPreName, String key ) {
		return new String(Base64.decode(getSharedPreferences(context,shaPreName).getString(key,"").getBytes(), Base64.DEFAULT));
	}

	public synchronized static int getInt(Context context, String shaPreName, String key ) {
		return getSharedPreferences(context,shaPreName).getInt(key,0);
	}
	/**
	 * 根据文件名取得存储的数据对象 先将取得的数据转化成二进制数组，然后转化成对象
	 * 
	 * @param context
	 * @param shaPreName
	 *            读取数据的文件名
	 * @return
	 */
	public static Object getObject(Context context,String shaPreName, String key) {
//		List<Object> list;
		Object object;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				shaPreName, Activity.MODE_PRIVATE);
//		String message = sharedPreferences.getString(HEADPORTRAIT, "");
		String message = sharedPreferences.getString(key, "");
		byte[] buffer = Base64.decode(message.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			object = ois.readObject();
//			list = (List<Object>) ois.readObject();
			ois.close();
			return object;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static <T> T getObject(Context context,Class<T> class1, String shaPreName , String key ) {
		Object object = null;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				shaPreName, Activity.MODE_PRIVATE);
		String message = sharedPreferences.getString(key, "");
		if (message!=null&&!TextUtils.isEmpty(message)) {
			byte[] buffer = Base64.decode(message.getBytes(), Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			try {
				ObjectInputStream ois = new ObjectInputStream(bais);
				object = ois.readObject();
//			list = (List<Object>) ois.readObject();
				ois.close();
				return (T) object;
			} catch (StreamCorruptedException e) {
				object = UserJSON.instantiationClass(class1);
				e.printStackTrace();
				return (T) object;
			} catch (IOException e) {
				object = UserJSON.instantiationClass(class1);
				e.printStackTrace();
				return (T) object;
			} catch (ClassNotFoundException e) {
				object = UserJSON.instantiationClass(class1);
				e.printStackTrace();
				return (T) object;
			} finally {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			object = UserJSON.instantiationClass(class1);
		}
		return (T) object;
	}
	/**
	 * 根据文件名取得存储的数据对象 先将取得的数据转化成二进制数组，然后转化成对象
	 * 
	 * @param context
	 * @param shaPreName
	 *            读取数据的文件名
	 * @return
	 */
	public static Object getListObject(Context context, String shaPreName) {
		List<Object> list;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				shaPreName, Activity.MODE_PRIVATE);
		String message = sharedPreferences.getString(HEADPORTRAIT, "");
		byte[] buffer = Base64.decode(message.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			list = (List<Object>) ois.readObject();
			ois.close();
			return list;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 通过名字清除数据
	 * @param context
	 * @param shaPreName
	 */
	public static void clear(Context context, String shaPreName) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				shaPreName, Activity.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}
	public synchronized static void deleteByApply(@NonNull Context context, @NonNull String shaPreName) {
		deleteByApply(context,shaPreName,null);
	}
	public synchronized static void deleteByApply(@NonNull Context context, @NonNull String shaPreName,String key) {
		if(key != null){
			getSharedPreferences(context,shaPreName).edit().remove(key).apply();
		}else{
			getSharedPreferences(context,shaPreName).edit().clear().apply();
		}
	}
	public static synchronized void downloadPictures(final save save,final String bturl) {
		try {
			final URL url = new URL(bturl);
			new Thread(new Runnable() {
				@Override
				public void run() {
//			BigInteger.probablePrime(10, new Random());
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						// 请求超时时间
						conn.setConnectTimeout(15000);
						// 设置获取图片的方式为GET
						conn.setRequestMethod("GET");
						if (conn.getResponseCode() == 200) {
							final Bitmap  bitmap = BitmapFactory.decodeStream(conn.getInputStream());
							if (bitmap==null) {
								return;
							}
							save.Object(bitmap);
						}
					} catch (ProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
