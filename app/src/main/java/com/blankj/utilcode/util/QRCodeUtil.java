package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.BitmapLuminanceSource;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.liuwei1995.red.zxing.decoding.DecodeFormatManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {
    public static final int MARGIN = 4;
    public static final float ZOOM = 0.2f;

    public static boolean createQRImageToFile(String content, int widthPix, int heightPix, Bitmap logoBm, String filePath) {
        return createQRImageToFile(content, widthPix, heightPix, logoBm, ZOOM, filePath);
    }

    public static boolean createQRImageToFile(String content, int widthPix, int heightPix, Bitmap logoBm, float zoom, String filePath) {
        return createQRImageToFile(content, widthPix, heightPix, logoBm, zoom, MARGIN, filePath);
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @param filePath  用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImageToFile(String content, int widthPix, int heightPix, Bitmap logoBm, float zoom, int margin, String filePath) {
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
                int logo_w = (int) (widthPix * (zoom >= 1 || zoom <= 0 ? 0.2 : zoom));//logo的显示宽度
                int logo_h = (int) (heightPix * (zoom >= 1 || zoom <= 0 ? 0.2 : zoom));//logo的显示高度
                m.setScale((float) logo_w / logoBm.getWidth(), (float) logo_h / logoBm.getHeight());// 设置缩放信息
                logoBm = Bitmap.createBitmap(logoBm, 0, 0, logoBm.getWidth(),
                        logoBm.getHeight(), m, false);
                int w_q = (widthPix - logo_w) / 2;//
                int h_s = (heightPix - logo_h) / 2;
                for (int y = 0; y < heightPix; y++) {
                    for (int x = 0; x < widthPix; x++) {
                        if (x > w_q && x < w_q + logo_w && y > h_s && y < h_s + logo_h) {
                            pixels[y * widthPix + x] = logoBm.getPixel(x - w_q, y - h_s);
                        } else {
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
     * @param content   类容
     * @param widthPix  二维码的宽度
     * @param heightPix 二维码的高度
     * @param logoBm    logo
     * @return
     */
    public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm) {
        return createQRImage(content, widthPix, heightPix, logoBm, ZOOM);
    }

    public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, float zoom) {
        return createQRImage(content, widthPix, heightPix, logoBm, zoom, MARGIN);
    }
    /**
     *

     * @return
     */
    /**
     * @param content   类容
     * @param widthPix  二维码的宽度
     * @param heightPix 二维码的高度
     * @param logoBm    logo
     * @param zoom      logo是  widthPix  heightPix 大小比例    0<zoom<1
     * @param margin    二维码的白色边框  默认为4
     * @return
     */
    public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm, float zoom, int margin) {
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
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);

            int[] pixels = new int[widthPix * heightPix];
            if (logoBm != null) {
                Matrix m = new Matrix();
//				// 将logo图片按martix设置的信息缩放
                int logo_w = (int) (widthPix * (zoom >= 1 || zoom <= 0 ? 0.2 : zoom));//logo的显示宽度
                int logo_h = (int) (heightPix * (zoom >= 1 || zoom <= 0 ? 0.2 : zoom));//logo的显示高度
                m.setScale((float) logo_w / logoBm.getWidth(), (float) logo_h / logoBm.getHeight());// 设置缩放信息
                logoBm = Bitmap.createBitmap(logoBm, 0, 0, logoBm.getWidth(),
                        logoBm.getHeight(), m, false);
                int w_q = (widthPix - logo_w) / 2;//
                int h_s = (heightPix - logo_h) / 2;
                for (int y = 0; y < heightPix; y++) {
                    for (int x = 0; x < widthPix; x++) {
                        if (x > w_q && x < w_q + logo_w && y > h_s && y < h_s + logo_h) {
                            pixels[y * widthPix + x] = logoBm.getPixel(x - w_q, y - h_s);
                        } else {
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
        if (scanBitmap == null) return null;
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

        int[] pixels = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];//保存所有的像素的数组，图片宽×高
        scanBitmap.getPixels(pixels, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), pixels);


//		byte[] data = getYUV420sp(scanBitmap.getWidth(), scanBitmap.getHeight(), scanBitmap);
//		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
//				scanBitmap.getWidth(),
//				scanBitmap.getHeight(),
//				0, 0,
//				scanBitmap.getWidth(),
//				scanBitmap.getHeight(),
//				false);


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

    public static Result scanImage(Bitmap scanBitmap) {
        Result result = null;
        try {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(
                    2);
// 可以解析的编码类型
            Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
            if (decodeFormats.isEmpty()) {
                decodeFormats = new Vector<BarcodeFormat>();
                // 这里设置可扫描的类型，我这里选择了都支持
                decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
// 设置继续的字符编码格式为UTF8
            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
// 设置解析配置参数

            multiFormatReader.setHints(hints);
            result = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(
                    new BitmapLuminanceSource(scanBitmap))));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 开始对图像资源解码
     * @param picturePath
     * @return
     */
    public static Result scanImage(String picturePath) {
        Bitmap scanBitmap = BitmapFactory.decodeFile(picturePath);
        if (scanBitmap == null) return null;
        return scanImage(scanBitmap);
    }

    /**
     * 编码转换
     *
     * @param str s
     * @return s
     */
    public static String recode(String str) {
        String formart = "";
        try {
            boolean UTF_8 = Charset.forName("UTF-8").newEncoder()
                    .canEncode(str);
            if (UTF_8) return str;

            boolean GB2312 = Charset.forName("GB2312").newEncoder()
                    .canEncode(str);
            if (GB2312) {
                formart = new String(str.getBytes("GB2312"), "UTF-8");
                return formart;
            }

            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                return formart;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    /**
     * YUV420sp
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] getYUV420sp(int inputWidth, int inputHeight,
                                     Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];

        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    /**
     * RGB转YUV420sp
     *
     * @param yuv420sp inputWidth * inputHeight * 3 / 2
     * @param argb     inputWidth * inputHeight
     * @param width
     * @param height
     */
    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width,
                                       int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;

        // ---颜色数据---
//      int a, R, G, B;
        int R, G, B;
        //
        int argbIndex = 0;
        //

        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // a is not used obviously
//              a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                //
                argbIndex++;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                //
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                // NV21 has a plane of Y and interleaved planes of VU each
                // sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                // sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    //
                    yuv420sp[uvIndex++] = (byte) V;
                    //
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }
}