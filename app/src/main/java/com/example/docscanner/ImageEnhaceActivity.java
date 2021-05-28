package com.example.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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

    NativeClass nativeClass;

    FloatingActionButton fabMain;
    FloatingActionButton fabPDF;
    FloatingActionButton fabJpeg;

    TextView fabPDFText;
    TextView fabJpegText;

    Boolean isFabVisible;


    Button btnRotateLeft;
    Button btnSave;
    Button btnRotateRight;

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

        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateRight = findViewById(R.id.btnRotateRight);
        btnSave = findViewById(R.id.btnImageSave);

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
        this.btnRotateLeft.setOnClickListener(btnRoateLeftClick);
        this.btnRotateRight.setOnClickListener(btnRoateRightClick);
        this.btnSave.setOnClickListener(btnImageSaveClick);
    }

    private View.OnClickListener btnRoateLeftClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Matrix m = new Matrix();
            m.postRotate(-90);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);

            imageView.setImageBitmap(result);
        }
    };

    private View.OnClickListener btnRoateRightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Matrix m = new Matrix();
            m.postRotate(90);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);

            imageView.setImageBitmap(result);

        }
    };

    private View.OnClickListener btnImageSaveClick = new View.OnClickListener() {
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

    private void sendEmail(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"email@address.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "test@test");
        email.putExtra(Intent.EXTRA_TEXT, "내용 미리보기 (미리적을 수 있음)");
        startActivity(email);
    }


}