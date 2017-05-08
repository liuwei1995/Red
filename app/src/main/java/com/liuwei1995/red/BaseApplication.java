package com.liuwei1995.red;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.liuwei1995.red.entity.AppEntity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linxins on 17-4-15
 */

public class BaseApplication extends Application {

    public static  String versionName;
    public static  int     versionCode;
    private static final String TAG = "BaseApplication";

    /**
     * 版本号
     */
    public static final Map<String, AppEntity> WeChat_map = new HashMap<>();
    public static final Map<String, AppEntity> QQ_map = new HashMap<>();

    public static final String WeChat = "WeChat";
    public static final String QQ = "QQ";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtils.getInstance().init(this);
        Utils.init(this);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        CrashHandler.getInstance().init(this,true);
        try {
            byte[] bytes = FileUtils.readAssets2Bytes(this, "WeChat_QQ.json");
            String s = new String(bytes);
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                AppEntity appEntity = new AppEntity();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                appEntity.setName(name);
                String versionName = jsonObject.getString("VersionName");
                appEntity.setVersionName(versionName);
                int versionCode = jsonObject.getInt("versionCode");
                appEntity.setVersionCode(versionCode);
                String url = jsonObject.getString("url");
                appEntity.setUrl(url);
                if(WeChat.equals(name)){
                    WeChat_map.put(versionName,appEntity);
                }else if (QQ.equals(name)){
                    QQ_map.put(versionName,appEntity);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
//        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                Log.d(TAG, "onActivityCreated: ");
//            }
//            @Override
//            public void onActivityStarted(Activity activity) {
//                Log.d(TAG, "onActivityStarted: ");
//            }
//            @Override
//            public void onActivityResumed(Activity activity) {
//                Log.d(TAG, "onActivityResumed: ");
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//                Log.d(TAG, "onActivityPaused: ");
//            }
//            @Override
//            public void onActivityStopped(Activity activity) {
//                Log.d(TAG, "onActivityStopped: ");
//            }
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                Log.d(TAG, "onActivitySaveInstanceState: ");
//            }
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//                Log.d(TAG, "onActivityDestroyed: ");
//            }
//        });
        initImageLoader(this);
    }
    /**
     * 配置imageLoader
     * @param applicationContext
     */
    private void initImageLoader(Context applicationContext) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

       /* ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        // Initialize ImageLoader with configuration.  */
    }

}
