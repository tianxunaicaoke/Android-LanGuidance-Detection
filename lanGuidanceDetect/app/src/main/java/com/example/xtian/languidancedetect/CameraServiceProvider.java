package com.example.xtian.languidancedetect;

import android.content.Context;

import com.example.camera.CameraService;

import dagger.Module;
import dagger.Provides;


@Module
public class CameraServiceProvider {

    @LanGuidanceDetectSingleton
    @Provides
    public CameraService provideCameraService(Context context){
        return new CameraService(context);
    }
}
