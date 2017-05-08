package com.liuwei1995.red.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by liuwei on 2016/7/19
 */
public class RedViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;
    private final ImageLoader imageLoader;
    private int viewType;

    protected RedViewHolder(View rootView, int viewType) {
        super(rootView);
        this.mViews = new SparseArray<View>();
        mConvertView = rootView;
        mConvertView.setTag(this);
        this.viewType = viewType;
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId id
     * @return T
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param parent parent
     * @param viewType viewType
     * @param mItemLayoutId i
     * @return h
     */
    public RedViewHolder get(Context context, ViewGroup parent, int viewType, int mItemLayoutId) {
        if (mConvertView == null) {
            View view = LayoutInflater.from(context).inflate(mItemLayoutId, parent, false);
            return new RedViewHolder(view, viewType);
        }
        return (RedViewHolder) mConvertView.getTag();
    }


    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public RedViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId viewId
     * @param drawableId drawableId
     * @return this
     */
    public RedViewHolder setImageResource(@IdRes int viewId, @DrawableRes int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId viewId
     * @param bm bm
     * @return
     */
    public RedViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * @param viewId viewId
     * @param uri uri
     * @return
     */
    public RedViewHolder setImageByUrl(int viewId, String uri) {
        imageLoader.displayImage(uri, (ImageView) getView(viewId));
        return this;
    }

    public void stop() {
        imageLoader.stop();
    }

    public void resume() {
        imageLoader.resume();
    }

    public void destroy() {
        imageLoader.destroy();
    }

}
