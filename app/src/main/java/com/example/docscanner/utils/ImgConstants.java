package com.example.docscanner.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

public class ImgConstants {
    public final static String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "imagedir";
    public final static int PICK_IMAGE = 1001;
    public final static int GET_IMAGE = 1002;
    public static Bitmap selectedimgBitmap;
}
