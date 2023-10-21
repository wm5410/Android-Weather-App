package com.example.ourassignmentthree;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
/*
 * This activity displays information about a webCamera.
 */
public class WebCameraDetailActivity extends AppCompatActivity {
    //Declare variables
    protected String title;
    protected String latitude;
    protected String longitude;
    protected String city;
    protected String region;
    protected double latitudeDouble;
    protected double longitudeDouble;
    protected boolean isMarkerClicked;
    protected String image;
    /*
     * This is the onCreate method which executes when the activity starts.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camera_detail);
        //GET the id's of the two textViews
        TextView textViewTitle = findViewById(R.id.tv_camera_title);
        TextView textView = findViewById(R.id.tv_location_info);
        //GET the id of the imageView
        ImageView imageView = findViewById(R.id.iv_web_camera_footage);
        //Receive the selected index from the intent
        int selectedIndex = getIntent().getIntExtra("selectedIndex", -1);
        isMarkerClicked = getIntent().getBooleanExtra("isMarkerClicked", false);
        ArrayList<String> cameraTitles = getIntent().getStringArrayListExtra("cameraTitles");
        ArrayList<String> cameraCities = getIntent().getStringArrayListExtra("cameraCities");
        ArrayList<String> cameraRegions = getIntent().getStringArrayListExtra("cameraRegions");
        ArrayList<String> cameraLatitudes = getIntent().getStringArrayListExtra("cameraLatitudes");
        ArrayList<String> cameraLongitudes = getIntent().getStringArrayListExtra("cameraLongitudes");
        ArrayList<String> cameraURLS = getIntent().getStringArrayListExtra("cameraURL");
        //Check if a marker was clicked or a item in the listView was clicked
        if(!isMarkerClicked){
            //Handle the case where a item in the listView was clicked
            if(selectedIndex != -1){
                //Check if the selectedIndex is valid
                if(selectedIndex >= 0 && selectedIndex < cameraTitles.size()){
                    //Store information in variables
                    title = cameraTitles.get(selectedIndex);
                    city = cameraCities.get(selectedIndex);
                    region = cameraRegions.get(selectedIndex);
                    latitude = cameraLatitudes.get(selectedIndex);
                    longitude = cameraLongitudes.get(selectedIndex);
                    image = cameraURLS.get(selectedIndex);
                    //IF latitude and longitude are NOT null, convert them to double
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
                    //Display errors
                    title = "Invalid selection";
                    city = "Invalid selection";
                    region = "Invalid selection";
                    latitude = "Invalid selection";
                    longitude = "Invalid selection";
                    image = "Invalid selection";
                }
            }
            else{
                //Display errors
                title = "Title not found";
                city = "City not found";
                region = "Region not found";
                latitude = "Latitude not found";
                longitude = "Longitude not found";
                image = "Image not found";
            }
            // Create formatted versions of the latitude and longitude to display it as 3 decimal places
            @SuppressLint("DefaultLocale") String formattedLatitude = String.format("%.3f", latitudeDouble);
            @SuppressLint("DefaultLocale") String formattedLongitude = String.format("%.3f", longitudeDouble);
            //Display data in the imageView, textViewTitle and textView
            Picasso.get().load(image).into(imageView);
            textViewTitle.setText(title);
            textView.setText("Latitude: " + formattedLatitude + "\nLongitude: " + formattedLongitude + "\nCity: " + city + "\nRegion: " + region);
        }else {
            // Retrieve data from the intent and store them in variables
            title = getIntent().getStringExtra("title");
            city = getIntent().getStringExtra("city");
            region = getIntent().getStringExtra("region");
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
            image = getIntent().getStringExtra("cameraURL");
            // Display data in the imageView, textViewTitle and textView
            Picasso.get().load(image).into(imageView);
            textViewTitle.setText(title);
            textView.setText("Latitude: " + latitude + "\nLongitude: " + longitude + "\nCity: " + city + "\nRegion: " + region);
        }
    }
}