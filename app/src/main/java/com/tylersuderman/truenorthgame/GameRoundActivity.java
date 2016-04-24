package com.tylersuderman.truenorthgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameRoundActivity extends AppCompatActivity {
    public static final String TAG = GameRoundActivity.class.getSimpleName();
    @Bind(R.id.welcomeToGameTextView) TextView mWelcomeToGameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);

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
