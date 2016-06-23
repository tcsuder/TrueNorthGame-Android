package com.tylersuderman.truenorthgame;

import android.os.Handler;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tylersuderman.truenorthgame.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by tylersuderman on 4/23/16.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);


//    @Test
//    public void loginButtonToGameRoundActivityFuncationality() {
//        onView(withId(R.id.usernameEditText)).perform(typeText("tyler"));
//        onView(withId(R.id.passwordEditText)).perform(typeText("password"));
//        onView(withId(R.id.loginButton)).perform(click());
//        onView(withId(R.id.welcomeToGameTextView))
//                .check(matches(withText("HELLO TYLER!")));
//
//    }
}