package com.liuwei1995.red.activity;

import android.content.res.AssetManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by dell on 2017/4/26
 */

public class TestActivity {
    public void s() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");

// 获取 gDefault 这个字段, 想办法替换它
        Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);
        Object gDefault = gDefaultField.get(null);

// 4.x以上的gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的字段
        Class<?> singleton = Class.forName("android.util.Singleton");
        Field mInstanceField = singleton.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

// ActivityManagerNative 的gDefault对象里面原始的 IActivityManager对象
        Object rawIActivityManager = mInstanceField.get(gDefault);

// 创建一个这个对象的代理对象, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { iActivityManagerInterface }, new IActivityManagerHandler(rawIActivityManager) {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                });
        mInstanceField.set(gDefault, proxy);
    }

    private abstract class IActivityManagerHandler implements InvocationHandler {

        public IActivityManagerHandler(Object rawIActivityManager) {
        }

    }

    AssetManager mAssetManager = null;
//    public void getres(String mDexPath){
//        try {
//            AssetManager assetManager = AssetManager.class.newInstance();
//            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
//            addAssetPath.invoke(assetManager, mDexPath);
//            mAssetManager = assetManager;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Resources superRes = super.getResources();
//        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
//                superRes.getConfiguration());
//    }
}
