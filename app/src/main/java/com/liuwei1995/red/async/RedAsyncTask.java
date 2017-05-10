package com.liuwei1995.red.async;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by dell on 2017/5/10
 */

public abstract class RedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected Context mContext;

    public RedAsyncTask(Context context) {
        super();
        this.mContext = context;
    }


    /**
     * 运行在UI线程中，在调用doInBackground()之前执行
     */
    @Override
    protected void onPreExecute() {
         onPreExecute(mContext);
    }
    /**
     * 运行在UI线程中，在调用doInBackground()之前执行
     */
    protected void onPreExecute(Context context) {

    }


    @Override
    protected Result doInBackground(Params... params) {
        return doInBackground(mContext,params);
    }


    public abstract Result doInBackground(Context context, Params... params);

    @Override
    protected void onPostExecute(Result result) {
        onPostExecute(mContext,result);
    }
    /**
     * 运行在ui线程中，在doInBackground()执行完毕后执行
     * @param context c
     * @param result r
     */
    protected void onPostExecute(Context context,Result result){

    }

    /**
     * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
     */
    @Override
    protected void onProgressUpdate(Object ...values) {
        onProgressUpdate(mContext,values);
    }
    /**
     * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
     */
    protected void onProgressUpdate(Context context,Object ...values) {

    }


}
