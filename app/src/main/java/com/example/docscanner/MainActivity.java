package com.example.docscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    final static int PICK_IMAGE = 1001;

    Button btnOpenGallery;
    Button btnCapture;

    ImageView imgView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    static{
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imgView = (ImageView) findViewById(R.id.imgview);
        this.btnCapture = (Button) findViewById(R.id.Capture);
        this.btnOpenGallery = (Button) findViewById(R.id.openGallery);
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

        }
    };
}