package com.liuwei1995.red.zxing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.QRCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.EyeActivity;
import com.liuwei1995.red.view.RedAlertDialogV7;
import com.liuwei1995.red.zxing.camera.CameraManager;
import com.liuwei1995.red.zxing.decoding.CaptureActivityHandler;
import com.liuwei1995.red.zxing.decoding.InactivityTimer;
import com.liuwei1995.red.zxing.view.ViewfinderView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;
    private TextView tv_camera_switch;
    private Camera camera;
    private Parameters parameter;
    private android.view.View parent;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        parent = findViewById(R.id.fl_capture);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        findViewById(R.id.btn_cancel_scan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
        this.findViewById(R.id.tv_go_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
        this.findViewById(R.id.btn_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* 开启Pictures画面Type设定为image */
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                /* 使用Intent.ACTION_GET_CONTENT这个Action */
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT);
//                Bitmap qrImage = QRCodeUtil.createQRImage("http://m.blog.csdn.net/article/details?id=52223638", 200, 200, null);
//                Result result = QRCodeUtil.scanningImage(qrImage);
//                if(result != null){
//                    Toast.makeText(CaptureActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
//                }
            }
        });
        hasSurface = false;
        tv_camera_switch = (TextView) this.findViewById(R.id.tv_camera_switch);
        inactivityTimer = new InactivityTimer(this);
    }

    public static final int RESULT = 200;

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            /**
             * SURFACE_TYPE_PUSH_BUFFERS表明该Surface不包含原生数据，Surface用到的数据由其他对象提供，
             * 在Camera图像预览中就使用该类型的Surface，有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。
             */
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        //电筒开关
        tv_camera_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = tv_camera_switch.getText().toString().trim();
                if ("打开".equals(trim)) {
                    tv_camera_switch.setText("关闭");
                    camera.startPreview();
                    parameter = camera.getParameters();

                    parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(parameter);
                } else if ("关闭".equals(trim)) {
                    tv_camera_switch.setText("打开");
                    parameter = camera.getParameters();

                    parameter.setFlashMode(Parameters.FLASH_MODE_OFF);

                    camera.setParameters(parameter);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT){
            if(data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if(cursor != null){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Result result = QRCodeUtil.scanningImage(picturePath);
                    if(result != null){
                        String text = result.getText();
                        show(text);
                    }else {
                        Toast.makeText(this, "识别失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    public synchronized void show(final String resultString){
        RedAlertDialogV7 red = new RedAlertDialogV7(this,R.layout.pop_capture_activity_b,R.style.AnimBottom) {
            @Override
            protected void convertView(View view) {
                setText(R.id.tv_content,resultString);
                setOnClickListener(R.id.btn_open_app,this);
                setOnClickListener(R.id.btn_cancel,this);
            }

            @Override
            public void onClick(View v) {
                dismiss();
                if (v.getId() == R.id.btn_cancel){

                }else {
                    if (URLUtil.isNetworkUrl(resultString)){
                        if (resultString.trim().contains("http://ofo.so") || resultString.trim().contains("https://ofo.so")){
                            String s = resultString;
                            s = s.replace("http://ofo.so","").replace("https://ofo.so","");
                            int i = s.lastIndexOf("/");
                            if(i != -1 && i < s.length()){
                                s = s.substring(i+1);
                                EyeActivity.newStartActivity(CaptureActivity.this,1,s);
                                finish();
                            }else {
                                Toast.makeText(CaptureActivity.this, "没有支持的应用", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultString));
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Toast.makeText(CaptureActivity.this, "没有支持的应用", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onDismiss(DialogInterface dialog) {
                continuePreview();
            }
        };
        red.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 判断是否有浏览器
     *
     * @param context
     * @return
     */
    private boolean hasBrowser(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://"));
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        final int size = (list == null) ? 0 : list.size();
        return size > 0;
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else if (URLUtil.isNetworkUrl(resultString)) {
            if (!hasBrowser(this)) {
                Toast.makeText(CaptureActivity.this, "没有应用程序可打开", Toast.LENGTH_SHORT).show();
                return;
            }
            show(resultString);
        } else {
            show(resultString);
        }
    }

    private void continuePreview() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
        } catch (IOException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}