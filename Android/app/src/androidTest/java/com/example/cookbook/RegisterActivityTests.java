package com.example.cookbook;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cookbook.ui.authentication.LoginActivity;
import com.example.cookbook.ui.authentication.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTests {
    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void testRegistrationPass() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.signup_username))
                .perform(typeText(Configuration.username));
        onView(ViewMatchers.withId(R.id.signup_email))
                .perform(typeText(Configuration.email));
        onView(ViewMatchers.withId(R.id.signup_password))
                .perform(typeText(Configuration.password));
        onView(ViewMatchers.withId(R.id.signup))
                .perform(click());

        Thread.sleep(4000);
    }

    @Test
    public void testRegistrationFail() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.signup_username))
                .perform(typeText("username"));
        onView(ViewMatchers.withId(R.id.signup_email))
                .perform(typeText("invalid_email"));
        onView(ViewMatchers.withId(R.id.signup_password))
                .perform(typeText(Configuration.password));
        onView(ViewMatchers.withId(R.id.signup))
                .perform(click());

        Thread.sleep(4000);
    }
}
