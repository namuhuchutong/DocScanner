package com.example.docscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.docscanner.utils.CameraSurfaceView;
import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity {

    final int PERMISSIONS_REQUEST_CODE = 1;
    final int PERMISSIONS_REQUEST_CAM = 2;

    Button btnOpenGallery;
    Button btnCapture;
    Button btnCamera;

    ImageView imgView;

    Uri selectedImage;
    Bitmap selectedBitmap;

    NativeClass a;
    CameraSurfaceView surfaceView;
    Button btnCameraCapture;

    static {
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElement();
        initializeEvent();
        requestPermission();
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

    private View.OnClickListener btnOpenGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
                Reference : https://g-y-e-o-m.tistory.com/107
                ACTION_PICK -> ACTION_GET_CONTENT
                createChooser -> ??????????????? ?????? ????????? ??? ????????? ????????? ??????
             */

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent, "Albums"), ImgConstants.PICK_IMAGE);

        }
    };

    private View.OnClickListener btnCaptureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (selectedBitmap == null)
                Toast.makeText(getApplicationContext(), "No Image!", Toast.LENGTH_SHORT).show();
            else {
                ImgConstants.selectedimgBitmap = selectedBitmap;

                Intent intent = new Intent(getApplicationContext(), ImageCropActivity.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener btnCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivityForResult(intent, ImgConstants.GET_IMAGE);
        }
    };

    private View.OnClickListener btnCameraCaptureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            caputre();
        }
    };

    public void caputre() {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //bitmap = NativeClass.imgToBright(bitmap, 1, 100);  // ????????? ??????
                selectedBitmap = bitmap;
                //ImgConstants.selectedimgBitmap = selectedBitmap;  // ????????? ??????
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
        } else if (requestCode == ImgConstants.GET_IMAGE && resultCode == RESULT_OK && data != null) {
            byte[] byteArray = data.getByteArrayExtra("CapturedImage");
            selectedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgView.setImageBitmap(selectedBitmap);
            btnCapture.setVisibility(View.VISIBLE);
        }
    }

    private void requestPermission() {
        boolean shouldProviceRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);//???????????? ????????? ??????????????? ????????? true ??????

        if (shouldProviceRationale) {
            //?????? ????????? ????????? ????????? ?????? ??????
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            //???????????????.
            makeDir();
        }


        boolean permission =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);//???????????? ????????? ??????????????? ????????? true ??????
        if(permission ){//== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},PERMISSIONS_REQUEST_CAM);

        }

    }



    /*
        ????????? ?????? ??? ?????? ??????
        ???????????? ????????? ???????????? ????????? ????????????.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //?????? ?????? ?????????
                    makeDir();
                } else {
                    //???????????? ?????? ?????????
                    denialDialog();
                    Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
                }
            }

            case PERMISSIONS_REQUEST_CAM:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }   else{
                    denialDialog();
                    Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
    /*
        ????????? ????????? ????????? ?????? ?????????????????? ??????
     */
    public void denialDialog() {
        new AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("????????? ???????????????. ?????? ???????????? ????????? ????????? ??????????????????.")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent); //????????????????????? ?????? ?????????????????? ?????? ?????? ????????? ???????????????
                    }
                })
                .create()
                .show();
    }


    // https://developer.android.com/training/permissions/requesting?hl=ko
    // https://stackoverflow.com/questions/33030933/android-6-0-open-failed-eacces-permission-denied/33031091#33031091
    // android6.0?????? ?????? ????????? ?????? ????????? ??????.

   public void makeDir() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath(); //????????? ?????????
        String directoryName = "imagedir";
        final File myDir = new File(root +File.separator + directoryName);
        if (!myDir.exists()) {
            boolean wasSuccessful = myDir.mkdirs();
            if (!wasSuccessful) {
                System.out.println("file: was not successful.");
            } else {
                System.out.println("file: ????????? ??????????????????." + root + "/" + directoryName);
            }
        } else {
            System.out.println("file: " + root + "/" + directoryName +"already exists");
        }
    }
}