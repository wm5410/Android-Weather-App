package com.example.ourassignmentthree;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.example.ourassignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    protected double latitude;
    protected double longitude;
    FusedLocationProviderClient fusedLocationProviderCLient;
    Location currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Places API (need to add our key)
        Places.initialize(getApplicationContext(), "AIzaSyA5pUxD_2Xi1s-bga4itPVaq-VblEHmxg8");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        checkPermissionsGranted();

        //Code for autocomplete fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place
                LatLng selectedLocation = place.getLatLng();
                updateMapLocation(selectedLocation.latitude, selectedLocation.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.i("LocationDebug", "An error occurred: " + status);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastKnownLocation();
    }
    public void checkPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission not granted, ask for permission
            askForPermission();
        } else {
            //Permission granted
            permissionGranted();
        }
    }
    public void askForPermission() {
        ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineGranted = result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false);
            boolean coarseGranted = result.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
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
    public void permissionGranted() {
        Toast.makeText(this, "LOCATION ACCESS GRANTED", Toast.LENGTH_SHORT).show();
        getLastKnownLocation();
    }
    public void permissionNotGranted() {
        Toast.makeText(this, "LOCATION ACCESS NOT GRANTED", Toast.LENGTH_SHORT).show();
    }
    public void getLastKnownLocation() {
        fusedLocationProviderCLient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);
        //Ask for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
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
    public void showMarker(LatLng location, String markerTitle){
        //Set the marker to be the orange drawable
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.img_mm_marker_orange);
        //Add marker to the current location and name it "Current Location"
        mMap.addMarker(new MarkerOptions().position(location).icon(customMarker).title(markerTitle));
        //Move the camera to where the current location is
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    // Handle location updates or get last known location here
    private void updateMapLocation(double latitude, double longitude) {
        System.out.println("Selected Location: " + latitude + ", " + longitude);
    }
}
