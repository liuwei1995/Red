package com.liuwei1995.red.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liuwei1995.red.R;
import com.liuwei1995.red.service.DesktopViewService;

/**
 * Created by liuwei on 2017/8/4 16:02
 */

public class AlerDesktopViewActivity extends Activity implements View.OnClickListener{

    public static void startActivity(Context context) {
        Intent intent = new Intent(context,AlerDesktopViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private EditText et_content;
    private Button btn_verify;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aler_desktop_view_activity);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(et_content.getText().toString().trim())){
            synchronized (this){
                if (!TextUtils.isEmpty(et_content.getText().toString().trim())){
                    String trim = et_content.getText().toString().trim();
                    Intent intent = new Intent(DesktopViewService.ACTION_DESKTOP_VIEW_SERVICE_RECEIVER_UPDATE);
                    intent.putExtra(DesktopViewService.TV_CONTENT_TXT_KEY,trim);
                    sendBroadcast(intent);
                    et_content.setText("");
                    finish();
                }
            }
        }
    }
}
