package com.example.camera;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.camera.CameraInitState.FAIL;
import static com.example.camera.CameraInitState.INIT;
import static com.example.camera.CameraInitState.SUCCESS;

@IntDef({INIT, SUCCESS, FAIL})
@Retention(RetentionPolicy.SOURCE)
@interface CameraInitState {
     int INIT = 0;
     int SUCCESS =1;
     int FAIL = 2;
}
