package com.franziroesner.passwordmanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

/** An Activity that starts a camera preview and waits for notifications
 *  that a QR code has been found. */
public abstract class QrCodeActivity extends Activity {
	
	public static String TAG = "ScannerActivity";
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private boolean mStillLooking;
	
    protected PasswordStore mPasswordStore;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        
        mStillLooking = true;
        
        // Instantiate password store
        SharedPreferences prefs = getSharedPreferences("PasswordStore", MODE_PRIVATE);
        mPasswordStore = new PasswordStore(prefs);
        
        startCamera();
    }
	
	@Override
	protected void onPause() {
		Log.i(TAG, "onPause()");
		stopCamera();	    
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume()");
	    super.onResume();
	    
	    if (mStillLooking) {
	    	startCamera();
	    }
	}
	
	protected void startCamera() {
		Log.i(TAG, "startCamera()");
		
		if (mCamera != null) return;
		
		try {
			mCamera = Camera.open(0);
		} catch (Exception e) {
			Log.i(TAG, "Failed to start camera, retry in a bit");
			try {
				// Wait and try again... 
				// (There seems to be a race condition in Glass where something
				// else tries to get the camera when you say "OK Glass", but it
				// releases it again after a few seconds. So wait for that.)
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// Well okay then
				Log.e(TAG, "Exception while sleeping...");
			}
			startCamera();
			return;
		}
		
		// Set camera params -- workaround for Glass bug that warps preview
		Camera.Parameters params = mCamera.getParameters();
	    params.setPreviewFpsRange(30000, 30000);
	    mCamera.setParameters(params);
        
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
	}
	
	protected void stopCamera() {
		Log.i(TAG, "stopCamera()");
		
		if (mCamera != null) {
			  mPreview.getHolder().removeCallback(mPreview);
			  mCamera.setPreviewCallback(null);
			  mCamera.stopPreview();		        
		      mCamera.release();
		      mCamera = null;
		      
		      FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		      preview.removeView(mPreview);
		}
	}
	
	/* Callback for when CameraPreview finds a QR code. */
	public abstract void qrCodeFound(String text);
}