package com.example.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import javax.inject.Inject;

public class CameraService {
    private final String cameraId;
    private Context context;
    private CameraManager cameraManager;
    private CameraDevice mCameraDevice;
    private @CameraInitState
    int cameraInitState;

    @Inject
    public CameraService(Context context) {
        this.context = context;
        cameraId = Integer.toString(CameraCharacteristics.LENS_FACING_FRONT);
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * @return CameraInitState.SUCCESS if openCamera is ok
     */
    public int openCamera(Handler handler) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return CameraInitState.FAIL;
        }
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    cameraInitState = CameraInitState.SUCCESS;
                    Toast.makeText(context,"",Toast.LENGTH_LONG);
                    mCameraDevice = cameraDevice;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    cameraInitState = CameraInitState.FAIL;
                    mCameraDevice.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                    cameraInitState = CameraInitState.FAIL;
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return CameraInitState.SUCCESS;
    }

    /**
     * @return CameraInitState
     */
    public @CameraInitState
    int getCameraInitState() {
        return cameraInitState;
    }
}
