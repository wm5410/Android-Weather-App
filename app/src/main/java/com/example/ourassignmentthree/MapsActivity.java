package com.example.ourassignmentthree;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ourassignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    protected final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    protected ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderCLient;
    Location currentLocation;
    ArrayList<String> webCameras = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2", "Camera 3", "Camera 4", "Camera 5"));
    RequestQueue queue;
    String url = "https://api.windy.com/webcams/v1";
    CustomBaseAdapter customBaseAdapter;
    /*
     * This creates everything on the maps activity that is shown after the activity starts up.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyA5pUxD_2Xi1s-bga4itPVaq-VblEHmxg8");
        //Gets the id for the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);
        //Check if permissions have been granted
        checkPermissionsGranted();
        //Gets the id of the autocomplete fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        //Set a listener for when a location on the autocomplete fragment is clicked
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place
                moveCameraToPlace(place);
                RequestQueue("9geNKlDpdftqFJ6ytFBTBck1kUrTdM8v", place);
                //Access the listview
                ListView cameraList = (ListView) findViewById(R.id.lv_camera_list);
                //Custom adapter
                customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), webCameras);
                //Bind adapter to listview
                cameraList.setAdapter(customBaseAdapter);
                //On click listener
                AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                        //Start the web camera detail activity
                        Intent intent = new Intent(view.getContext(), WebCameraDetailActivity.class);
                        startActivity(intent);
                    }
                };
                //Add onclick to the listview
                cameraList.setOnItemClickListener(onItemClickListener);
            }
            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.i("LocationDebug", "An error occurred: " + status);
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        TextView responseTextView;
        @Override
        protected String doInBackground(Void... voids) {
            responseTextView = findViewById(R.id.test);
            try {
                String apiKey1 = "2d6d25ab6612f49333551ae60271d591";
                String city = "Hamilton";
                String country = "nz";

                String apiUrl = "https://pro.openweathermap.org/data/2.5/weather?q=" + city + "," + country + "&APPID=" + apiKey1;
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Make a GET request
                urlConnection.setRequestMethod("GET");

                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Disconnect after reading the response
                urlConnection.disconnect();

                return response.toString();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            processWeatherData(result);
        }

        private void processWeatherData(String result) {
            try {
                // Convert the response string to a JSON object
                JSONObject jsonResponse = new JSONObject(result);

                // Extract information from the JSON object
                String cityName = jsonResponse.getString("name");
                JSONObject mainObject = jsonResponse.getJSONObject("main");
                double temperature = mainObject.getDouble("temp");
                JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                String weatherMain = "";
                if (weatherArray.length() > 0) {
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    weatherMain = weatherObject.getString("main");
                }

                // Display the information in the TextView
                String displayText = "City: " + cityName + "\nTemperature: " + temperature + " K \n" + weatherMain;
                responseTextView.setText(displayText);

                //Process the information - display images based on responses

            } catch (Exception e) {
                responseTextView.setText("Error processing JSON: " + e.getMessage());
            }
        }
    }


    private class FetchCameraTask extends AsyncTask<Void, Void, String> {
        TextView responseTextView;
        @Override
        protected String doInBackground(Void... voids) {
            responseTextView = findViewById(R.id.test2);
            try {
                String apiUrl = "https://staging.windy.com/webcams/api/v3/countries?lang=en";

                // Create a URL object
                URL url = new URL(apiUrl);

                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Set request headers
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("x-windy-api-key", "9geNKlDpdftqFJ6ytFBTBck1kUrTdM8v");

                // Get the response code
                int responseCode = connection.getResponseCode();

                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Close the connection
                connection.disconnect();

                // Return the response as a string
                return response.toString();

            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            responseTextView.setText(result);
        }
    }


    public void RequestQueue(String apiKey, Place place) {

        //////////////////////////////////  will code //////////////
        //Go to method that will execute and process the openweathermap api
        new FetchWeatherTask().execute();

        //To get weather data use https://pro.openweathermap.org/data/2.5/weather?q=Hamilton,nz&APPID=2d6d25ab6612f49333551ae60271d591
        //Can try https://history.openweathermap.org/data/2.5/history/city?id=2885679&type=hour&appid=2d6d25ab6612f49333551ae60271d591 However this is not the right one

        new FetchCameraTask().execute();

        /////////////////////////////// justin code ////



        //Instantiate the RequestQueue
        queue = Volley.newRequestQueue(this);

        String urlWithApiKey = url + "?apiKey=" + apiKey + "&location=" + place;
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithApiKey, new Response.Listener<String>() {
            //This method is not executing
            @Override
            public void onResponse(String response) {
                // Handle the response here
                if (webCameras.isEmpty()) {
                    // If the list is empty, initialize it with the first response
                    webCameras = new ArrayList<>();
                    webCameras.add(response);
                } else {
                    // List is not empty, add the new response
                    webCameras.add(response);
                }
                //Notify the adapter of the change
                customBaseAdapter.notifyDataSetChanged();
                //Get the id of the textview
                TextView textView = (TextView) findViewById(R.id.tv_camera_name);
                //Set the text
                textView.setText(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Handle the error
                Log.i("LocationDebug","An error occurred: " + error);
            }
        });
        //Add the request to the queue
        queue.add(stringRequest);
    }
    /*
     * Display location on the map when map loads.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLastKnownLocation();
    }
    /*
     * This will check if permissions were either granted or not.
     */
    public void checkPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission not granted, ask for permission
            askForPermission();
        } else {
            //Permission granted
            permissionGranted();
        }
    }
    /*
     * This will ask the user for location permissions.
     */
    public void askForPermission() {
        ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            boolean fineGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
            boolean coarseGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));
            //Assume fineGranted is never null
            if (fineGranted) {
                //fine permission granted
                permissionGranted();
            } else if (coarseGranted) {
                //coarse permission granted
                permissionGranted();
            } else {
                //No permission has been granted
                permissionNotGranted();
            }
        });
        activityResultLauncher.launch(new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        });
    }
    /*
     * Displays a toast when permission is granted and then calls the getLastKnownLocation.
     */
    public void permissionGranted() {
        Toast.makeText(this, "LOCATION ACCESS GRANTED", Toast.LENGTH_SHORT).show();
        getLastKnownLocation();
    }
    /*
     * Displays toast when permission is not granted.
     */
    public void permissionNotGranted() {
        Toast.makeText(this, "LOCATION ACCESS NOT GRANTED", Toast.LENGTH_SHORT).show();
    }
    /*
     * This will get the last known location of the device.
     */
    public void getLastKnownLocation() {
        fusedLocationProviderCLient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);
        //Ask for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderCLient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    Log.d("LocationDebug", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
                    LatLng newLat = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMarker(newLat, "Current location");
                }
                else{
                    Log.e("LocationDebug", "Location is null");
                }
            }
        });
    }
    /*
     * This will display the marker on the map and move the camera to it.
     */
    public void showMarker(LatLng location, String markerTitle){
        //Set the marker to be the orange drawable
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_marker_orange);
        //Add marker to the current location and name it "Current Location"
        mMap.addMarker(new MarkerOptions().position(location).icon(customMarker).title(markerTitle));
        //Move the camera to where the current location is
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
    /*
     * This will move the camera and add the marker to the new location that was searched.
     */
    public void moveCameraToPlace(Place place){
        if(place != null && mMap != null){
            //Stores the searched location inside the in a variable
            LatLng placeLatLng = place.getLatLng();
            //Stores the name of the new location in a variable
            String placeName = place.getName();
            if(placeLatLng != null){
                //Clear the marker on the old location
                mMap.clear();
                //Display the new location and move camera to it
                showMarker(placeLatLng, placeName);
            }
        }
    }
    /*
     * Display nearby web cameras.
     */
//    public void showCameraMarker(LatLng location, String cameraMarkerTitle){
//        //Set the marker
//        BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_camera_teal);
//        //Add marker to the current location
//        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(cameraMarkerTitle));
//    }
//
//    public void showWeatherMarker(String weather, LatLng location, String weatherMarkerTitle){
//        if(Objects.equals(weather, "clear")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_clear);
//            //Add marker to the current location
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//        else if(Objects.equals(weather, "clouds")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_clouds);
//            //Add marker to the current location and name it "Current Location"
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//        else if(Objects.equals(weather, "drizzle")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_drizzle);
//            //Add marker to the current location and name it "Current Location"
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//        else if(Objects.equals(weather, "rain")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_rain);
//            //Add marker to the current location and name it "Current Location"
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//        else if(Objects.equals(weather, "snow")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_snow);
//            //Add marker to the current location and name it "Current Location"
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//        else if(Objects.equals(weather, "thunderstorm")){
//            //Set the marker
//            BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_thunderstorm);
//            //Add marker to the current location and name it "Current Location"
//            mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title(weatherMarkerTitle));
//        }
//    }
}
