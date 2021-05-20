package com.example.docscanner.utils;

import android.graphics.Bitmap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NativeClass {

    private static final int THRESHOLD_LEVEL = 2;
    private static final double AREA_LOWER_THRESHOLD = 0.2;
    private static final double AREA_UPPER_THRESHOLD = 0.98;
    private static final double DOWNSCALE_IMAGE_SIZE = 600f;

    public Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        PerspectiveTransformation perspective = new PerspectiveTransformation();
        MatOfPoint2f rectangle = new MatOfPoint2f();
        rectangle.fromArray(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4));
        Mat dstMat = perspective.transform(ImageUtils.bitmapToMat(bitmap), rectangle);
        return ImageUtils.matToBitmap(dstMat);
    }

    private static Comparator<MatOfPoint2f> AreaDescendingComparator = new Comparator<MatOfPoint2f>() {
        @Override
        public int compare(MatOfPoint2f o1, MatOfPoint2f o2) {
           double area1 = Imgproc.contourArea(o1);
           double area2 = Imgproc.contourArea(o2);
           return (int) Math.ceil(area2 - area1);
        }
    };

    public MatOfPoint2f getPoint(Bitmap bitmap){
        Mat src = ImageUtils.bitmapToMat(bitmap);


        // 이미지를 다운 스케일링 처리하여 성능 향상
        double ratio = DOWNSCALE_IMAGE_SIZE / Math.max(src.width(), src.height());
        Size downscaledSize = new Size(src.width() * ratio, src.height() * ratio);
        Mat downscaled = new Mat(downscaledSize, src.type());
        Imgproc.resize(src, downscaled, downscaledSize);

        List<MatOfPoint2f> rectangles = getPoints(downscaled);
        if(rectangles.size() == 0){
            return null;
        }
        Collections.sort(rectangles, AreaDescendingComparator);
        MatOfPoint2f largeRectangle = rectangles.get(0);
        MatOfPoint2f result = mathUtils.scaleRectangle(largeRectangle, 1f / ratio);
        return result;
    }

    private boolean isRectangle(MatOfPoint2f polygon, int srcArea)
    {
        MatOfPoint polygonInt = mathUtils.toMatOfPointInt(polygon);

        if(polygon.rows()  != 4)
        {
            return false;
        }

        double area = Math.abs(Imgproc.contourArea(polygon));
        if(area < srcArea * AREA_LOWER_THRESHOLD || area > srcArea * AREA_UPPER_THRESHOLD)
        {
            return false;
        }

        if(!Imgproc.isContourConvex(polygonInt))
        {
            return false;
        }


        double maxCosine = 0;
        Point[] approxPoints = polygon.toArray();

        for (int i = 2; i < 5; i++)
        {
            double cosine = Math.abs(mathUtils.angle(approxPoints[i%4], approxPoints[i - 2], approxPoints[i - 1]));
            maxCosine = Math.max(cosine, maxCosine);
        }

        if(maxCosine >= 0.3){
            return false;
        }

        return true;
    }


    public List<MatOfPoint2f> getPoints(Mat src){

        Mat blurred = new Mat();
        Imgproc.medianBlur(src, blurred, 9);

        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U);
        Mat gray = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint2f> rectangles = new ArrayList<>();

        List<Mat> sources = new ArrayList<>();
        sources.add(blurred);
        List<Mat> dests = new ArrayList<>();
        dests.add(gray0);

        int srcArea = src.rows() * src.cols();

        for ( int c = 0; c < 3; c++)
        {
            int[] ch = {c, 0};
            MatOfInt fromTo = new MatOfInt(ch);

            Core.mixChannels(sources, dests, fromTo);

            for(int l = 0; l < THRESHOLD_LEVEL; l++)
            {
                if( l == 0)
                {
                    Imgproc.Canny(gray0, gray, 10, 20);

                    Imgproc.dilate(gray, gray, Mat.ones(new Size(3, 3), 0));
                }
                else
                {
                    int threshold = (l + 1) * 255 / THRESHOLD_LEVEL;
                    Imgproc.threshold(gray0, gray, threshold, 255, Imgproc.THRESH_BINARY);
                }

                Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                for(MatOfPoint contour : contours)
                {
                    MatOfPoint2f contourFloat = mathUtils.toMatOfPointFloat(contour);
                    double arcLen = Imgproc.arcLength(contourFloat, true) *0.02;

                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(contourFloat, approx, arcLen, true);

                    if(isRectangle(approx, srcArea))
                    {
                        rectangles.add(approx);
                    }
                }
            }
        }

        return rectangles;

    }


}
