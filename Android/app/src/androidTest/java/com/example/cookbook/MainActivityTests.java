package com.example.cookbook;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cookbook.ui.authentication.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRecipeBehaviour() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.dishList))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.commentSection))
            .perform(scrollTo(), click());
        Thread.sleep(4000);
    }

    @Test
    public void testDrawerBehaviour() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.my_drawer_layout))
                .perform(DrawerActions.open());
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.nav_favourites))
                .perform(click());
        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.my_drawer_layout))
                .perform(DrawerActions.open());
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.nav_home))
                .perform(click());
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.my_drawer_layout))
                .perform(DrawerActions.open());
        Thread.sleep(1000);
        onView(ViewMatchers.withId(R.id.nav_my_recipes))
                .perform(click());
        Thread.sleep(4000);
    }
}
