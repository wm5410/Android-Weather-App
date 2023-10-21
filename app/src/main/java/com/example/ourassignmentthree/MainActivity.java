package com.example.ourassignmentthree;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/*
 * This is the main activity of the application. It appears when the app is launched.
 */
public class MainActivity extends AppCompatActivity {
    /*
     * This creates everything on the main activity that is shown after the activity starts up.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /*
     * This starts up the maps activity.
     */
    public void onClickStart(View view){
        //Create an intent
        Intent intent = new Intent(this, MapsActivity.class);
        //Start the new activity
        startActivity(intent);
    }
}