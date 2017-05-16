package com.liuwei1995.red.util.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.webkit.DownloadListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by liuwei on 2017/4/7
 */

public class BaseDownloadListener implements DownloadListener {

    private Context mContext;
    private ProgressDialog mDialog;

    public BaseDownloadListener(Context context) {
        mContext = context;
    }
    private boolean isDownload = false;

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isDownload() {
        return isDownload;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Log.i("tag", "url="+url);
        Log.i("tag", "userAgent="+userAgent);
        Log.i("tag", "contentDisposition="+contentDisposition);
        Log.i("tag", "mimetype="+mimetype);
        Log.i("tag", "contentLength="+contentLength);
        if(isDownload){
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Toast t=Toast.makeText(mContext, "需要SD卡。", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }
            DownloaderTask task=new DownloaderTask();
            task.execute(url);
        }else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(intent);
        }
    }
    //内部类
    private class DownloaderTask extends AsyncTask<String, Void, String> {

        public DownloaderTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String url=params[0];
            String fileName=url.substring(url.lastIndexOf("/")+1);
            fileName= URLDecoder.decode(fileName);
            Log.i("tag", "fileName="+fileName);

            File directory = Environment.getExternalStorageDirectory();
            File file=new File(directory,fileName);
            if(file.exists()){
                Log.i("tag", "The file has already exists.");
                return fileName;
            }
            HttpURLConnection con = null;
            InputStream input = null;
            try {
                 con = (HttpURLConnection) new URL(url).openConnection();
                con.setConnectTimeout(30*1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                if(HttpURLConnection.HTTP_OK == con.getResponseCode()){
                     input = con.getInputStream();
                    writeToSDCard(fileName,input);
                    input.close();
                    return fileName;
                }else{
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
                if(input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if(con != null)
                con.disconnect();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            closeProgressDialog();
            if(result==null){
                Toast t=Toast.makeText(mContext, "连接错误！请稍后再试！", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }

            Toast t=Toast.makeText(mContext, "已保存到SD卡。", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            File directory=Environment.getExternalStorageDirectory();
            File file=new File(directory,result);
            Log.i("tag", "Path="+file.getAbsolutePath());

            Intent intent = getFileIntent(file);

            mContext.startActivity(intent);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    private void showProgressDialog(){

        if(mDialog==null){
            mDialog = new ProgressDialog(mContext);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mDialog.setMessage("正在加载 ，请等待...");
            mDialog.setIndeterminate(false);//设置进度条是否为不明确
            mDialog.setCancelable(true);//设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog=null;
                }
            });
            mDialog.show();

        }
    }
    private void closeProgressDialog(){
        if(mDialog!=null){
            mDialog.dismiss();
            mDialog=null;
        }
    }
    public Intent getFileIntent(File file){
//       Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
        Uri uri = Uri.fromFile(file);
        String type = getMIMEType(file);
        Log.i("tag", "type="+type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    public void writeToSDCard(String fileName,InputStream input){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File directory=Environment.getExternalStorageDirectory();
            File file=new File(directory,fileName);
//          if(file.exists()){
//              Log.i("tag", "The file has already exists.");
//              return;
//          }
            FileOutputStream fos = null;
            try {
                 fos = new FileOutputStream(file);
                byte[] b = new byte[2048];
                int j = 0;
                while ((j = input.read(b)) != -1) {
                    fos.write(b, 0, j);
                }
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            Log.i("tag", "NO SDCard.");
        }
    }

    private String getMIMEType(File f){
        String type="";
        String fName=f.getName();
      /* 取得扩展名 */
        String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if(end.equals("pdf")){
            type = "application/pdf";//
        }
        else if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            type = "audio/*";
        }
        else if(end.equals("3gp")||end.equals("mp4")){
            type = "video/*";
        }
        else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            type = "image/*";
        }
        else if(end.equals("apk")){
        /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
//      else if(end.equals("pptx")||end.equals("ppt")){
//        type = "application/vnd.ms-powerpoint";
//      }else if(end.equals("docx")||end.equals("doc")){
//        type = "application/vnd.ms-word";
//      }else if(end.equals("xlsx")||end.equals("xls")){
//        type = "application/vnd.ms-excel";
//      }
        else{
//        /*如果无法直接打开，就跳出软件列表给用户选择 */
            type="*/*";
        }
        return type;
    }
}
