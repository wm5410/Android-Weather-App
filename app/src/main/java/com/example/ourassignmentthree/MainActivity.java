package com.example.ourassignmentthree;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderCLient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissionsGranted();
    }

    public void onClickStart(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
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
            //todo get result
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
        //getLastKnownLocation();
    }

    public void permissionNotGranted() {
        Toast.makeText(this, "LOCATION ACCESS NOT GRANTED", Toast.LENGTH_SHORT).show();
    }

//    public void getLastKnownLocation() {
//        fusedLocationProviderCLient = LocationServices.getFusedLocationProviderClient(this);
//        //Ask for location
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationProviderCLient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                displayLocation(location);
//            }
//        });
//    }

//    public void displayLocation(Location location){
//        TextView textViewLocation = findViewById(R.id.tv_location);
//        if(location != null){
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            textViewLocation.setText(latitude + ", " + longitude);
//        }
//        else{
//            textViewLocation.setText("Whoops, something went wrong!");
//        }
//    }
}