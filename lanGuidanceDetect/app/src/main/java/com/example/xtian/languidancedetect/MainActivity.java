package com.example.xtian.languidancedetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.camera.CameraInitState;
import com.example.camera.CameraService;
import com.example.opencv.OpenCVJNI;

import java.nio.ByteBuffer;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject CameraService cameraService;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    @Inject CamerListener camerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraComponent.Builder cameraComponent = DaggerCameraComponent
                .builder()
                .context(this);
        cameraComponent.build().inject(this);

        Button tv = findViewById(R.id.button);
        ImageView imageView = findViewById(R.id.imageView);
        surfaceView = findViewById(R.id.surfaceView);
        camerListener.setImageView(imageView);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(CameraInitState.SUCCESS == cameraService.getCameraInitState()){
                  cameraService.takePicture();
              }
            }
        });
        cameraService.addImageReaderListener(camerListener,new Handler(getMainLooper()));
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cameraService.openCamera(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

}
