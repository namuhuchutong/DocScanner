package com.example.docscanner;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;

public class ImageEnhaceActivity extends AppCompatActivity {

    ImageView imgView;
    Bitmap selectedBitmap;

    Button btnImgEnhace;

    NativeClass nativeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enhance);

        nativeClass = new NativeClass();

        selectedBitmap = ImgConstants.selectedimgBitmap;
        ImgConstants.selectedimgBitmap = null;
        imgView.setImageBitmap(selectedBitmap);

        imgView = (ImageView) findViewById(R.id.imageView);
        btnImgEnhace = (Button) findViewById(R.id.btnImageEnhance);

        btnImgEnhace.setOnClickListener(btnImgEnhaceClick);
    }

    private View.OnClickListener btnImgEnhaceClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
        }
    };
}
