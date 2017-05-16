package com.liuwei1995.red.util.webview.X5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;

import com.liuwei1995.red.BaseApplication;
import com.liuwei1995.red.R;
import com.liuwei1995.red.util.FileUtils;
import com.liuwei1995.red.util.MD5Util;
import com.liuwei1995.red.util.SharedPreferencesUtil;
import com.liuwei1995.red.view.RedPopupWindow;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by liuwei on 2017/4/6
 */

public class BaseWebViewClient extends WebViewClient {

    private Context mContext;

    public BaseWebViewClient(Context context) {
        mContext = context;
    }

    private static final String TAG = "BaseWebViewClient";
    private boolean onPageStarted = false;
    private String loadUrl = null;

    public void setCallLoading(boolean isLoading) {

    }

    public void setCallError(boolean isError) {

    }

    public void setCallToolbarTitle(String title) {

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        loadUrl = url;
        onPageStarted = true;
        isError = false;
        setCallLoading(true);
        setCallError(false);
    }


    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
        sslErrorHandler.proceed();
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        setCallLoading(false);
        int errorCode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            errorCode = error.getErrorCode();
            if (errorCode == -6 || errorCode == -10) return;
        }
        isError = true;
        setCallError(true);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        setCallLoading(false);
        if (errorCode == -6 || errorCode == -10) return;
        isError = true;
        setCallError(true);
    }

    private boolean isError = false;

    @Override
    public void onPageFinished(WebView view, String url) {
        loadUrl = null;
        onPageStarted = false;
        if (!isError) {
            String title = view.getTitle();
            setCallToolbarTitle(title);
            setCallError(false);
            setCallLoading(false);
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            SharedPreferencesUtil.saveString(mContext, CookieStr, "cookies", url);
        } else {
            setCallToolbarTitle("");
        }
        super.onPageFinished(view, url);
    }
    private View mainLayout;

    /**
     * 
     * @param mainLayout
     */
    public void setMainLayout(View mainLayout) {
        this.mainLayout = mainLayout;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest, Bundle bundle) {
        return super.shouldInterceptRequest(webView, webResourceRequest, bundle);
    }

//
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        if (BaseApplication.NETWORK_IS_AVAILABLE) {
//            //不使用缓存：
//            view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
//        } else {
//            //优先使用缓存：
//            view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            String url = request.getUrl().toString();
//            if (!Patterns.WEB_URL.matcher(url).matches()) {
//                showPopupWindow(url);
//                return true;
//            }
//        }
//        return super.shouldOverrideUrlLoading(view, request);
//
//    }
    private void showPopupWindow(String url){
        final Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if(scheme != null && !TextUtils.isEmpty(scheme)){
            String host = uri.getHost();
            if(host != null && !TextUtils.isEmpty(host)){
                PackageManager pm = mContext.getPackageManager();
                boolean installed = false;
                try {
                    pm.getPackageInfo(host, PackageManager.GET_ACTIVITIES);
                    installed = true;
                } catch (PackageManager.NameNotFoundException e) {
                    installed = false;
                }
                if(installed)
                if(mainLayout != null){
                    RedPopupWindow healthyPopupWindow = new RedPopupWindow(mContext, R.layout.layout_select_pic_pop){
                        @Override
                        public void setContentView(Context context, View contentView) {
                            contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //销毁弹出框
                                    dismiss();
                                }
                            });
                            contentView.findViewById(R.id.tvAlbum).setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                }
                            });
                        }
                    };
                    healthyPopupWindow.showAtLocation(mainLayout,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        }
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView webview, String url) {
        //网络请求
        if (BaseApplication.NETWORK_IS_AVAILABLE) {
            //不使用缓存：
            webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        } else {
            //优先使用缓存：
            webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        }
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            showPopupWindow(url);
            return true;
        }
        return super.shouldOverrideUrlLoading(webview, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse response = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String url = request.getUrl().toString();
            response = getWebResourceResponse(url);
            return response;
        }
        return super.shouldInterceptRequest(view, request);
    }

    private WebResourceResponse getWebResourceResponse(String url) {
        WebResourceResponse response = null;
        try {
            if (url.endsWith(".png") || url.endsWith(".PNG")) {
                response = new WebResourceResponse("image/png", "UTF-8", new FileInputStream(new File(getJSPath(".png"), MD5Util.getStringMD5(url) + ".png")));
            }
//                    else if (url.endsWith(".gif")) {
//                        response = new WebResourceResponse("image/gif", "UTF-8", new FileInputStream(new File(getJSPath(".gif") ,MD5Util.getStringMD5(url) + ".gif")));
//                    }

            else if (url.endsWith(".jpg") || url.endsWith(".JPG")) {
                response = new WebResourceResponse("image/jpeg", "UTF-8", new FileInputStream(new File(getJSPath(".jpg"), MD5Util.getStringMD5(url) + ".jpg")));
//                        https://t11.baidu.com/it/u=3502031116,3125523297&fm=170&s=F5A5B854CC130ED2140C49960300108B&w=218&h=146&img.JPEG
            } else if (url.endsWith(".jpeg") || url.endsWith(".JPEG")) {
                response = new WebResourceResponse("image/jpeg", "UTF-8", new FileInputStream(new File(getJSPath(".jpeg"), MD5Util.getStringMD5(url) + ".jepg")));
            } else {
//                        if (".js".endsWith(url)) {
//                            response = new WebResourceResponse("text/javascript", "UTF-8", new FileInputStream(new File(getJSPath(".js"),MD5Util.getStringMD5(url) + ".js")));
//                        } else if (url.endsWith(".css")) {
//                            response = new WebResourceResponse("text/css", "UTF-8", new FileInputStream(new File(getJSPath(".css") ,MD5Util.getStringMD5(url) + ".css")));
//                        } else if (url.endsWith(".html")) {
//                            response = new WebResourceResponse("text/html", "UTF-8", new FileInputStream(new File(getJSPath(".html") ,MD5Util.getStringMD5(url) + ".html")));
//                        }
            }
            if (response != null) {
                Log.e(TAG, "getWebResourceResponse: 获取的是本地的文件" + url);
            }
        } catch (FileNotFoundException e) {
//                    try {
//                        response = new WebResourceResponse("image/png", "UTF-8", getResources().getAssets().open("app_cion.png"));
//                    } catch (FileNotFoundException e1) {
//                        e1.printStackTrace();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//            TODO  这里可以下载图片
//            Intent intent = new Intent(mContext, DownloadHtmlService.class);
//            intent.putExtra("url", url);
//            intent.setAction(DownloadHtmlService.class.getName());
//            mContext.startService(intent);
        }
        return response;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return getWebResourceResponse(url);
    }

    private String path = null;

    private File getJSPath(String name) {
        if (path == null) {
            path = FileUtils.getExtSDCardPath(mContext);
            if (path == null) {
                path = FileUtils.getInnerSDCardPath();
            }
        }
        if (path != null && !TextUtils.isEmpty(path)) {
            File file = new File(path + "/" + mContext.getPackageName(), name);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
                Log.e(TAG, "getJSPath: " + "=========file  不存在==" + file.getPath());
            }
            return file;
        }
        return null;
    }

}
