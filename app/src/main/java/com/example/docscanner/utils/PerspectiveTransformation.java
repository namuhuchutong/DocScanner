package com.example.docscanner.utils;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class PerspectiveTransformation {

    private static final String DEBUG_TAG = "PerspectiveTransformation";

    public PerspectiveTransformation() {
    }

    /*
    *
    *   Mat 객체는 n-차원 or 1차원 or n-채널을 나타내는 배열.
    *   백터, 이미지, 행렬, 점 집합, 텐서, 히스토그램 등 다양한 것을 저장 가능.
    *
    *   아래에서 쓰이는 Mat은 Bitmap(원본 이미지)를 Mat 형태로 변환하여 사용.
    *
    *   @ Bitmap : 비트로 맵핑된 이미
     */

    public Mat transform(Mat src, MatOfPoint2f corners) {

        /*
        *   @ MatOfPoint2f : 채널이 2인 점(x,y)들의 집합이 Mat으로 된 형태
        *   @ Channel : 흑백 (1채널), RGB(3채널)
        *
        *   MatOfPoint2f -> Mat 클래스를 상속받음. 단, 채널이 2개.
        *
        *   파라미터로 받은 코너를 정렬한 뒤 이미지 변환.
         */

        MatOfPoint2f sortedCorners = sortCorners(corners);
        Size size = getRectangleSize(sortedCorners);

        Mat result = Mat.zeros(size, src.type());
        MatOfPoint2f imageOutline = getOutline(result);

        // Perspective Transform : 이미지의 경계선(이미지 가장 끝 모서리들)에 테두리를 맵핑(ROI로 지정된 영역 중 가장 끝 모서리들)
        Mat transformation = Imgproc.getPerspectiveTransform(sortedCorners, imageOutline);
        // wrapPerspetive : 회전, 평행이동, 스케일을 포함한 변환 작업. 원본 이미지는 tranformation에 지정된 맵핑으로 변환되어 result에 저장.
        // 크기는 ROI로 지정된 영역만큼.
        Imgproc.warpPerspective(src, result, transformation, size, Imgproc.INTER_LINEAR);

        Bitmap dummy = ImageUtils.matToBitmap(result);
        return result;
    }

    private Size getRectangleSize(MatOfPoint2f rectangle) {

        /*
        *   ROI 영역의 크기를 계산.
         */

        Point[] corners = rectangle.toArray();

        double top = getDistance(corners[0], corners[1]);
        double right = getDistance(corners[1], corners[2]);
        double bottom = getDistance(corners[2], corners[3]);
        double left = getDistance(corners[3], corners[0]);

        double averageWidth = (top + bottom) / 2f;
        double averageHeight = (right + left) / 2f;

        return new Size(new Point(averageWidth, averageHeight));
    }

    private double getDistance(Point p1, Point p2) {

            // 점과 점 사이 거리 구하기.

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private MatOfPoint2f getOutline(Mat image) {

        // 원본 이미지 경계선 모서리 점을 정렬 후 반환.

        Point topLeft = new Point(0, 0);
        Point topRight = new Point(image.cols(), 0);
        Point bottomRight = new Point(image.cols(), image.rows());
        Point bottomLeft = new Point(0, image.rows());
        Point[] points = {topLeft, topRight, bottomRight, bottomLeft};

        MatOfPoint2f result = new MatOfPoint2f();
        result.fromArray(points);

        return result;
    }

    private MatOfPoint2f sortCorners(MatOfPoint2f corners) {

        /*
        *   ROI 모서리 정렬.
         */

        Point center = getMassCenter(corners);
        List<Point> points = corners.toList();
        List<Point> topPoints = new ArrayList<Point>();
        List<Point> bottomPoints = new ArrayList<Point>();

        for (Point point : points) {
            if (point.y < center.y) {
                topPoints.add(point);
            } else {
                bottomPoints.add(point);
            }
        }

        Point topLeft = topPoints.get(0).x > topPoints.get(1).x ? topPoints.get(1) : topPoints.get(0);
        Point topRight = topPoints.get(0).x > topPoints.get(1).x ? topPoints.get(0) : topPoints.get(1);
        Point bottomLeft = bottomPoints.get(0).x > bottomPoints.get(1).x ? bottomPoints.get(1) : bottomPoints.get(0);
        Point bottomRight = bottomPoints.get(0).x > bottomPoints.get(1).x ? bottomPoints.get(0) : bottomPoints.get(1);

        MatOfPoint2f result = new MatOfPoint2f();
        Point[] sortedPoints = {topLeft, topRight, bottomRight, bottomLeft};
        result.fromArray(sortedPoints);

        return result;
    }

    private Point getMassCenter(MatOfPoint2f points) {

        // 중심 좌표 구하기.

        double xSum = 0;
        double ySum = 0;
        List<Point> pointList = points.toList();
        int len = pointList.size();
        for (Point point : pointList) {
            xSum += point.x;
            ySum += point.y;
        }
        return new Point(xSum / len, ySum / len);
    }

}