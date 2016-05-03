package com.tylersuderman.truenorthgame;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    public Artist artist;
    ArrayList<Song> songs = new ArrayList<>();
    ArrayList<Song> allSongs = new ArrayList<>();
    public MediaPlayer mediaPlayer;
    public String audioPath;
    public CountDownTimer countdownTimer;
    public int playTime = 8000;
    private MultipleChoiceAdapter mAdapter;




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
            Song song = allSongs.get(i);
            if (songs.size() == 4) {
                break;
            } else if (songs.size() > 0) {
                songs.add(song);
            } else {
                if (song.getPlayed() == true ) {
                    Log.i(TAG, "Song skipped");
                } else {
                    song.setToPlayed();
                    song.setRightAnswer();
                    songs.add(song);
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
        Collections.shuffle(songs);




//        SET UP MEDIA PLAYER

        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        DELAY PLAY OF SONG AND SHOWING OF CHOICES
        new android.os.Handler().postDelayed(

        new Runnable() {
            public void run() {

                //        PLAY SONG
                mediaPlayer.start();


                //        SET CHOICES INTO RECYCLERVIEW
                mAdapter = new MultipleChoiceAdapter(getApplicationContext(), songs);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(GameRoundActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        }, 500);

//        END PLAY AFTER PLAYTIME IS UP

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mediaPlayer.pause();
                    }
                },
                playTime);

//        SHOW COUNTDOWN IN VIEW
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
