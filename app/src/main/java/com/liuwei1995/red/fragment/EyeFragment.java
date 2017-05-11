package com.liuwei1995.red.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuwei1995.red.fragment.presenter.EyeFragmentPresenter;


/**
 * Created by dell on 2017/5/5
 */

public class EyeFragment extends BaseFragment<EyeFragmentPresenter>{


    private static final String TAG = EyeFragment.class.getSimpleName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ACCOUNT = "account";
    public static final String TYPE = "type";

    public EyeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EyeFragment newInstance(int type,String account,boolean isPrepared) {
        EyeFragment fragment = new EyeFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        if(account != null)
        args.putString(ACCOUNT,account);
        args.putBoolean("isPrepared",isPrepared);
        fragment.setArguments(args);
        return fragment;
    }


    public EyeFragmentPresenter getPresenter() {
        return new EyeFragmentPresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);//必须重写父类的方法
        presenter.onAttach(context,this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = presenter.onCreateView(inflater, container, savedInstanceState);
        if (!isPrepared){
            initData();
        }
        return rootView;
    }
    @Override
    protected void initData() {
        presenter.initData();
    }

    @Override
    protected void onInvisible() {
        if(presenter != null)
        presenter.onInvisible();
    }

    @Override
    public void onClick(View v) {
        presenter.onClick(v);
    }


    @Override
    public void onDetach() {
        presenter.onDetach();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
