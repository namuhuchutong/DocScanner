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

        // 계산된 ROI 크기를 비교.

        public int compare(MatOfPoint2f m1, MatOfPoint2f m2) {
            double area1 = Imgproc.contourArea(m1);
            double area2 = Imgproc.contourArea(m2);
            return (int) Math.ceil(area2 - area1);
        }
    };


    public MatOfPoint2f getPoint(Bitmap bitmap) {

        Mat src = ImageUtils.bitmapToMat(bitmap);

        // 처리 속도 향상을 위해서 이미지 크기를 줄임.
        double ratio = DOWNSCALE_IMAGE_SIZE / Math.max(src.width(), src.height());
        Size downscaledSize = new Size(src.width() * ratio, src.height() * ratio);
        Mat downscaled = new Mat(downscaledSize, src.type());
        Imgproc.resize(src, downscaled, downscaledSize);

        List<MatOfPoint2f> rectangles = getPoints(downscaled);
        if (rectangles.size() == 0) {
            return null;
        }

        // 가장 큰 ROI 중에서 ratio에 맞춰 다운 스케일.

        Collections.sort(rectangles, AreaDescendingComparator);
        MatOfPoint2f largestRectangle = rectangles.get(0);
        MatOfPoint2f result = mathUtils.scaleRectangle(largestRectangle, 1f / ratio);
        return result;
    }

    //public native float[] getPoints(Bitmap bitmap);
    public List<MatOfPoint2f> getPoints(Mat src) {

        // 이미지 노이즈를 줄이기 위해서 블러링 처리
        Mat blurred = new Mat();
        Imgproc.medianBlur(src, blurred, 9);

        // 컬러 이미지는 많은 연산으로 요구함. 흑백 이미지로 변환하여 처리.
        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U);
        Mat gray = new Mat();

        Bitmap dummy = ImageUtils.matToBitmap(gray0);

        // Core.mixChannels.
        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint2f> rectangles = new ArrayList<>();

        List<Mat> sources = new ArrayList<>();
        sources.add(blurred);
        List<Mat> destinations = new ArrayList<>();
        destinations.add(gray0);

        // 사각형 filter
        int srcArea = src.rows() * src.cols();

        // 모든 경계를 탐색
        for (int c = 0; c < 3; c++) {
            int[] ch = {c, 0};
            MatOfInt fromTo = new MatOfInt(ch);

            Core.mixChannels(sources, destinations, fromTo);

            // 임계값에 따라 처리가 다름.
            for (int l = 0; l < THRESHOLD_LEVEL; l++) {
                if (l == 0) {
                    // 0일 떄 캐니 알고리즘을 통해 처리함
                    // 자바 API에서는 커널 크기가 지정할 수 없음. (c++에서는 소벨 커널 크기 지정 파라미터 존재)
                    Imgproc.Canny(gray0, gray, 20, 30);

                    // Canny 알고리즘으로 얻은 이미지에 노이즈를 제거함(필터 내부의 가장 밝은 값으로 변환) -> 흰색 강
                    Imgproc.dilate(gray, gray, Mat.ones(new Size(5, 5), 0));
                } else {

                    // 임계값 이상부터 전부 이진화 처리(검정 or 흰색)
                    int threshold = (l + 1) * 255 / THRESHOLD_LEVEL;
                    Imgproc.threshold(gray0, gray, threshold, 255, Imgproc.THRESH_BINARY);
                }

                // 테두리 처리된 ROI들이 리스트에 저장됨. (이미지에 따라 영역이 다앙하게 나온다. index을 통해 접근 가능)
                Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                //for debugging.
                Bitmap a = ImageUtils.matToBitmap(gray);

                for (MatOfPoint contour : contours) {
                    MatOfPoint2f contourFloat = mathUtils.toMatOfPointFloat(contour);
                    double arcLen = Imgproc.arcLength(contourFloat, true) * 0.02;

                    /*
                    *   이미지 윤곽선을 얻었다면 불필요한 윤관선을 얻을 수 있다.
                    *   얻은 윤관의 부분을 다각형으로 근사하여 단순하게 처리할 수 있음.
                     */
                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(contourFloat, approx, arcLen, true);

                    if (isRectangle(approx, srcArea)) {
                        rectangles.add(approx);
                    }
                }
            }
        }

        return rectangles;

    }

    private boolean isRectangle(MatOfPoint2f polygon, int srcArea) {
        MatOfPoint polygonInt = mathUtils.toMatOfPointInt(polygon);

        if (polygon.rows() != 4) {
            return false;
        }

        double area = Math.abs(Imgproc.contourArea(polygon));
        if (area < srcArea * AREA_LOWER_THRESHOLD || area > srcArea * AREA_UPPER_THRESHOLD) {
            return false;
        }

        if (!Imgproc.isContourConvex(polygonInt)) {
            return false;
        }

        // 코사인 값( 72.5도) 0.3을 넘으면 안됨. -> 이미지 변환 시 에러 발생
        double maxCosine = 0;
        Point[] approxPoints = polygon.toArray();

        for (int i = 2; i < 5; i++) {
            double cosine = Math.abs(mathUtils.angle(approxPoints[i % 4], approxPoints[i - 2], approxPoints[i - 1]));
            maxCosine = Math.max(cosine, maxCosine);
        }

        if (maxCosine >= 0.3) {
            return false;
        }

        return true;
    }


    public static Bitmap imgToBW(Bitmap bitmap){
        Mat mat = ImageUtils.bitmapToMat(bitmap);

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());
        Imgproc.equalizeHist(gray, dest);
        Bitmap result = ImageUtils.matToBitmap(dest);
        return result;
    }

    public static Bitmap imgToBright(Bitmap bitmap, double alpha, double beta){
        Mat mat = ImageUtils.bitmapToMat(bitmap);
        Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());

        mat.convertTo(dest, -1, alpha, beta);
        Bitmap result = ImageUtils.matToBitmap(dest);
        return result;
    }

}
