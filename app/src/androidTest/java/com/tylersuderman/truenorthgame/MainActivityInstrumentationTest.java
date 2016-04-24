package com.tylersuderman.truenorthgame;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by tylersuderman on 4/23/16.
 */
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void validateEditText() {
        onView(withId(R.id.usernameEditText)).perform(typeText("tyler")).check(matches(withText("tyler")));

    }

    @Test
    public void loginButtonToGameRoundActivityFuncationality() {
        onView(withId(R.id.usernameEditText)).perform(typeText("tyler"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password"));
        onView(withId(R.id.loginButton)).perform(click());

    }
}