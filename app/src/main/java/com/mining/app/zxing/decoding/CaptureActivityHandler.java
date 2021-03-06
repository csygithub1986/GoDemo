/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mining.app.zxing.decoding;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.godemo.GlobalEnvironment;
import com.example.godemo.ScanActivity;
import com.example.godemo.R;
import com.go.algorithm.Detector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.view.ViewfinderResultPointCallback;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final ScanActivity activity;
    private final DecodeThread decodeThread;

    public CaptureActivityHandler(ScanActivity activity, Vector<BarcodeFormat> decodeFormats,
                                  String characterSet) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        // Start ourselves capturing previews and decoding.
//        CameraManager.get().startPreview();
//        restartPreviewAndDecode();
    }

    /**
     * 处理分析图片以后的结果
     *
     * @param message
     */
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                //Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                break;
            case R.id.goImageDetect:
                Bundle bundle = message.getData();

                /***********************************************************************/
                Bitmap img = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

                //压缩图片，传给PC
                Bitmap smallImg = zoomImg(img, img.getWidth() / 3, img.getHeight() / 3);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 把压缩后的数据存放到baos中
                smallImg.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                GlobalEnvironment.BitmapBytes = baos.toByteArray();

                //解析图片，如果失败，不采纳
                long start = System.currentTimeMillis();

                int[] result = Detector.Detect(img, img.getWidth(), img.getHeight(), 19);
                long end = System.currentTimeMillis();
//                Log.d(TAG, "c++ Detect图像  (" + (end - start) + " ms):\n");
                if (result == null) {
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    GlobalEnvironment.Data = new int[0];
                    activity.DetectFailCallback();
                    return;
                }
                GlobalEnvironment.ScanedBitmap = img;
                GlobalEnvironment.Data = result;

                activity.DetectSuccessCallback();


                break;
        }
    }


    private Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public void quitSynchronously() {
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

    }


    /**
     *
     */
    public void restartPreviewAndDecode() {
        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        activity.drawViewfinder();
    }

}
