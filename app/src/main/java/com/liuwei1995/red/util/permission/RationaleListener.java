package com.liuwei1995.red.util.permission;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by dell on 2017/5/3
 */

public interface RationaleListener {
    /**
     * Go request permission.
     */
     void resume();

    /**
     * Cancel the operation.
     */
     void cancel();


     void showSettingDialog(@NonNull Context context, @NonNull RationaleListener rationale);

     void settingDialogConfirmCallBack(@NonNull RationaleListener rationale);


}
