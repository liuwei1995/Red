package com.liuwei1995.red.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * Created by liuwei on 2017/5/11
 */

public abstract class RedSnackbar implements View.OnClickListener{

    public static final int RED = 0xfff44336;
    public static final int GREEN = 0xff4caf50;
    public static final int BLUE = 0xff2195f3;
    public static final int ORANGE = 0xffffc107;
    public static final int WHITE  = 0xf6f5f4;//白色

    private View customView;

    private Context mContext;

    public RedSnackbar(Context mContext,@LayoutRes int layoutId) {
        this(mContext,LayoutInflater.from(mContext).inflate(layoutId, null));
    }
    public RedSnackbar(Context mContext,@NonNull View customView) {
        this.mContext = mContext;
        this.customView = customView;
        setCustomView(mContext,customView);
    }
    private Snackbar mSnackbar;

    private @ColorInt int color = ORANGE;

    public RedSnackbar setBackgroundColor(@ColorInt int color){
        this.color = color;
        return this;
    }
    public synchronized void make(@NonNull View view,@IntRange(from = 500) int duration){
        mSnackbar = Snackbar.make(view, "", duration);

        View actionView = mSnackbar.getView();
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout)actionView;
        int childCount = snackbarLayout.getChildCount();
        if(childCount > 0){
            View childAt = snackbarLayout.getChildAt(0);
            childAt.setVisibility(View.GONE);
        }
        actionView.setBackgroundColor(color);
        synchronized (this){
            ViewParent pare = customView.getParent();
            if(pare != null)
            if(pare instanceof Snackbar.SnackbarLayout){
                Snackbar.SnackbarLayout parent = (Snackbar.SnackbarLayout) pare;
                    parent.removeView(customView);
            }else if(pare instanceof ViewGroup){
                ((ViewGroup)pare).removeView(customView);
            }
        }
        snackbarLayout.addView(customView);
        mSnackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                ((Snackbar.SnackbarLayout)transientBottomBar.getView()).removeView(customView);
                if(callback != null)
                callback.onDismissed(transientBottomBar,event);
            }
            @Override
            public void onShown(Snackbar sb) {
                if(callback != null)
                    callback.onShown(sb);
            }
        });
        mSnackbar.show();
    }
    public synchronized void dismiss(){
        if(mSnackbar != null && mSnackbar.isShown()){
            mSnackbar.dismiss();
        }
    }
    public boolean isShown(){
        return mSnackbar != null && mSnackbar.isShown();
    }
    public void setCustomView(Context mContext,View customView){

    }
    public <T extends View> T getView(@IdRes int id){
        if(customView != null)
            return (T) customView.findViewById(id);
        return null;
    }

    public <T extends View> RedSnackbar setText(@IdRes int id,CharSequence text){
        TextView view = getView(id);
        if(view != null)view.setText(text);
        return this;
    }
    public <T extends View> RedSnackbar setText(@IdRes int id,@StringRes int resid){
        TextView view = getView(id);
        if(view != null)view.setText(resid);
        return this;
    }
    public RedSnackbar setOnClickListener(@IdRes int id){
        View view = getView(id);
        if(view != null){
            view.setOnClickListener(this);
        }
        return this;
    }
    public RedSnackbar setOnClickListener(@IdRes int id,View.OnClickListener onClick){
        View view = getView(id);
        if(view != null){
            view.setOnClickListener(onClick);
        }
        return this;
    }

    @Override
    public void onClick(View v) {

    }

    private Callback callback;

    @NonNull
    public RedSnackbar addCallback(@NonNull Callback callback) {
        this.callback = callback;
        return  this;
    }

    public static class Callback  {

        public void onShown(Snackbar sb) {
            // Stub implementation to make API check happy.
        }

        public void onDismissed(Snackbar transientBottomBar, @BaseTransientBottomBar.BaseCallback.DismissEvent int event) {
            // Stub implementation to make API check happy.
        }
    }

}
