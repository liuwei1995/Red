package com.liuwei1995.red.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuwei1995.red.R;


/**
 * Created by liuwei on 2017/4/25
 */

public abstract class RedPopupWindow extends PopupWindow implements View.OnClickListener,PopupWindow.OnDismissListener{

    protected  View mContentView;

    public RedPopupWindow(Context context, View contentView) {
        super(context);
        this.mContentView = contentView;
        setContentView(context,mContentView);
        //设置按钮监听
        //设置SelectPicPopupWindow的View
        if(mContentView == null)mContentView = new RelativeLayout(context);
        this.setContentView(contentView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //点击外面不消失
        this.setOutsideTouchable(true);
        this.setTouchable(true);


        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOnDismissListener(this);
    }
    public RedPopupWindow(@NonNull Context context, @LayoutRes int itemLayoutId){
        this(context,((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(itemLayoutId, null));
    }

    public  void setContentView(Context context,View contentView){

    }
    protected <T extends View> T getView(@IdRes int id){
        if(mContentView != null)return (T) mContentView.findViewById(id);
        return null;
    }
    protected <T extends TextView> void setText(@IdRes int id,@StringRes int resid){
        TextView view = getView(id);
        if (view != null)
            view.setText(resid);
    }
    protected <T extends TextView> void setText(@IdRes int id,CharSequence text){
        TextView view = getView(id);
        if (view != null)
            view.setText(text);
    }
    protected  void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener){
        View view = getView(id);
        if (view != null)
            view.setOnClickListener(onClickListener);
    }
    /**
     * 自定义的显示popupWindow
     *<li>showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);</li>
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
        } else {
            this.dismiss();
        }
    }
    public void showAtLocation(View parent){
        showAtLocation(parent,Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDismiss() {

    }
}
