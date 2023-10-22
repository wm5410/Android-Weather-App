package com.example.ourassignmentthree;

import static org.mockito.Mockito.verify;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class MapsActivityUnitTest {
    //Declare variables
    @Mock
    GoogleMap googleMap;
    private MapsActivity mapsActivity;
    private ActivityController<MapsActivity> activityController;

    /*
     * Things to do to set up the test
     * This uses roboelectric and mockito and they need to be initialised
     */
    @Before
    public void setUp() {
        // Initialize your mocks
        googleMap = Mockito.mock(GoogleMap.class);
        activityController = Robolectric.buildActivity(MapsActivity.class).create().start();
        mapsActivity = activityController.get();
    }

    /*
     * This is the method and main test that a marker has been added
     */
    @Test
    public void showMarker_ShouldAddMarkerToMap() {
        // Test values for location
        LatLng location = new LatLng(-37.784390, 175.294224);
        String markerTitle = "Test Marker";

        // Call showMarker method
        mapsActivity.showMarker(location, markerTitle);

        // Verify that the addMarker method of GoogleMap is called
        verify(googleMap).addMarker(Mockito.any(MarkerOptions.class));
    }
}
