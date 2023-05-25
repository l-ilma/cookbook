package com.example.cookbook;

import android.content.Context;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cookbook.ui.authentication.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

//NOTE: first run register tests, then login tests!!
@RunWith(AndroidJUnit4.class)
public class LoginActivityTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.cookbook", appContext.getPackageName());
    }

    @Test
    public void testLoginPass() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.login_email))
                .perform(typeText(Configuration.email));
        onView(ViewMatchers.withId(R.id.login_password))
                .perform(typeText(Configuration.password));

        onView(ViewMatchers.withId(R.id.login))
                .perform(click());

        Thread.sleep(4000);
    }

    @Test
    public void testLoginFail() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.login_email))
                .perform(typeText("unknown_user@gmail.com"));
        onView(ViewMatchers.withId(R.id.login_password))
                .perform(typeText("password"));

        onView(ViewMatchers.withId(R.id.login))
                .perform(click());

        Thread.sleep(4000);
    }
}