package com.tylersuderman.truenorthgame;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by tylersuderman on 5/8/16.
 */
public class TrueNorthApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
