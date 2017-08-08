package com.liuwei1995.red.util.permission.target;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by liuwei on 2017/5/3
 */

public interface Target {

    Context getContext();

    boolean shouldShowRationalePermissions(@NonNull String... permissions);

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);
}
