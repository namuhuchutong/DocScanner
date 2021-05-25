package com.example.docscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docscanner.utils.CameraSurfaceView;
import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    Button btnOpenGallery;
    Button btnCapture;
    Button btnCamera;

    ImageView imgView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    NativeClass a;
    CameraSurfaceView surfaceView;
    Button btnCameraCapture;

    static{
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElement();
        initializeEvent();
    }

    private void initializeElement() {
        this.surfaceView = findViewById(R.id.surfaceView);
        this.imgView = (ImageView) findViewById(R.id.imgView);
        this.btnOpenGallery = (Button) findViewById(R.id.btnOpenGallery);
        this.btnCapture = (Button) findViewById(R.id.btnImageProcess);
        this.btnCameraCapture = (Button) findViewById(R.id.btnCameraCapture);

    }

    private void initializeEvent() {
        this.btnCapture.setOnClickListener(this.btnCaptureClick);
        this.btnOpenGallery.setOnClickListener(this.btnOpenGalleryClick);
        this.btnCameraCapture.setOnClickListener(this.btnCameraCaptureClick);
    }

    private View.OnClickListener btnOpenGalleryClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            /*
                Reference : https://g-y-e-o-m.tistory.com/107
                ACTION_PICK -> ACTION_GET_CONTENT
                createChooser -> 사용자에게 어디 포토를 쓸 것인가 선택권 주기
             */

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent, "Albums"), ImgConstants.PICK_IMAGE);

        }
    };

    private View.OnClickListener btnCaptureClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            ImgConstants.selectedimgBitmap = selectedBitmap;

            Intent intent = new Intent(getApplicationContext(), ImageCropActivity.class);
            startActivity(intent);

        }
    };

    private View.OnClickListener btnCameraClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){


            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivityForResult(intent, ImgConstants.GET_IMAGE);
        }
    };

    private View.OnClickListener btnCameraCaptureClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            caputre();
        }
    };

    public void caputre(){
        surfaceView.capture(new Camera.PictureCallback(){
           @Override
           public void onPictureTaken(byte[] data, Camera camera){
               BitmapFactory.Options options = new BitmapFactory.Options();
               options.inSampleSize = 2;
               Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
               selectedBitmap = bitmap;
               imgView.setVisibility(View.VISIBLE);
               surfaceView.setVisibility(View.INVISIBLE);
               imgView.setImageBitmap(selectedBitmap);

           }
        });
    }

    private void loadImage() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(this.selectedImage);
            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            imgView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            imgView.setImageBitmap(selectedBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImgConstants.PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            this.loadImage();
        }
        else if(requestCode == ImgConstants.GET_IMAGE && resultCode == RESULT_OK && data != null){
            byte[] byteArray = data.getByteArrayExtra("CapturedImage");
            selectedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgView.setImageBitmap(selectedBitmap);
            btnCapture.setVisibility(View.VISIBLE);
        }
    }
}