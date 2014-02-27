package com.yaniv.incore;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview 
		extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
	private SurfaceHolder mHolder;
    private Camera mCamera;
    private Size previewSize;
    
	public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        previewSize = sizes.get(1);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.setFixedSize(previewSize.height, previewSize.width);
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
        
        // deprecated setting, but required on Android versions prior to 3.0
        // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw 
    	// the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();            
            
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	if (mCamera != null) {
    		mCamera.stopPreview();
    		mCamera.setPreviewCallback(null);

    		mCamera.release();
    		mCamera = null;
        }
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
        } catch (Exception e) {
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Parameters parameters = mCamera.getParameters();

//        parameters.setPreviewSize(optimalPreviewSize.width-50, optimalPreviewSize.height);
        
//        Size optimalPreviewSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
//        List<Camera.Size> cameraSizes = parameters.getSupportedPictureSizes();
//        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//        Camera.Size pictureSize = cameraSizes.get(cameraSizes.size() - 1);
        parameters.setPictureSize(1280, 960);
//        cameraSizes = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        requestLayout();
        mCamera.setParameters(parameters);
        

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    public void resetPreview() {
    	try {
        	mCamera.stopPreview();
			mCamera.setPreviewDisplay(mHolder);
	        mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
  
}
