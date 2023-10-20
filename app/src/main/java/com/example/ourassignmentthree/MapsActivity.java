package com.example.ourassignmentthree;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.model.Marker;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //Declare variables
    protected final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    protected ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderCLient;
    Location currentLocation;
    String[] webCameras = new String[]{};
    CustomBaseAdapter customBaseAdapter;
    ListView cameraList;
    List<String> cameraTitles = new ArrayList<>();
    List<String> cameraCities = new ArrayList<>();
    List<String> cameraRegions = new ArrayList<>();
    List<String> cameraLatitudes = new ArrayList<>();
    List<String> cameraLongitudes = new ArrayList<>();
    boolean isMarkerClicked = false;
    private int blah = 0;
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
                //Create and set variables
                LatLng latLng = place.getLatLng();
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                //Access the listview
                cameraList = (ListView) findViewById(R.id.lv_camera_list);
                // Handle the selected place
                moveCameraToPlace(place);
                //Call the methods to get weather info and camera info
                new FetchWeatherTask(latitude, longitude).execute();
                FetchCameraTask cam = (FetchCameraTask) new FetchCameraTask(latitude, longitude).execute();
                //On click listener
                AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                        //Set isMarkerClicked to false
                        isMarkerClicked = false;
                        //Create an intent
                        Intent intent = new Intent(view.getContext(), WebCameraDetailActivity.class);
                        //Put extra information so the WebCameraDetail Activity can access it
                        intent.putStringArrayListExtra("cameraTitles", (ArrayList<String>) cameraTitles);
                        intent.putStringArrayListExtra("cameraCities", (ArrayList<String>) cameraCities);
                        intent.putStringArrayListExtra("cameraRegions", (ArrayList<String>) cameraRegions);
                        intent.putStringArrayListExtra("cameraLatitudes", (ArrayList<String>) cameraLatitudes);
                        intent.putStringArrayListExtra("cameraLongitudes", (ArrayList<String>) cameraLongitudes);
                        intent.putExtra("selectedIndex", index);
                        intent.putExtra("webcamId", "your_webcam_id");
                        intent.putExtra("isMarkerClicked", isMarkerClicked);
                        //Start the activity
                        startActivity(intent);
                    }
                };
                //Add onclick to the listview
                cameraList.setOnItemClickListener(onItemClickListener);
                //Add onclick listener for camera markers
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //GET the tag for the marker
                        String markerTag = (String) marker.getTag();
                        //Get the index of the camera selected
                        String cameraIndex = marker.getTitle();
                        //Set isMarkerClicked to true
                        isMarkerClicked = true;
                        //Create an intent
                        Intent intent = new Intent(MapsActivity.this, WebCameraDetailActivity.class);
                        //Put extra information so the WebCameraDetail Activity can access it
                        intent.putExtra("isMarkerClicked", isMarkerClicked);
                        intent.putExtra("webcamId", "your_webcam_id");

                        if("Camera 0".equals(cameraIndex)){
                            String Title = cameraTitles.get(0);
                            String City = cameraCities.get(0);
                            String Region = cameraRegions.get(0);
                            String Latitude = cameraLatitudes.get(0);
                            String Longitude = cameraLongitudes.get(0);
                            intent.putExtra("title", Title);
                            intent.putExtra("city", City);
                            intent.putExtra("region", Region);
                            intent.putExtra("latitude", Latitude);
                            intent.putExtra("longitude", Longitude);
                        }
                        else if("Camera 1".equals(cameraIndex)){
                            String Title = cameraTitles.get(1);
                            String City = cameraCities.get(1);
                            String Region = cameraRegions.get(1);
                            String Latitude = cameraLatitudes.get(1);
                            String Longitude = cameraLongitudes.get(1);
                            intent.putExtra("title", Title);
                            intent.putExtra("city", City);
                            intent.putExtra("region", Region);
                            intent.putExtra("latitude", Latitude);
                            intent.putExtra("longitude", Longitude);
                        }
                        else if("Camera 2".equals(cameraIndex)){
                            String Title = cameraTitles.get(2);
                            String City = cameraCities.get(2);
                            String Region = cameraRegions.get(2);
                            String Latitude = cameraLatitudes.get(2);
                            String Longitude = cameraLongitudes.get(2);
                            intent.putExtra("title", Title);
                            intent.putExtra("city", City);
                            intent.putExtra("region", Region);
                            intent.putExtra("latitude", Latitude);
                            intent.putExtra("longitude", Longitude);
                        }
                        else if("Camera 3".equals(cameraIndex)){
                            String Title = cameraTitles.get(3);
                            String City = cameraCities.get(3);
                            String Region = cameraRegions.get(3);
                            String Latitude = cameraLatitudes.get(3);
                            String Longitude = cameraLongitudes.get(3);
                            intent.putExtra("title", Title);
                            intent.putExtra("city", City);
                            intent.putExtra("region", Region);
                            intent.putExtra("latitude", Latitude);
                            intent.putExtra("longitude", Longitude);
                        }
                        else if("Camera 4".equals(cameraIndex)){
                            String Title = cameraTitles.get(4);
                            String City = cameraCities.get(4);
                            String Region = cameraRegions.get(4);
                            String Latitude = cameraLatitudes.get(4);
                            String Longitude = cameraLongitudes.get(4);
                            intent.putExtra("title", Title);
                            intent.putExtra("city", City);
                            intent.putExtra("region", Region);
                            intent.putExtra("latitude", Latitude);
                            intent.putExtra("longitude", Longitude);
                        }



                        //Check if the tag matches with the camera teal so that the activity will only start when clicked on a camera marker
                        if("img_mm_camera_teal".equals(markerTag)){
                            startActivity(intent);
                        }
                        return false;
                    }
                });
                //Clear the camera lists
                cameraTitles.clear();
                cameraCities.clear();
                cameraRegions.clear();
                cameraLatitudes.clear();
                cameraLongitudes.clear();
            }
            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.i("LocationDebug", "An error occurred: " + status);
            }
        });
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
        //Set the fusedLocationProviderClient
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
                if (location != null) {
                    //Set currentLocation to location
                    currentLocation = location;
                    //Create a new LatLng variable and show the marker
                    LatLng newLat = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMarker(newLat, "Current location");
                } else {
                    //Display error in the output
                    Log.e("LocationDebug", "Location is null");
                }
            }
        });
    }
    /*
     * This will display the marker on the map and move the camera to it.
     */
    public void showMarker(LatLng location, String markerTitle) {
        //Set the marker to be the orange drawable
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_marker_orange);
        //Add marker to the current location and name it "Current Location"
        mMap.addMarker(new MarkerOptions().position(location).icon(customMarker).title(markerTitle).snippet("Nice place! :)"));
        //Move the camera to where the current location is
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }
    /*
     * This will move the camera and add the marker to the new location that was searched.
     */
    public void moveCameraToPlace(Place place) {
        if (place != null && mMap != null) {
            //Stores the searched location inside the in a variable
            LatLng placeLatLng = place.getLatLng();
            //Stores the name of the new location in a variable
            String placeName = place.getName();
            if (placeLatLng != null) {
                //Clear the marker on the old location
                mMap.clear();
                //Display the new location and move camera to it
                showMarker(placeLatLng, placeName);
            }
        }
    }
    /*
     * This is a class that gets the weather information
     */
    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        //Declare variable
        protected double latitude;
        protected double longitude;
        /*
         * This is the constructor which initialises two variables.
         */
        public FetchWeatherTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        /*
         * This method will use the api key and make requests returning a response.
         */
        @Override
        protected String doInBackground(Void... voids) {
            try {
                //Create and set variables
                String apiKey1 = "2d6d25ab6612f49333551ae60271d591";
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey1;
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // Make a GET request
                urlConnection.setRequestMethod("GET");
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                //Read through the input line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Disconnect after reading the response
                urlConnection.disconnect();
                return response.toString();
            } catch (Exception e) {
                //Display error message
                return "Error: " + e.getMessage();
            }
        }
        /*
         * Calls the processWeatherData method and parses a string variable.
         */
        @Override
        protected void onPostExecute(String result) {
            processWeatherData(result);
        }
        /*
         * This method gets the information of the weather and displays it.
         */
        private void processWeatherData(String result) {
            try {
                // Convert the response string to a JSON object
                JSONObject jsonResponse = new JSONObject(result);
                // Extract information from the JSON object
                JSONObject mainObject = jsonResponse.getJSONObject("main");
                double temperature = mainObject.getDouble("temp");
                JSONObject coordObject = jsonResponse.getJSONObject("coord");
                double longitude = coordObject.getDouble("lon");
                double latitude = coordObject.getDouble("lat");
                JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                String weatherMain = "";
                //Offset the longitude so it won't be covered by the location marker
                longitude += 0.03;
                //If the array is NOT empty
                if (weatherArray.length() > 0) {
                    //Create a JSONObject
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    //Set the string variable
                    weatherMain = weatherObject.getString("main");
                }
                // Format latitude and longitude to display only two decimal places and set a string variable
                @SuppressLint("DefaultLocale") String formattedLatitude = String.format("%.2f", latitude);
                @SuppressLint("DefaultLocale") String formattedLongitude = String.format("%.2f", longitude);
                @SuppressLint("DefaultLocale") String formattedTemperature = String.format("%.2f", temperature);
                String snippet = "Latitude: " + formattedLatitude + ", Longitude: " + formattedLongitude + ", Temperature: " + formattedTemperature;
                //Check weather conditions and add custom markers
                if(weatherMain.equalsIgnoreCase("Rain")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_rain);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Rainy").snippet(snippet));
                    }
                }
                else if(weatherMain.equalsIgnoreCase("Clouds")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_clouds);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Cloudy").snippet(snippet));
                    }
                }
                else if(weatherMain.equalsIgnoreCase("Clear")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_clear);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Sunny").snippet(snippet));
                    }
                }
                else if(weatherMain.equalsIgnoreCase("drizzle")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_drizzle);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Drizzling").snippet(snippet));
                    }
                }
                else if(weatherMain.equalsIgnoreCase("snow")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_snow);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Snow").snippet(snippet));
                    }
                }
                else if(weatherMain.equalsIgnoreCase("Thunderstorm")){
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_weather_thunderstorm);
                    //Add marker to the location
                    if(mMap != null){
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).icon(cameraMarker).title("Thunderstorm").snippet(snippet));
                    }
                }
            } catch (Exception e) {
                //Display error message
                Log.d("Error processing JSON: ", e.getMessage());
            }
        }
    }
    /*
     * This is a class that gets the information for web cameras.
     */
    private class FetchCameraTask extends AsyncTask<Void, Void, String> {
        //Declare variable
        protected double latitude;
        protected double longitude;
        protected String city;
        protected String region;
        protected String title;
        /*
         * This is the constructor which sets a latitude and longitude variable.
         */
        public FetchCameraTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        /*
         * This method will use the api key and make requests returning a response.
         */
        @Override
        protected String doInBackground(Void... voids) {
            try {
                //Set url
                String apiUrl = "https://api.windy.com/webcams/api/v3/webcams?lang=en&limit=5&offset=0&categoryOperation=and&sortKey=popularity&sortDirection=asc&nearby="+ latitude + "%2C" + longitude+ "%2C100&include=categories&continents=OC&categories=traffic";
                // Create a URL object
                URL url = new URL(apiUrl);
                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Set the request method to GET
                connection.setRequestMethod("GET");
                // Set request headers
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("x-windy-api-key", "9geNKlDpdftqFJ6ytFBTBck1kUrTdM8v");
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                //Read through the input line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Close the connection
                connection.disconnect();
                // Return the response as a string
                return response.toString();
            } catch (Exception e) {
                //Display error message
                return "Error: " + e.getMessage();
            }
        }
        /*
         * Calls the processWebcamData and parses a string variable.
         */
        @Override
        protected void onPostExecute(String result) {
            // Handle the result
            processWebcamData(result);
        }
        /*
         * This method will process the webCam data.
         */
        private void processWebcamData(String result) {
            try {
                //Create JSONObject and JSONArray variables
                JSONObject jsonObject = new JSONObject(result);
                JSONArray webcams = jsonObject.getJSONArray("webcams");
                //Create a new arrayList to store the cameras
                List<String> webCamerasList = new ArrayList<>();
                //Set blah to zero
                blah = 0;
                for (int i = 0; i < webcams.length(); i++) {
                    //Get the item
                    JSONObject webcam = webcams.getJSONObject(i);
                    //Store its title in a string variable
                    title = webcam.getString("title");
                    //Store its webcamId in a string variable
                    String webcamId = webcam.getString("webcamId");
                    //Add to the new arrayList
                    webCamerasList.add(title);
                    //Add the title to the list to store it there
                    cameraTitles.add(title);
                    //Get location information
                    new GetLocation(webcamId, blah).execute();
                    new GetImage(webcamId).execute();
                    if(blah < 4){
                        //Increment the index
                        blah++;
                    }
                }
                //Convert the webCamerasList to a String[]
                webCameras = webCamerasList.toArray(new String[0]);
                //Set the custom adapter to the webCameras list
                customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), webCameras);
                cameraList.setAdapter(customBaseAdapter);
                //Loop through each camera title in the webCameras array
                StringBuilder cameraInfoBuilder = new StringBuilder();
                for (String camera : webCameras) {
                    cameraInfoBuilder.append(camera).append("\n"); // Add a new line for each camera
                }
            } catch (Exception e) {
                // Print detailed error message
                Log.d("Error processing JSON: ", e.getMessage());
            }
        }
        /*
         * This is a class used to get the location of the webCameras.
         */
        private class GetLocation extends AsyncTask<Void, Void, String> {
            //Declare variables
            protected String id;
            protected int index;
            protected double Latitude;
            protected double Longitude;
            /*
             * This is the constructor which sets an id.
             */
            public GetLocation(String ID, int index) {
                this.id = ID;
                this.index = index;
            }
            /*
             * This method process the api key and url.
             */
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //Set url
                    String apiUrl = "https://api.windy.com/webcams/api/v3/webcams/" + id + "?lang=en&include=location";
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
                    //Read through the input line
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    // Close the connection
                    connection.disconnect();
                    return response.toString();
                } catch (Exception e) {
                    //Display error message
                    return "Error: " + e.getMessage();
                }
            }
            /*
             * Calls the processWebcamLocation method and execute it.
             */
            @Override
            protected void onPostExecute(String result) {
                // Handle the result
                processWebcamLocation(result);
            }
            /*
             * This method handles the webCameras and displays them on the map.
             */
            private LatLng processWebcamLocation(String result)
            {
                try{
                    //Create new json object
                    JSONObject json = new JSONObject(result);
                    Log.d("jsonObject", json.toString());
                    // Extract latitude and longitude
                    JSONObject location = json.getJSONObject("location");
                    Latitude = location.getDouble("latitude");
                    Longitude = location.getDouble("longitude");
                    city = location.getString("city");
                    region = location.getString("region");
                    //Add variables to their respective arrayLists to store
                    cameraCities.add(city);
                    cameraRegions.add(region);
                    cameraLatitudes.add(String.valueOf(Latitude));
                    cameraLongitudes.add(String.valueOf(Longitude));
                    //Set a LatLng object for the location of the camera
                    LatLng webcamLocation = new LatLng(Latitude, Longitude);
                    //Set the marker with the custom icon
                    BitmapDescriptor cameraMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_camera_teal);
                    //Add marker to the location
                    if(mMap != null){
                        (mMap.addMarker(new MarkerOptions().position(webcamLocation).icon(cameraMarker).title("Camera " + index))).setTag("img_mm_camera_teal");
                    }
                    return new LatLng(Latitude, Longitude);
                }
                catch (Exception e) {
                    return null;
                }
            }
        }
        /*
         * This method handles everything for the images captured by the webcams.
         */
        private class GetImage extends AsyncTask<Void, Void, String> {
            //Declare variables
            protected String id;
            /*
             * This is the constructor of the class and it sets an id.
             */
            public GetImage(String ID) {
                this.id = ID;
            }
            /*
             * This method process the api key and url.
             */
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //Set url
                    String apiUrl = "https://api.windy.com/webcams/api/v3/webcams/" + id + "?lang=en&include=urls";
                    // Create a URL object
                    URL url = new URL(apiUrl);
                    // Open a connection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // Set the request method to GET
                    connection.setRequestMethod("GET");
                    // Set request headers
                    connection.setRequestProperty("accept", "application/json");
                    connection.setRequestProperty("x-windy-api-key", "9geNKlDpdftqFJ6ytFBTBck1kUrTdM8v");
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    //Read through the input line
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    // Close the connection
                    connection.disconnect();
                    return response.toString();
                } catch (Exception e) {
                    //Display error message
                    return "Error: " + e.getMessage();
                }
            }
            /*
             * Calls the processImg method and executes it.
             */
            @Override
            protected void onPostExecute(String result) {
                // Handle the result
                processImg(result);
            }
            /*
             * This method handles the images and displays them in the webCameraDetailActivity.
             */
            private String processImg(String result)
            {
                try{
                    //Create a JSONObject
                    JSONObject json = new JSONObject(result);
                    //Get the urls
                    JSONObject urls = json.getJSONObject("urls");
                    // Get the "edit" URL
                    String editUrl = urls.getString("edit");

                    return editUrl;
                }
                catch (Exception e) {
                    return null;
                }
            }
        }
    }
}
