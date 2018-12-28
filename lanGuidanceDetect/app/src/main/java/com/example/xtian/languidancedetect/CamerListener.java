package com.example.xtian.languidancedetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.example.opencv.OpenCVJNI;

import java.nio.ByteBuffer;

import javax.inject.Inject;

@LanGuidanceDetectSingleton
public class CamerListener implements OnImageAvailableListener {

    private ImageView imageView;

    @Inject
    CamerListener() {

    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Image image = imageReader.acquireNextImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        OpenCVJNI openCVJNI = new OpenCVJNI();
        int[] resultPixes = openCVJNI.gray(pix, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0, w, h);
        imageView.setImageBitmap(result);
        image.close();
    }
}
