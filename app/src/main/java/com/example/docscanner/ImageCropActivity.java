package com.example.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.Gravity;
import android.graphics.drawable.BitmapDrawable;


import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.NativeClass;
import com.example.docscanner.utils.PolygonView;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageCropActivity extends Activity{

    FrameLayout holder;
    ImageView imgView;
    PolygonView polygonView;
    Bitmap selectedImgBitmap;
    Button btnImgEnhace;

    NativeClass nativeClass;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        initalizeElement();

    }

    private void initalizeElement() {
        nativeClass = new NativeClass();
        btnImgEnhace = (Button) findViewById(R.id.btnImageEnhance);
        holder = (FrameLayout) findViewById(R.id.holderImageCrop);
        imgView = (ImageView) findViewById(R.id.imageView);
        polygonView = (PolygonView) findViewById(R.id.polygonView);

        holder.post(new Runnable() {
            @Override
            public void run() {
                initalizeCropping();
            }
        });
        btnImgEnhace.setOnClickListener(btnImgEnhaceClick);
    }

    private void initalizeCropping(){
        selectedImgBitmap = ImgConstants.selectedimgBitmap;
        ImgConstants.selectedimgBitmap = null;

        Bitmap scaledBitmap = scaledBitmap(selectedImgBitmap, holder.getWidth(), holder.getHeight());
        imgView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);

        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);

        int padding = 16;

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;

        polygonView.setLayoutParams(layoutParams);
    }

    private View.OnClickListener btnImgEnhaceClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v){
            ImgConstants.selectedimgBitmap = getCroppedImage();
            Intent intent = new Intent(getApplicationContext(), ImageEnhaceActivity.class);
            startActivity(intent);
        }
    };

    protected Bitmap getCroppedImage() {

        Map<Integer, PointF> points = polygonView.getPoints();

        float xRatio = (float) selectedImgBitmap.getWidth() / imgView.getWidth();
        float yRatio = (float) selectedImgBitmap.getHeight() / imgView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;

        return nativeClass.getScannedBitmap(selectedImgBitmap, x1, y1, x2, y2, x3, y3, x4, y4);

    }


    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height){

        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getHeight(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap){

       List<PointF> pointFs = getContourEdgePoints(tempBitmap);
       Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
       return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap){

        MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
        List<Point> points = Arrays.asList(point2f.toArray());

        List<PointF> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
        }

        return result;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap){

        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints))
        {
            orderedPoints = getOutlinePoints(tempBitmap);
        }

        return orderedPoints;
    }
}
