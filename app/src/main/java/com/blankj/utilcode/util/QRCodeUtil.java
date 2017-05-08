package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {
	public static final int MARGIN = 4;
	public static final float ZOOM = 0.2f;

	public static boolean createQRImageToFile(String content, int widthPix,int heightPix, Bitmap logoBm,String filePath) {
		return createQRImageToFile(content, widthPix, heightPix, logoBm, ZOOM, filePath);
	}
	public static boolean createQRImageToFile(String content, int widthPix,int heightPix, Bitmap logoBm,float zoom,String filePath) {
		return createQRImageToFile(content, widthPix, heightPix, logoBm, zoom, MARGIN, filePath);
	}
	/**
	 * 生成二维码Bitmap
	 *
	 * @param content
	 *            内容
	 * @param widthPix
	 *            图片宽度
	 * @param heightPix
	 *            图片高度
	 * @param logoBm
	 *            二维码中心的Logo图标（可以为null）
	 * @param filePath
	 *            用于存储二维码图片的文件路径
	 * @return 生成二维码及保存文件是否成功
	 */
	public static boolean createQRImageToFile(String content, int widthPix,int heightPix, Bitmap logoBm,float zoom,int margin, String filePath) {
		try {
			if (content == null || "".equals(content)) {
				return false;
			}
			// 配置参数
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 设置字符编码
			// 容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 设置空白边距的宽度
			// hints.put(EncodeHintType.MARGIN, 2); //default is 4
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(content,
					BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
			int[] pixels = new int[widthPix * heightPix];
			if (logoBm != null) {
				Matrix m = new Matrix();
//				// 将logo图片按martix设置的信息缩放
				int logo_w = (int) (widthPix*(zoom >= 1 || zoom <= 0 ?0.2:zoom));//logo的显示宽度
				int logo_h = (int) (heightPix*(zoom >= 1 || zoom <= 0 ?0.2:zoom));//logo的显示高度
				m.setScale((float)logo_w/logoBm.getWidth(), (float)logo_h/logoBm.getHeight());// 设置缩放信息
				logoBm = Bitmap.createBitmap(logoBm, 0, 0, logoBm.getWidth(),
						logoBm.getHeight(), m, false);
				int w_q = (widthPix - logo_w)/2;//
				int h_s = (heightPix - logo_h)/2;
				for (int y = 0; y < heightPix; y++) {
					for (int x = 0; x < widthPix; x++) {
						if(x > w_q && x < w_q + logo_w && y > h_s && y < h_s +logo_h){
							pixels[y * widthPix + x] = logoBm.getPixel(x - w_q, y - h_s);
						}else {
							if (bitMatrix.get(x, y)) {
								pixels[y * widthPix + x] = 0xff000000;
							} else {
								pixels[y * widthPix + x] = 0xffffffff;
							}
						}
					}
				}
			} else 
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = 0xff000000;
					} else {
						pixels[y * widthPix + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
			// 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							new FileOutputStream(filePath));
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * 
	 * @param content  类容
	 * @param widthPix  二维码的宽度
	 * @param heightPix  二维码的高度
	 * @param logoBm  logo
	 * @return
	 */
	public static Bitmap createQRImage(String content, int widthPix,int heightPix, Bitmap logoBm) {
		return createQRImage(content, widthPix, heightPix, logoBm, ZOOM);
	}
	public static Bitmap createQRImage(String content, int widthPix,int heightPix, Bitmap logoBm,float zoom) {
		return createQRImage(content, widthPix, heightPix, logoBm, zoom, MARGIN);
	}
	/**
	 * 
	
	 * @return
	 */
	/**
	 * 
	 * @param content  类容
	 * @param widthPix  二维码的宽度
	 * @param heightPix  二维码的高度
	 * @param logoBm  logo
	 * @param zoom  logo是  widthPix  heightPix 大小比例    0<zoom<1
	 * @param margin  二维码的白色边框  默认为4
	 * @return
	 */
	public static Bitmap createQRImage(String content, int widthPix,int heightPix, Bitmap logoBm,float zoom,int margin) {
	if (content == null || "".equals(content)) {
		return logoBm;
	}
	try {
		// 配置参数
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 设置字符编码
		// 容错级别
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置空白边距的宽度
		 hints.put(EncodeHintType.MARGIN, margin); //default is 4
		// 图像数据转换，使用了矩阵转换
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
		
		int[] pixels = new int[widthPix * heightPix];
		if (logoBm != null) {
			Matrix m = new Matrix();
//				// 将logo图片按martix设置的信息缩放
			int logo_w = (int) (widthPix*(zoom >= 1 || zoom <= 0 ?0.2:zoom));//logo的显示宽度
			int logo_h = (int) (heightPix*(zoom >= 1 || zoom <= 0 ?0.2:zoom));//logo的显示高度
			m.setScale((float)logo_w/logoBm.getWidth(), (float)logo_h/logoBm.getHeight());// 设置缩放信息
			logoBm = Bitmap.createBitmap(logoBm, 0, 0, logoBm.getWidth(),
					logoBm.getHeight(), m, false);
			int w_q = (widthPix - logo_w)/2;//
			int h_s = (heightPix - logo_h)/2;
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if(x > w_q && x < w_q + logo_w && y > h_s && y < h_s +logo_h){
						pixels[y * widthPix + x] = logoBm.getPixel(x - w_q, y - h_s);
					}else {
						if (bitMatrix.get(x, y)) {
//								pixels[y * widthPix + x] = 0xff00ffff;
							pixels[y * widthPix + x] = 0xff000000;
						} else {
							pixels[y * widthPix + x] = 0xffffffff;
						}
					}
				}
			}
		} else {
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = 0xff000000;
					} else {
						pixels[y * widthPix + x] = 0xffffffff;
					}
				}
			}
		}
		// 生成二维码图片的格式，使用ARGB_8888
		Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
		return bitmap;
	} catch (WriterException e) {
		e.printStackTrace();
	}
	return logoBm;
}

	public synchronized static Result scanningImage(Bitmap scanBitmap) {
		if(scanBitmap == null)return null;
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

		int[] pixels = new int[scanBitmap.getWidth()*scanBitmap.getHeight()];//保存所有的像素的数组，图片宽×高
		scanBitmap.getPixels(pixels,0,scanBitmap.getWidth(),0,0,scanBitmap.getWidth(),scanBitmap.getHeight());

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(),scanBitmap.getHeight(),pixels);
		BinaryBitmap bit = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bit, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	public synchronized static Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
		int sampleSize = (int) (options.outHeight / (float) 200);

		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;

		options.inJustDecodeBounds = false; // 获取新的大小

		scanBitmap = BitmapFactory.decodeFile(path, options);
		return scanningImage(scanBitmap);
	}
}