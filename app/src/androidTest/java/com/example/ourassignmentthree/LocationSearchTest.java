package com.example.ourassignmentthree;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocationSearchTest {
    //Make the maps activity the subject of the test
    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule<>(MapsActivity.class);

    @Before
    public void setUp() {
    }

    /*
     * This test will make sure the UI works and that a place can be entered into the search bar and the correct result is displayed
     */
    @Test
    public void testLocationSearch() throws InterruptedException {
        onView(withId(R.id.autocomplete_fragment))
                .perform(click());  // Click search bar

            // Type "Hamilton" into the search field
            onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(typeText("Hamilton New Zealand"), closeSoftKeyboard());

            Thread.sleep(2000);

            // Click the first suggestion
            onView(withText("Hamilton")).perform(ViewActions.click());

            Thread.sleep(2000);

            //Could not figure out the code to verify the place so the tester must verify themselves
    }

    /*
     * Another test to check that the correct API information is received
     * This will look through the listview to verify
     */
    @Test
    public void testLocationAPIResults() throws InterruptedException {
        onView(withId(R.id.autocomplete_fragment))
                .perform(click());  // Click search bar

        // Type "Hamilton" into the search field
        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(typeText("Hamilton New Zealand"), closeSoftKeyboard());

        Thread.sleep(2000);

        // Click the first suggestion
        onView(withText("Hamilton")).perform(ViewActions.click());

        Thread.sleep(2000);

        //Verify the output
        onData(withText("Hamilton")).inAdapterView(withId(R.id.lv_camera_list)).check(matches(isDisplayed()));
    }
}


