package com.example.docscanner.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
    카메라 전환없이 해당 앱에서 직접 카메라 동작을 확인 가능.

 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder holder;
    Camera cam = null;

    public CameraSurfaceView(Context context)
    {
        super(context);
        initalize(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        initalize(context);
    }

    private void initalize(Context context){

        if(!checkCameraHardware(context)){
            Log.d("Camera", "There is no Camera");
            return;
        }

        holder = getHolder();
        holder.addCallback(this);
    }

    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        cam = Camera.open();

        try{
            cam.setPreviewDisplay(holder);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        cam.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        cam.stopPreview();
        cam.release();
        cam = null;
    }

    public boolean capture(Camera.PictureCallback callback){
        if(cam != null){
            cam.takePicture(null, null, callback);
            return true;
        }else{
            return false;
        }
    }
}
