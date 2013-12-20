package com.franziroesner.passwordmanager;

import java.io.IOException;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A Camera preview class that looks for QR codes in the preview images. */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
	public static String TAG = "CameraPreview";
	
	private QrCodeActivity mActivity;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Reader mReader;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mActivity = (QrCodeActivity)context;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        // Set up QR Code reader
        mReader = new MultiFormatReader();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// Convert to BinaryBitmap for use by zxing library
		Camera.Size size = camera.getParameters().getPreviewSize();
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, size.width, size.height, 0, 0,
                size.width, size.height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        // Look for a QR code in the image
        Result result;
		try {
			result = mReader.decode(bitmap);
			if (result != null) {
		        Log.i(TAG, "QR CODE RESULT: " + result.getText());
		        mActivity.qrCodeFound(result.getText());
	        }
			
		} catch (Exception e) {
			Log.d(TAG, "Error during decoding: " + e.getMessage());
		}                
	}
}