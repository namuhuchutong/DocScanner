package com.example.docscanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.docscanner.utils.CameraSurfaceView;

public class CameraActivity extends Activity {


    CameraSurfaceView surfaceView;
    ImageView imgView;
    Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camactivity_surface_view);

        surfaceView = findViewById(R.id.CamView);
        imgView = findViewById(R.id.imgview);
        btnCapture = findViewById(R.id.btnCapture);

        btnCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                caputre();
            }
        });

    }

    public void caputre(){
        surfaceView.capture(new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imgView.setImageBitmap(bitmap);

                camera.startPreview();
            }
        });
    }
}
