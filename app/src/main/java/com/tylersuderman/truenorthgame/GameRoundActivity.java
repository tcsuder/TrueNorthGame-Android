package com.tylersuderman.truenorthgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameRoundActivity extends AppCompatActivity {
    public static final String TAG = GameRoundActivity.class.getSimpleName();
    public City newYorkCity = new City("New York City", "40.7128° N", "74.0059° W");
    public City newOrleans = new City("New Orleans", "29.9511° N", "90.0715° W");
    public ArrayList<City> cityArray = new ArrayList<City>();
    @Bind(R.id.welcomeToGameTextView) TextView mWelcomeToGameTextView;
    @Bind(R.id.cityOneTextView) TextView mCityOneTextView;
    @Bind(R.id.cityTwoTextView) TextView mCityTwoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);
        cityArray.add(newOrleans);
        cityArray.add(newYorkCity);

        mCityOneTextView.setText(cityArray.get(0).getName());
        mCityTwoTextView.setText(cityArray.get(1).getName());

        Log.i(TAG, "NO PLACE LIKE " + newYorkCity.getName());
        Log.i(TAG, "I PREFER " + newOrleans.getName());
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.i(TAG, "YOUR USERNAME IS:" + username + "!");
        if (username.equals(" ")) {
            mWelcomeToGameTextView.setText("WELCOME GUEST!");
        } else {
            mWelcomeToGameTextView.setText("HELLO " + username.toUpperCase() + "!");
        }

    }
}
