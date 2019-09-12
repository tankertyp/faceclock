package com.pingfly.faceclock;

import android.content.Context;


import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Instrumented ring_face_clock_default, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under ring_face_clock_default.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.pingfly.faceclock", appContext.getPackageName());
    }
}
