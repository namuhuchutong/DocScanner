package com.example.docscanner.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

public class ImgConstants {

    /*
        폴더 경로 및 이미지 전달 확인 상수

        static 변수는 메모리에 할당이 되면 종료될 때까지 계속 남아 있음. -> 원본 이미지 초기화 변수로 활용
        -> 단점 : (추측) 가상 에뮬레이터에서 메모리 공간을 추가 할당할 때 시간 지연이 발생.
                메모리 할당이 안 된 상태에서 이미지 연산을 시작하면 Null 참조 에러를 받음.

        -> 개선 방안 1 : 모든 이미지를 전달할 때, intent에 압축된 바이트로 전달.
            단점 : 이미지를 전달할 때 마다 바이트를 다시 matrix로 변환해야 함.
     */

    public final static String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "imagedir";
    public final static int PICK_IMAGE = 1001;
    public final static int GET_IMAGE = 1002;
    public static Bitmap selectedimgBitmap;
}
