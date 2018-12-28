package com.example.xtian.languidancedetect;

import android.content.Context;

import dagger.BindsInstance;
import dagger.Component;

@LanGuidanceDetectSingleton
@Component(modules = CameraServiceProvider.class)
public interface CameraComponent {
    void inject(MainActivity mainActivity);
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context ctx);
        CameraComponent build();
    }
}
