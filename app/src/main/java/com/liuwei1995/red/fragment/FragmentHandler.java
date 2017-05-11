package com.liuwei1995.red.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * Created by liuwei on 2017/5/11
 */

public class FragmentHandler extends Handler {

    private FragmentHandlerInterface mFragmentHandlerInterface;

    public FragmentHandler(@NonNull FragmentHandlerInterface mFragmentHandlerInterface) {
        this.mFragmentHandlerInterface = mFragmentHandlerInterface;
    }

    @Override
    public void handleMessage(Message msg) {
        if(mFragmentHandlerInterface != null)
        mFragmentHandlerInterface.handleMessage(msg);
    }
}
