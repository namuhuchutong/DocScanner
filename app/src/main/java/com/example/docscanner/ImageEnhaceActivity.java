package com.example.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ImageEnhaceActivity extends Activity {

    ImageView imageView;
    Bitmap selectedImageBitmap;

    Button btnImgEnhace;

    NativeClass nativeClass;

    FloatingActionButton fabMain;
    FloatingActionButton fabPDF;
    FloatingActionButton fabJpeg;

    TextView fabPDFText;
    TextView fabJpegText;

    Boolean isFabVisible;

    BitmapDrawable drawable;
    Bitmap img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enhance);

        initializeElement();
        initalizeEvent();
        initializeImage();
    }

    private void initializeElement() {

        nativeClass = new NativeClass();

        imageView = findViewById(R.id.imageView);
        fabMain = findViewById(R.id.fabMain);
        fabPDF = findViewById(R.id.fabPdf);
        fabJpeg = findViewById(R.id.fabJpeg);
        fabPDFText = findViewById(R.id.fabPdfText);
        fabJpegText = findViewById(R.id.fabJpegText);

        fabPDFText.setVisibility(View.GONE);
        fabPDF.hide();
        fabJpegText.setVisibility(View.GONE);
        fabJpeg.hide();

        isFabVisible = false;

    }

    private void initializeImage() {

        selectedImageBitmap = ImgConstants.selectedimgBitmap;
        ImgConstants.selectedimgBitmap = null;
        imageView.setImageBitmap(selectedImageBitmap);

    }

    private void initalizeEvent() {
        this.fabMain.setOnClickListener(FabMainClick);
        this.fabPDF.setOnClickListener(FabPDFClick);
        this.fabJpeg.setOnClickListener(FabJpegClick);
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

    private View.OnClickListener FabMainClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isFabVisible) {
                fabPDF.show();
                fabJpeg.show();
                fabPDFText.setVisibility(View.VISIBLE);
                fabJpegText.setVisibility(View.VISIBLE);

                isFabVisible = true;
            } else {
                fabPDF.hide();
                fabJpeg.hide();
                fabPDFText.setVisibility(View.GONE);
                fabJpegText.setVisibility(View.GONE);

                isFabVisible = false;
            }
        }
    };

    private View.OnClickListener FabPDFClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
            startActivityForResult(intent, 1);

        }
    };

    private View.OnClickListener FabJpegClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
            startActivityForResult(intent, 2);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String FileName = data.getStringExtra("file name");
            FileName = FileName + ".pdf";

        } else if (requestCode == 2 && requestCode == RESULT_OK) {

        }
    }


}