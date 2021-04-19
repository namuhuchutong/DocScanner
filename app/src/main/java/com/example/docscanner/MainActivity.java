package com.example.docscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    static{
        OpenCVLoader.initDebug();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}