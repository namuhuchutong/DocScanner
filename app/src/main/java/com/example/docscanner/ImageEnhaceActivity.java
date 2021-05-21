package com.example.docscanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;

public class ImageEnhaceActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap selectedImageBitmap;

    Button btnImgEnhace;

    NativeClass nativeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enhance);

        initializeElement();
        initializeImage();
    }

    private void initializeElement() {

        nativeClass = new NativeClass();
        imageView = findViewById(R.id.imageView);
    }

    private void initializeImage() {

        selectedImageBitmap = ImgConstants.selectedimgBitmap;
        ImgConstants.selectedimgBitmap = null;
        imageView.setImageBitmap(selectedImageBitmap);

    }

    private View.OnClickListener btnImageToBWClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener btnImageToMagicColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener btnImageToGrayClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

}
