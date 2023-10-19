package com.example.ourassignmentthree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WebCameraDetailActivity extends AppCompatActivity {
    private String title;
    private int viewCount;
    private int webcamId;
    private String status;
    private String lastUpdatedOn;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camera_detail);


        //textView.findViewById(R.id.tv_location_info);
        Intent intent = getIntent();
        String webcamId = intent.getStringExtra("webcamId");
        double latitude = intent.getDoubleExtra("latitude", 1);
        double longitude = intent.getDoubleExtra("longitude", 1);

        //textView.setText(latitude + " " + longitude);
    }
}