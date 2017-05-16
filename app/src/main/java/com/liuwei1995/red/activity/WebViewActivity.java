package com.liuwei1995.red.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.liuwei1995.red.R;
import com.liuwei1995.red.util.permission.AndPermission;
import com.liuwei1995.red.util.webview.X5.BaseDownloadListener;
import com.liuwei1995.red.util.webview.X5.BaseWebChromeClient;
import com.liuwei1995.red.util.webview.X5.BaseWebSettings;
import com.liuwei1995.red.util.webview.X5.BaseWebViewClient;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class WebViewActivity extends BaseActivity {
    private static final String TAG = "WebViewActivity";
    private static final String APP_NAME_UA = "com.liuwei1995.red";

    private WebView mWebView;
    private String url = null;
    private ProgressBar pbProgress;

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        避免输入法界面弹出后遮挡输入光标的问题
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_web_view);
        setToolbar();
        setMenu(R.menu.menu_webview_activity);
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Uri data = intent.getData();
            if(data != null){
                url = data.toString();
            }else {
                Toast.makeText(this, "数据错误稍后重试", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }else {//com.android.browser.application_id
            for (String key : savedInstanceState.keySet()){
                Object o = savedInstanceState.get(key);
                LogUtils.d(TAG,o);
            }
            url = savedInstanceState.getString("url");
        }
        if (url == null || url.isEmpty() || ! Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(this, "数据错误稍后重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        mWebView = new WebView(getApplicationContext());
        LinearLayout mLinearLayout = $(R.id.activity_web_view);
        mLinearLayout.addView(mWebView);
        webSettings();
        AndPermission.with(this).setPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .start();
        mWebView.loadUrl(url);
    }
    public void webSettings(){
        mWebView.setDownloadListener(new BaseDownloadListener(this));
        mWebView.setWebChromeClient(new BaseWebChromeClient(this,mWebView){

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if(pbProgress != null)
                    if (newProgress == 100) {
                        // 网页加载完成
                        pbProgress.setVisibility(View.GONE);
                    } else {
                        if(pbProgress.getVisibility() != View.VISIBLE)
                            pbProgress.setVisibility(View.VISIBLE);
                        // 加载中
                        pbProgress.setProgress(newProgress);
                    }
                super.onProgressChanged(webView, newProgress);
            }
            @Override
            public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
            }
        });
        mWebView.setWebViewClient(new BaseWebViewClient(this){
            @Override
            public void onReceivedLoginRequest(WebView webView, String s, String s1, String s2) {
                super.onReceivedLoginRequest(webView, s, s1, s2);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                String title = webView.getTitle();
                setToolbarTitle(title);
            }
        });

        BaseWebSettings.WebSettings(this,mWebView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        int activitySize = AppManager.getAppManager().getActivitySize();
        if(activitySize <= 1){
            startActivity(new Intent(this,MainActivity.class));
        }
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.refresh_interface){
            mWebView.reload();
        }else {
            finish();
        }
        return super.onMenuItemClick(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == -1){
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            //设置 缓存模式  取本地缓存
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            mWebView.goBack();//返回上个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
