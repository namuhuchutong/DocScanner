package com.example.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageEnhaceActivity extends Activity {

    ImageView imageView;
    Bitmap selectedImageBitmap;
    Bitmap orign;

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

    Button btnOrignal;
    Button btnBW;
    Button btnBrightness;

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

        btnOrignal = findViewById(R.id.btnOrignal);
        btnBW = findViewById(R.id.btnBW);
        btnBrightness = findViewById(R.id.btnBrightness);

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
        orign = ImgConstants.selectedimgBitmap;
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
        this.btnOrignal.setOnClickListener(btnOrignalClick);
        this.btnBW.setOnClickListener(btnBWClick);
        this.btnBrightness.setOnClickListener(btnBrightnessclick);
    }

    // ?????? ????????? ??????
    private View.OnClickListener btnOrignalClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imageView.setImageBitmap(orign);
        }
    };

    // ????????? ?????? ??????
    private View.OnClickListener btnBWClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap res;

            res = nativeClass.imgToBW(bitmap);
            imageView.setImageBitmap(res);

        }
    };

    // ????????? ?????? ??????
    private View.OnClickListener btnBrightnessclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap res;

            res = nativeClass.imgToBright(bitmap, 1, 50);
            imageView.setImageBitmap(res);

        }
    };

    // ???????????? 90 ??????
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

    // ??????????????? 90 ??????
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
            Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
            startActivityForResult(intent, 120);
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
            startActivityForResult(intent, 110);

        }
    };

    private View.OnClickListener FabJpegClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
            startActivityForResult(intent, 120);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 110 && resultCode == RESULT_OK) {
            String FileName = data.getStringExtra("file name");
            FileName = ImgConstants.sdPath + File.separator+ FileName + ".pdf";
            File path = new File(FileName);

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();


            /*
                pdf ?????? ?????? ?????????
                ????????? ???????????? ?????? -> ?????? ?????? ??????
             */
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageinfo  = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageinfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            try {
                document.writeTo(new FileOutputStream(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            document.close();

            sendEmail(FileName);

        } else if (requestCode == 120 && resultCode == RESULT_OK) {
            String FileName = data.getStringExtra("file name");
            FileName = ImgConstants.sdPath + File.separator+ FileName + ".jpeg";
            File path = new File(FileName);

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            try {
                FileOutputStream outstream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendEmail(String filename){
        try {
            Intent email = new Intent(Intent.ACTION_SEND);

            File file = new File(filename);

            if (!file.exists() || !file.canRead()) {
                Log.e("PDF FILE", "Doesn't exist!");
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            }

            /*
                Issue#5
                android.os.FileUriExposedException: file:///storage/emulated/0/imagedir/324fsf.pdf exposed beyond app through ClipData.Item.getUri()
             */

            /*
                https://developer.android.com/reference/androidx/core/content/FileProvider
                android 7.0 Uri ?????? ??????

             */

            Uri attachment = FileProvider.getUriForFile(this, "com.example.docscanner.fileprovider", file);

            email.setType("plain/text");
            String[] address = {"email@address.com"};
            email.putExtra(Intent.EXTRA_EMAIL, address);
            email.putExtra(Intent.EXTRA_SUBJECT, "Title");
            email.putExtra(Intent.EXTRA_STREAM, attachment);
            email.putExtra(Intent.EXTRA_TEXT, "Content");
            startActivity(email);
        }catch (Throwable t){
            Log.i("sending maill exception", t.toString());
            Toast.makeText(this, "Request failed try again", Toast.LENGTH_SHORT).show();
        }
    }

    /*

    public Uri getUriFromPath(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null);

        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;


    }
    */

}