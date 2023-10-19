package com.example.ourassignmentthree;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class WebCameraDetailActivity extends AppCompatActivity {
    protected String title;
    protected int viewCount;
    protected int webcamId;
    protected String status;
    protected String lastUpdatedOn;
    protected double latitude;
    protected double longitude;
    protected String city;
    protected String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camera_detail);
        //GET the id's of the two textViews
        TextView textViewTitle = findViewById(R.id.tv_camera_title);
        TextView textView = findViewById(R.id.tv_location_info);
        //GET data from the Maps Activity
        title = getIntent().getStringExtra("title");
        city = getIntent().getStringExtra("city");
        region = getIntent().getStringExtra("region");
        latitude = getIntent().getDoubleExtra("latitude", 1);
        longitude = getIntent().getDoubleExtra("longitude", 1);
        //Create formatted versions of the latitude and longitude to display it as 3 decimal places
        @SuppressLint("DefaultLocale") String formattedLatitude = String.format("%.3f", latitude);
        @SuppressLint("DefaultLocale") String formattedLongitude = String.format("%.3f", longitude);
        //Display in the textViews
        textViewTitle.setText(title);
        textView.setText("Latitude: " + formattedLatitude + "\n" + "Longitude: " + formattedLongitude + "\n" + "City: " + city + "\n" + "Region: " + region);
    }
}