package com.example.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;

import java.util.Arrays;

public class CameraService {
    private final String cameraId;
    private Context context;
    private CameraManager cameraManager;
    private CameraDevice mCameraDevice;
    private ImageReader imageReader;
    private @CameraInitState
    int cameraInitState;
    private HandlerThread cameraBackGroundHandlerThread;
    private Handler cameraBackGroundHandler;
    private CameraCaptureSession cameraCaptureSession;

    public CameraService(Context context) {
        this.context = context;
        cameraId = Integer.toString(CameraCharacteristics.LENS_FACING_FRONT);
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        initBackGroundHandler();
    }

    /**
     * @return CameraInitState.SUCCESS if openCamera is ok
     */
    public int openCamera(final SurfaceHolder surfaceHolder) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraInitState = CameraInitState.FAIL;
            return CameraInitState.FAIL;
        }
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    cameraInitState = CameraInitState.SUCCESS;
                    mCameraDevice = cameraDevice;
                    createCameraCaptureSession(surfaceHolder);
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
            }, cameraBackGroundHandler);
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

    private void initBackGroundHandler() {
        cameraBackGroundHandlerThread = new HandlerThread("CameraBackground");
        cameraBackGroundHandlerThread.start();
        cameraBackGroundHandler = new Handler(cameraBackGroundHandlerThread.getLooper());
    }

    private void createCameraCaptureSession(final SurfaceHolder surfaceHolder) {
        try {
            final CaptureRequest.Builder mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surfaceHolder.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession mCameraCaptureSession) {
                            if (null == mCameraDevice) {
                                return;
                            }
                            cameraCaptureSession = mCameraCaptureSession;
                            try {
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                mPreviewRequestBuilder.addTarget(imageReader.getSurface());
                                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                                cameraCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, cameraBackGroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void addImageReaderListener(ImageReader.OnImageAvailableListener listener, Handler mainHandler){
        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG,1);
        imageReader.setOnImageAvailableListener(listener, mainHandler);
    }

    @Deprecated
    public void takePicture(){
        if (mCameraDevice == null) return;
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            cameraCaptureSession.capture(mCaptureRequest, null, cameraBackGroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
