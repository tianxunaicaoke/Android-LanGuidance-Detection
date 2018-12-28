package com.example.opencv;

public class OpenCVJNI {
    static {
        System.loadLibrary("native-lib");
    }
    public native int[] gray(int[] buf, int w, int h);
}
