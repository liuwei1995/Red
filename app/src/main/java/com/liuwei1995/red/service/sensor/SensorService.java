package com.liuwei1995.red.service.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.liuwei1995.red.MainActivity;


public class SensorService extends Service implements SensorEventListener {
    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**单次有效计步*/
    private Sensor mStepCount;
    /**系统计步累加值*/
    private  Sensor  mStepDetector;
    /**系统服务*/
    private SensorManager mSensorManager;
    private Intent intent = new Intent(MainActivity.ACTION_SensorServiceReceiver_CHANGE);
    @Override
    public void onCreate() {
        super.onCreate();
//        使用传感器之前首先获取SensorManager通过系统服务获取：
         mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        2、获取我们需要的传感器类型：
//单次有效计步
         mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//系统计步累加值
          mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//        3、注册监听者（监听传感器事件）
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSensorManager != null){
            //        PS：取消注册：
            if(mStepDetector != null)
                mSensorManager.unregisterListener(this, mStepDetector);
            if(mStepCount != null)
                mSensorManager.unregisterListener(this, mStepCount);
        }
    }

    private static final String TAG = "SensorService";
   public StringBuilder sb = new StringBuilder();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        sb.delete(0,sb.length());
        if (values.length > 0){
            sb.append(values[0]);
            intent.putExtra("data",event.sensor.getStringType()+"\n\t"+values[0]);
            sendBroadcast(intent);
        }
        Log.e(TAG, "onSensorChanged: "+event.sensor.toString()+"\n\tvalues:"+sb.toString());
//        if (event.sensor.getType() == sensorTypeC) {
//
//            //event.values[0]为计步历史累加值
//
////            tvAllCount.setText(event.values[0] + "步");
//
//        }
//
//        if (event.sensor.getType() == sensorTypeD) {
//
//            if (event.values[0] == 1.0) {
//
//                mDetector++;
//
//                //event.values[0]一次有效计步数据
//
//                tvTempCount.setText(mDetector + "步");
//
//            }
//
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
