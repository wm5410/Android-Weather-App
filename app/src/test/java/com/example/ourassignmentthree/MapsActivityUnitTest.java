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

    @Mock
    GoogleMap googleMap;

    private MapsActivity mapsActivity;
    private ActivityController<MapsActivity> activityController;

    @Before
    public void setUp() {
        // Initialize your mocks
        googleMap = Mockito.mock(GoogleMap.class);

        // Create an instance of MapsActivity with Robolectric
        activityController = Robolectric.buildActivity(MapsActivity.class).create().start();
        mapsActivity = activityController.get();
    }

    @Test
    public void showMarker_ShouldAddMarkerToMap() {
        // Assuming you have a LatLng and title to test with
        LatLng location = new LatLng(-37.784390, 175.294224);
        String markerTitle = "Test Marker";

        // Call your showMarker method
        mapsActivity.showMarker(location, markerTitle);

        // Verify that the addMarker method of GoogleMap is called
        verify(googleMap).addMarker(Mockito.any(MarkerOptions.class));
    }
}
