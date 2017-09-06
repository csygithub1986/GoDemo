package com.go.algorithm;

import android.graphics.Bitmap;
import android.graphics.Color;


public class Detector {
    static {
        System.loadLibrary("native-lib");
    }

    public static native int[] Detect(Object bitmap,int width,int height,int boardSize);

}
