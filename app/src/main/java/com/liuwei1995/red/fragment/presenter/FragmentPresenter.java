package com.liuwei1995.red.fragment.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuwei1995.red.fragment.iview.FragmentIView;

/**
 * Created by liuwei on 2017/5/11
 */

public abstract class FragmentPresenter implements FragmentIView {

    protected Context mContext;
    protected Fragment mFragment;

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroyView() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onDetach() {
    }

    @Override
    public void onAttach(Activity activity) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
    }

    @Override
    public void onClick(View v) {

    }

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
}
