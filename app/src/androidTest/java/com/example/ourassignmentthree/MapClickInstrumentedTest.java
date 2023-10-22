package com.example.ourassignmentthree;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.KeyEvent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapClickInstrumentedTest  {
    //Make the maps activity the subject of the test
    @Rule
    public ActivityTestRule<MapsActivity> activityTestRule = new ActivityTestRule<>(MapsActivity.class);
    /*
     * This test will simply perform a click on the map
     */
    @Test
    public void testMapClick() {
        // Find the Google Map fragment by its ID and perform a click action
        onView(withId(R.id.map)).perform(click());
    }
    /*
     * This test is more in-depth and makes sure that a range of user inputs are valid
     */
    @Test
    public void testMapInteraction() {
        // Find the Google Map fragment
        onView(withId(R.id.map))
                .perform(click()) // Click on the map
                .perform(doubleClick()) // Double-click on the map
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_PLUS)) // Zoom in
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_MINUS)) // Zoom out
                .perform(ViewActions.swipeLeft()) // Swipe left
                .perform(ViewActions.swipeRight()) // Swipe right
                .perform(ViewActions.swipeUp()) // Swipe up
                .perform(ViewActions.swipeDown()); // Swipe down
        // Perform checks
        onView(withId(R.id.map))
                .check(matches(isDisplayed())) // Check if the map is displayed
                .check(matches(isClickable()));// Check if the map is clickable
    }
}


