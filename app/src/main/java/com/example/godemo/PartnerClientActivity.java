package com.example.godemo;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.view.ViewfinderView;
import com.tcp.CommonDataDefine;
import com.tcp.TcpClient;

import java.io.IOException;

import static com.tcp.CommonDataDefine.*;

public class PartnerClientActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonDataDefine.NetConnected:
                    Toast.makeText(PartnerClientActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                    break;
                case CommonDataDefine.GameStart:
                    String str = (String) msg.obj;
//                    String[] propertys = str.split(";");
//                    for (int i = 0; i < propertys.length; i++) {
//                        String[] items=propertys[i].split("=");
//                    }
                    Toast.makeText(PartnerClientActivity.this, str, Toast.LENGTH_SHORT).show();

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
//        setContentView(R.layout.activity_partner_client);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;

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
//            handler = new CaptureActivityHandler(this, null, null);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
