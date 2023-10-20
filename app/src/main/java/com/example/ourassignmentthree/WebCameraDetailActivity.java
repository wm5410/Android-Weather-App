package com.example.ourassignmentthree;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class WebCameraDetailActivity extends AppCompatActivity {
    protected String title;
    protected int viewCount;
    protected int webcamId;
    protected String status;
    protected String lastUpdatedOn;
    protected String latitude;
    protected String longitude;
    protected String city;
    protected String region;
    protected double latitudeDouble;
    protected double longitudeDouble;
    protected boolean isMarkerClicked;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camera_detail);
        //GET the id's of the two textViews
        TextView textViewTitle = findViewById(R.id.tv_camera_title);
        TextView textView = findViewById(R.id.tv_location_info);
        //Receive the selected index from the intent
        int selectedIndex = getIntent().getIntExtra("selectedIndex", -1);
        isMarkerClicked = getIntent().getBooleanExtra("isMarkerClicked", false);
        ArrayList<String> cameraTitles = getIntent().getStringArrayListExtra("cameraTitles");
        ArrayList<String> cameraCities = getIntent().getStringArrayListExtra("cameraCities");
        ArrayList<String> cameraRegions = getIntent().getStringArrayListExtra("cameraRegions");
        ArrayList<String> cameraLatitudes = getIntent().getStringArrayListExtra("cameraLatitudes");
        ArrayList<String> cameraLongitudes = getIntent().getStringArrayListExtra("cameraLongitudes");
        //Check if a marker was clicked or a item in the listView was clicked
        if(!isMarkerClicked){
            //Handle the case where a item in the listView is clicked
            if(selectedIndex != -1){
                //Check if the selectedIndex is valid
                if(selectedIndex >= 0 && selectedIndex < cameraTitles.size()){
                    title = cameraTitles.get(selectedIndex);
                    city = cameraCities.get(selectedIndex);
                    region = cameraRegions.get(selectedIndex);
                    latitude = cameraLatitudes.get(selectedIndex);
                    longitude = cameraLongitudes.get(selectedIndex);

                    if(latitude != null && longitude != null){
                        try{
                            latitudeDouble = Double.parseDouble(latitude);
                            longitudeDouble = Double.parseDouble(longitude);
                        }
                        catch(Exception e) {
                            Log.d("Error with latitude and longitude: ", e.getMessage());
                        }
                    }
                }
                else{
                    title = "Invalid selection";
                    city = "Invalid selection";
                    region = "Invalid selection";
                    latitude = "Invalid selection";
                    longitude = "Invalid selection";
                }
            }
            else{
                title = "Title not found";
                city = "City not found";
                region = "Region not found";
                latitude = "Latitude not found";
                longitude = "Longitude not found";
            }
            //GET data from the Maps Activity
            webcamId = getIntent().getIntExtra("webcamId", 0);
            // Create formatted versions of the latitude and longitude to display it as 3 decimal places
            @SuppressLint("DefaultLocale") String formattedLatitude = String.format("%.3f", latitudeDouble);
            @SuppressLint("DefaultLocale") String formattedLongitude = String.format("%.3f", longitudeDouble);

            //Display in the textViews
            textViewTitle.setText(title);
            textView.setText("Latitude: " + formattedLatitude + "\nLongitude: " + formattedLongitude + "\nCity: " + city + "\nRegion: " + region);
        }else {
            // Retrieve data from the intent
            title = getIntent().getStringExtra("title");
            city = getIntent().getStringExtra("city");
            region = getIntent().getStringExtra("region");
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");

            // Display data
            textViewTitle.setText(title);
            textView.setText("Latitude: " + latitude + "\nLongitude: " + longitude + "\nCity: " + city + "\nRegion: " + region);
        }
    }
}