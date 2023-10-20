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

    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule<>(MapsActivity.class);


    @Before
    public void setUp() {
    }

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
    }

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

        // Assuming your ListView is in the MapsActivity layout, find it by its ID
//        onView(withId(R.id.lv_camera_list))
//                .check(matches(ViewMatchers.isDisplayed()));

        onData(withText("east")).inAdapterView(withId(R.id.lv_camera_list)).check(matches(isDisplayed()));

        // Check if the word "east" exists in any item of the ListView
        //onData(allOf(is(instanceOf(String.class)), containsString("east"))).check(matches(isDisplayed()));

    }

}


