package com.example.docscanner.utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
/*
    matrix -> bitmap,
    bitmap -> matrix 연산 모
 */
public class ImageUtils {

    /*
    *       왜 자꾸 에러가 나오는가?
    *       제대로 로딩이 안되는 문제인가?
    *     java.lang.UnsatisfiedLinkError: No implementation found for long org.opencv.core.Mat.n_Mat(int, int, int, double, double, double, double) (tried Java_org_opencv_core_Mat_n_1Mat and Java_org_opencv_core_Mat_n_1Mat__IIIDDDD)
     */
    public static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap bitmap32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap32, mat);
        return mat;
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

}
