package com.liuwei1995.red.view;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by liuwei on 2017/5/9
 */

public class RedDialog extends Dialog {

    public RedDialog(Context context) {
        super(context);
    }

    public RedDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RedDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
