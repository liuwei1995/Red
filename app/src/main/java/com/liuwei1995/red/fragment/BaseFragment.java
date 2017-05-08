package com.liuwei1995.red.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by liuwei on 2017/3/21
 * <li>要实现延迟加载Fragment内容,需要在 onCreateView
 * isPrepared = true;</li>
 */

public class BaseFragment extends Fragment implements View.OnClickListener{
    public boolean isPause = false;
    public long time = 0;

    /**
     * 查找view的空间id
     * @param view
     * @param viewID
     * @param <T>
     * @return
     */
    public <T extends View> T $(View view,int viewID) {
        return (T) view.findViewById(viewID);
    }
    public void setOnClickListener(View ...views){
        if(views != null)
        for (int i = 0; i < views.length; i++) {
            views[i].setOnClickListener(this);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    public boolean isVisible = true;
    /**
     * 是否第一次加载
     */
    public boolean isFirstLoad = true;
    /**
     * 标志位，View已经初始化完成。
     * 2016/04/29
     * 用isAdded()属性代替
     * 2016/05/03
     * isPrepared还是准一些,isAdded有可能出现onCreateView没走完但是isAdded了
     */
    public boolean isPrepared;

    private static final String TAG = "BaseFragment";

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Log.e(TAG, "setUserVisibleHint: "+super.toString()+"=============="+isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        time = System.currentTimeMillis();
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            isPrepared = arguments.getBoolean("isPrepared", false);
        }

    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            //if (!isAdded() || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        initData();
    }

    protected void initData() {
    }

    @Override
    public void onClick(View v) {

    }
}
