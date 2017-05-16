package com.liuwei1995.red.util;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类
 *
 * @author liuwei
 */
public class FileUtils {

    /**
     *  判断SD卡是否被挂载
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡的根目录
      * @return
     */
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡的完整空间大小，返回MB
      * @return
     */
    public static long getSDCardSize() {
        if (isSDCardMounted()) {
            StatFs fs = new StatFs(getSDCardBaseDir());
            long count = 0;
            long size = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                size = fs.getBlockSizeLong();
                count = fs.getBlockCountLong();
            }
            else
            {
                size = fs.getBlockSize();
                count = fs.getBlockCount();
            }
            return count * size / 1024 / 1024;
        }
        return 0;
    }

    /**
     * 获取SD卡的剩余空间大小
     * @return
     */
    public static long getSDCardFreeSize() {
        if (isSDCardMounted()) {
            StatFs fs = new StatFs(getSDCardBaseDir());
            long count = 0;
            long size = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                size = fs.getBlockSizeLong();
                count = fs.getBlockCountLong();
            }
            else
            {
                size = fs.getBlockSize();
                count = fs.getBlockCount();
            }
            return count * size / 1024 / 1024;
        }
        return 0;
    }

    /**
     * 获取SD卡的可用空间大小
     * @return
     */
    public static long getSDCardAvailableSize() {
        if (isSDCardMounted()) {
            StatFs fs = new StatFs(getSDCardBaseDir());
            long count = 0;
            long size = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                size = fs.getBlockSizeLong();
                count = fs.getBlockCountLong();
            }
            else
            {
                size = fs.getBlockSize();
                count = fs.getBlockCount();
            }
            return count * size / 1024 / 1024;
        }
        return 0;
    }

    /**
     * 往SD卡的公有目录下保存文件
     * @param data
     * @param type
     * @param fileName
     * @return
     */
    public static boolean saveFileToSDCardPublicDir(byte[] data, String type,
                                                    String fileName) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = Environment.getExternalStoragePublicDirectory(type);
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos != null)
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     *  往SD卡的自定义目录下保存文件
     * @param data
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean saveFileToSDCardCustomDir(byte[] data, String dir,
                                                    String fileName) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = new File(getSDCardBaseDir() + File.separator + dir);
            if (!file.exists()) {
                file.mkdirs();// 递归创建自定义目录
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos != null)
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 往SD卡的私有Files目录下保存文件
      * @param data
     * @param type
     * @param fileName
     * @param context
     * @return
     */
    public static boolean saveFileToSDCardPrivateFilesDir(byte[] data,
                                                          String type, String fileName, Context context) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = context.getExternalFilesDir(type);
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos != null)
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /***
     * 往SD卡的私有Cache目录下保存文件
      * @param data
     * @param fileName
     * @param context
     * @return
     */
    public static boolean saveFileToSDCardPrivateCacheDir(byte[] data,
                                                          String fileName, Context context) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = context.getExternalCacheDir();
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos != null)
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /***
     * 保存bitmap图片到SDCard的私有Cache目录
      * @param bitmap
     * @param fileName
     * @param context
     * @return
     */
    public static boolean saveBitmapToSDCardPrivateCacheDir(Bitmap bitmap,
                                                            String fileName, Context context) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            // 获取私有的Cache缓存目录
            File file = context.getExternalCacheDir();

            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                if (fileName != null
                        && (fileName.contains(".png") || fileName
                        .contains(".PNG"))) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /***
     * 从SD卡获取文件
      * @param fileDir
     * @return
     */
    public static byte[] loadFileFromSDCard(String fileDir) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            bis = new BufferedInputStream(
                    new FileInputStream(new File(fileDir)));
            byte[] buffer = new byte[8 * 1024];
            int c = 0;
            while ((c = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, c);
                baos.flush();
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(baos != null)
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /***
     * 从SDCard中寻找指定目录下的文件，返回Bitmap
      * @param filePath
     * @return
     */
    public Bitmap loadBitmapFromSDCard(String filePath) {
        byte[] data = loadFileFromSDCard(filePath);
        if (data != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bm != null) {
                return bm;
            }
        }
        return null;
    }

    /**
     * 获取SD卡公有目录的路径
     * @param type
     * @return
     */
    public static String getSDCardPublicDir(String type) {
        return Environment.getExternalStoragePublicDirectory(type).toString();
    }

    /**
     * 获取SD卡私有Cache目录的路径
     * @param context
     * @return
     */
    public static String getSDCardPrivateCacheDir(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 获取SD卡私有Files目录的路径
     * @param context
     * @param type
     * @return
     */
    public static String getSDCardPrivateFilesDir(Context context, String type) {
        return context.getExternalFilesDir(type).getAbsolutePath();
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }

    /**
     * 从sdcard中删除文件
     * @param filePath
     * @return
     */
    public static boolean removeFileFromSDCard(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                file.delete();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取当前程序路径
     * @param context
     * @return
     */
    public static String getAbsolutePath(@NonNull Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    /***
     * 获取该程序的安装包路径
     * @param context
     * @return
     */
    public static String getPackageResourcePath(@NonNull Context context){
        return  context.getPackageResourcePath();
    }

    /**
     * 获取程序默认数据库路径
     * @param context
     * @param databaseName
     * @return
     */
    public static String getAbsolutePath(@NonNull Context context,String databaseName){
        return  context.getDatabasePath(databaseName).getAbsolutePath();
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
    /**
     *
     * @param context 上下文
     * @param fileName  文件全名  包括后缀
     * @return  字节流数组
     */
    public synchronized static byte[] readFileFromAssetsByFileNameToBytes(Context context,String fileName) {
        byte[] res = null;
            if(context == null)return  res;
            //得到资源中的asset数据流
            try {
                InputStream in = context.getResources().getAssets().open(fileName);
                return readFileByInputStreamToBytes(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }

    public synchronized static byte[] readFileByInputStreamToBytes(InputStream in) {
        try {
            byte [] buffer = null;
            buffer = new byte[in.available()];
            in.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }




    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public static String getRealPath(Context mContext,Uri fileUrl){
        String fileName = null;
        if(fileUrl!= null){
            if (fileUrl.getScheme().toString().compareTo("content")==0)           //content://开头的uri
            {
                Cursor cursor = mContext.getContentResolver().query(fileUrl, null, null, null, null);
                if (cursor != null && cursor.moveToFirst())
                {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                    fileName = cursor.getString(column_index);          //取出文件路径
                    if(!fileName.startsWith("/mnt")){
//检查是否有”/mnt“前缀

                        fileName = "/mnt" + fileName;
                    }
                    cursor.close();
                }
            }else if (fileUrl.getScheme().compareTo("file")==0)         //file:///开头的uri
            {
                fileName = fileUrl.toString();
                fileName = fileUrl.toString().replace("file://", "");
//替换file://
                if(!fileName.startsWith("/mnt")){
//加上"/mnt"头
                    fileName += "/mnt";
                }
            }
        }
        return fileName;
    }
    public static String getImagePathFromUri(final Context context, Uri picUri) {
        // 选择的图片路径
        String selectPicPath = null;
        Uri selectPicUri = picUri;

        final String scheme = picUri.getScheme();
        if (picUri != null && scheme != null) {
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                // content://开头的uri
                Cursor cursor = context.getContentResolver().query(picUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 取出文件路径
                    selectPicPath = cursor.getString(columnIndex);

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!selectPicPath.startsWith("/storage") && !selectPicPath.startsWith("/mnt")) {
                        // 检查是否有"/mnt"前缀
                        selectPicPath = "/mnt" + selectPicPath;
                    }
                    //关闭游标
                    cursor.close();
                }
            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {// file:///开头的uri
                // 替换file://
                selectPicPath = selectPicUri.toString().replace("file://", "");
                int index = selectPicPath.indexOf("/sdcard");
                selectPicPath = index == -1 ? selectPicPath : selectPicPath.substring(index);
                if (!selectPicPath.startsWith("/mnt")) {
                    // 加上"/mnt"头
                    selectPicPath = "/mnt" + selectPicPath;
                }
            }
        }
        return selectPicPath;
    }
}
