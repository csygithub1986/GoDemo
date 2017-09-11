package com.go.algorithm;

import android.graphics.Bitmap;
import android.graphics.Color;


public class Detector {
    static {
        System.loadLibrary("native-lib");
    }

    public static native int[] Detect(Object bitmap,int width,int height,int boardSize);

    public static native void GetCut(Object bitmap);

    public static native void GetOrigin(Object bitmap);

    public static native void GetGrid(Object bitmap);

    public static native void GetCircle(Object bitmap);

    public static native void GetCanny(Object bitmap);


}
