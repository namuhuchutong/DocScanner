package com.example.docscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    final static int PICK_IMAGE = 1001;

    Button btnOpenGallery;
    Button btnCapture;
    Button btnCamera;

    ImageView imgView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    NativeClass a;


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
        this.imgView = (ImageView) findViewById(R.id.imageView);
        this.btnOpenGallery = (Button) findViewById(R.id.btnOpenGallery);
        this.btnCapture = (Button) findViewById(R.id.btnImageProcess);
        this.btnCamera = (Button) findViewById(R.id.btnCamera);
    }

    private void initializeEvent() {
        this.btnCapture.setOnClickListener(this.btnCaptureClick);
        this.btnOpenGallery.setOnClickListener(this.btnOpenGalleryClick);
        this.btnCamera.setOnClickListener(btnCameraClick);
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
            startActivityForResult(intent.createChooser(intent, "Albums"), PICK_IMAGE);

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
            startActivity(intent);
        }
    };

    private void loadImage() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(this.selectedImage);
            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            imgView.setImageBitmap(selectedBitmap);
            btnCapture.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            this.loadImage();
        }
    }
}