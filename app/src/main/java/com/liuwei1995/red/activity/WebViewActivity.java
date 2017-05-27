package com.liuwei1995.red.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.liuwei1995.red.R;
import com.liuwei1995.red.util.permission.AndPermission;
import com.liuwei1995.red.util.webview.BaseDownloadListener;
import com.liuwei1995.red.util.webview.BaseWebChromeClient;
import com.liuwei1995.red.util.webview.BaseWebSettings;
import com.liuwei1995.red.util.webview.BaseWebViewClient;
import com.liuwei1995.red.util.webview.ItemLongClickedPopWindow;
import com.liuwei1995.red.view.RedPopupWindow;

import static com.blankj.utilcode.util.ClipboardUtils.getIntent;

//import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;

//import com.tencent.smtt.sdk.WebSettings;
//import com.tencent.smtt.sdk.WebView;

//import com.liuwei1995.red.util.webview.X5.BaseDownloadListener;
//import com.liuwei1995.red.util.webview.X5.BaseWebChromeClient;
//import com.liuwei1995.red.util.webview.X5.BaseWebSettings;
//import com.liuwei1995.red.util.webview.X5.BaseWebViewClient;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mWebView != null){
            url = intent.getStringExtra("url");
            if (TextUtils.isEmpty(url)) {
                Uri data = intent.getData();
                if (data != null){
                    url = data.toString();
                }
            }
            mWebView.loadUrl(url);
        }
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
        if (savedInstanceState != null){
            url = savedInstanceState.getString("url");
        }
        if (TextUtils.isEmpty(url)) {
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data != null){
                url = data.toString();
            }
        }
        if (url == null || url.isEmpty() || ! Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(this, "数据错误稍后重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        mWebView = new WebView(getApplicationContext());
       final LinearLayout mLinearLayout = $(R.id.activity_web_view);
        mLinearLayout.addView(mWebView);
        webSettings();
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();

                if (null == result)
                    return false;
                int type = result.getType();
                /**
                 * WebView.HitTestResult.UNKNOWN_TYPE    未知类型
                 WebView.HitTestResult.PHONE_TYPE    电话类型
                 WebView.HitTestResult.EMAIL_TYPE    电子邮件类型
                 WebView.HitTestResult.GEO_TYPE    地图类型
                 WebView.HitTestResult.SRC_ANCHOR_TYPE    超链接类型
                 WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE    带有链接的图片类型
                 WebView.HitTestResult.IMAGE_TYPE    单纯的图片类型
                 WebView.HitTestResult.EDIT_TEXT_TYPE    选中的文字类型
                 */
                String imgurl = result.getExtra();


                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                    //let TextViewhandles context menu return true;
                }
                // Setup custom handlingdepending on the type
                switch (type) {
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // TODO
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        // Log.d(DEG_TAG, "超链接");
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        imgurl = result.getExtra();
                        RedPopupWindow healthyPopupWindow = new RedPopupWindow(WebViewActivity.this, R.layout.list_item_longclicked_img){
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
                                        dismiss();
                                    }
                                });
                            }
                        };
                        healthyPopupWindow.showAtLocation(mLinearLayout,
                                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                        //通过GestureDetector获取按下的位置，来定位PopWindow显示的位置
//                        itemLongClickedPopWindow.showAtLocation(v,Gravity.TOP| Gravity.LEFT, v.get, downY + 10);
//                        itemLongClickedPopWindow.showAtLocation(v,Gravity.TOP| Gravity.LEFT, downX, downY + 10);
//                        itemLongClickedPopWindow.showAtLocation();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        AndPermission.with(this).setPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .start();
        mWebView.loadUrl(url);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = ScreenUtils.getScreenHeight();
        final int screenWidth = ScreenUtils.getScreenWidth();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
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
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

//            @Override
//            public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
//                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
//            }
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
