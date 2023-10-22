package com.example.ourassignmentthree;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    /*
     * This test is the basic Instrumented test that makes sure that the basic initialisation of the app works
     * It will check that the test files are running in the correct place (ourassignmentthree) and have access to the activities and other files
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.ourassignmentthree", appContext.getPackageName());
    }
}