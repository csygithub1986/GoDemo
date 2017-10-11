package com.example.godemo;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;
import com.tcp.CommonDataDefine;
import com.tcp.FormatTransfer;
import com.tcp.TcpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ScanActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    //    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private EScanType ScanType;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonDataDefine.NetConnected:
                    Toast.makeText(ScanActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                    break;
                case CommonDataDefine.GameStart:
                    String str = (String) msg.obj;
//                    String[] propertys = str.split(";");
//                    for (int i = 0; i < propertys.length; i++) {
//                        String[] items=propertys[i].split("=");
//                    }
                    Toast.makeText(ScanActivity.this, str, Toast.LENGTH_SHORT).show();

                    break;
                case CommonDataDefine.GameOver:
                    break;
                case CommonDataDefine.Scan:
                    break;
                case CommonDataDefine.ServerStepData:
                    break;
                case CommonDataDefine.SendPreview:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        int type = getIntent().getIntExtra("scanType", 0);
        if (type == EScanType.GoScan.ordinal())
            ScanType = EScanType.GoScan;
        else if (type == EScanType.Partner.ordinal()) {
            ScanType = EScanType.Partner;
            //网络
            TcpClient.getInstance().setHandler(mHandler);
            //启动连接
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TcpClient.getInstance().start();
                }
            }).start();
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        playBeep = false;
        if (playBeep) {
            AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                playBeep = false;
            }
        }
        vibrate = false;
        if (vibrate) {
            initBeepSound();
        }
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
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(ScanActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            bundle.putParcelable("bitmap", barcode);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        ScanActivity.this.finish();
    }

    public void DetectSuccessCallback() {
        if (ScanType == EScanType.GoScan) {
            Intent resultIntent = new Intent();
            resultIntent.setClass(ScanActivity.this, ResultActivity.class);
            startActivity(resultIntent);
            ScanActivity.this.finish();
        } else if (ScanType == EScanType.Partner) {
            //总长度 =头+两个数组长度+两个长度int+一个是否成功
            int totalLen = 4 + GlobalEnvironment.BitmapBytes.length + GlobalEnvironment.Data.length + 4 + 4 + 1;
            byte[] sendData = new byte[totalLen + 4];
            int index = 0;
            //数据总长度
            CopyLHInt(sendData, index, totalLen);
            index += 4;
            //头
            CopyLHInt(sendData, index, CommonDataDefine.PreviewData);
            index += 4;
            //是否成功
            sendData[index] = 1;
            index += 1;
            //图像数据
            CopyLHInt(sendData, index, GlobalEnvironment.BitmapBytes.length);
            index += 4;
            CopyByte(sendData, index, GlobalEnvironment.BitmapBytes);
            index += GlobalEnvironment.BitmapBytes.length;

            //解析数据
            CopyLHInt(sendData, index, GlobalEnvironment.Data.length);
            index += 4;
            CopyByte(sendData, index, GlobalEnvironment.Data);
            index += GlobalEnvironment.Data.length;

            TcpClient.getInstance().Send(sendData);
        }
    }

    public void DetectFailCallback() {
        if (ScanType == EScanType.GoScan) {
        } else if (ScanType == EScanType.Partner) {
            //总长度   两个数组长度+两个长度int+一个是否成功
            int totalLen = 4 + GlobalEnvironment.BitmapBytes.length + GlobalEnvironment.Data.length + 4 + 4 + 1;
            Log.i("总长度", "" + totalLen);
            byte[] sendData = new byte[totalLen + 4];
            int index = 0;
            //数据总长度
            CopyLHInt(sendData, index, totalLen);
            index += 4;
            //头
            CopyLHInt(sendData, index, CommonDataDefine.PreviewData);
            index += 4;

            //是否成功
            sendData[index] = 0;
            index += 1;
            //图像数据
            CopyLHInt(sendData, index, GlobalEnvironment.BitmapBytes.length);
            index += 4;
            CopyByte(sendData, index, GlobalEnvironment.BitmapBytes);
            index += GlobalEnvironment.BitmapBytes.length;

            //解析数据
            CopyLHInt(sendData, index, GlobalEnvironment.Data.length);
            index += 4;
            CopyByte(sendData, index, GlobalEnvironment.Data);
            index += GlobalEnvironment.Data.length;

            TcpClient.getInstance().Send(sendData);
        }
    }

    private void CopyLHInt(byte[] byteData, int index, int intData) {
        byte[] intByteData = FormatTransfer.toLH(intData);
        for (int i = 0; i < intByteData.length; i++) {
            byteData[index + i] = intByteData[i];
        }
    }

    private void CopyByte(byte[] byteData, int index, byte[] copyData) {
        for (int i = 0; i < copyData.length; i++) {
            byteData[index + i] = copyData[i];
        }
    }

    private void CopyByte(byte[] byteData, int index, int[] copyData) {
        for (int i = 0; i < copyData.length; i++) {
            byteData[index + i] = (byte) copyData[i];
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, null, null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
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
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
