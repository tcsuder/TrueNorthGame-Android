package com.tylersuderman.truenorthgame;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameRoundActivity extends AppCompatActivity {
    public static final String TAG = GameRoundActivity.class.getSimpleName();

    @Bind(R.id.countdownTextView) TextView mCountdownTextView;

    public Artist artist;
    ArrayList<Song> songs = new ArrayList<>();
    ArrayList<Song> allSongs = new ArrayList<>();
    public MediaPlayer mediaPlayer;
    public String audioPath;
    public CountDownTimer countdownTimer;
    public int playTime = 8000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        artist = Parcels.unwrap(intent.getParcelableExtra("artist"));

//        RETRIEVE SHUFFLE AND CHOOSE 4 RANDOM SONGS AND REMOVE CURRENT QUIZ SONG
        allSongs = Parcels.unwrap(intent.getParcelableExtra("songs"));
        Collections.shuffle(allSongs);
        for (int i=0; i<allSongs.size(); i++) {
            if (songs.size() == 4) {
                break;
            } else if (songs.size() > 0) {
                songs.add(allSongs.get(i));
            } else {
                if (allSongs.get(i).getPlayed() == true ) {
                        Log.i(TAG, "Song skipped");
                    } else {
                        songs.add(allSongs.get(i));
                        allSongs.get(i).setToPlayed();
                    }
            }
        }


//        ERROR HANDLING FOR SONG RETREIVAL
        if (songs.size() > 0) {
            audioPath = songs.get(0).getPreview();
        } else {
            Toast.makeText(GameRoundActivity.this, "Async error: please choose artist again.",
                    Toast.LENGTH_SHORT).show();
        }




        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)

        } catch (Exception e) {
            e.printStackTrace();
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mediaPlayer.start();
                    }
                },
                500);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mediaPlayer.pause();
                    }
                },
                playTime);

        countdownTimer = new CountDownTimer(playTime, 1000) {

            public void onTick(long millisUntilFinished) {
                mCountdownTextView.setText("time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mCountdownTextView.setText("OVER!");
            }
        }.start();


    }
}
