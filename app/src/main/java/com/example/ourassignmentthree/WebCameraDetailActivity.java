package com.example.ourassignmentthree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class WebCameraDetailActivity extends AppCompatActivity {
    private String title;
    private int viewCount;
    private int webcamId;
    private String status;
    private String lastUpdatedOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camera_detail);
    }
}