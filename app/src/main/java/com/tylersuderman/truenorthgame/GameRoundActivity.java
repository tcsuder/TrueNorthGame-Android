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

    private Artist mArtist;
    private ArrayList<Song> guessingRoundSongs = new ArrayList<>();
    private ArrayList<Song> allSongs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private String audioPath;
    private CountDownTimer countdownTimer;
    private int playTime = 8000;
    private MultipleChoiceAdapter mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mArtist = Parcels.unwrap(intent.getParcelableExtra("artist"));

        allSongs = Parcels.unwrap(intent.getParcelableExtra("songs"));
        guessingRoundSongs = createSongArray(allSongs);
        playRightAnswerSong(guessingRoundSongs);


    }

    private ArrayList<Song> createSongArray(ArrayList<Song> allSongs) {
        Collections.shuffle(allSongs);
        ArrayList<Song> roundSongs = new ArrayList<>();
        for (int i=0; i<allSongs.size(); i++) {
            Log.d(TAG, "SONGS: "+ roundSongs);

            Song song = allSongs.get(i);
            if (roundSongs.size() == 4) {
                break;
            } else if (roundSongs.size() < 3) {
                roundSongs.add(song);
            } else {
                if (song.getPlayed() == true ) {
                    Log.i(TAG, "Song skipped");
                } else {
                    song.setToPlayed();
                    song.setRightAnswer();
                    roundSongs.add(song);
                }
            }

        }

        return roundSongs;
    }

    private void playRightAnswerSong(ArrayList<Song> roundSongs) {

        //        ERROR HANDLING FOR SONG RETREIVAL
        if (roundSongs.size() > 0) {
            audioPath = roundSongs.get(3).getPreview();
        } else {
            Toast.makeText(GameRoundActivity.this, "Async error: please choose artist again.",
                    Toast.LENGTH_SHORT).show();
        }
        Collections.shuffle(roundSongs);

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

                        showGuessRoundSongs();

                    }
                }, 500);

//        END PLAY AFTER PLAYTIME IS UP

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mediaPlayer.stop();
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

    private void showGuessRoundSongs() {
        mAdapter = new MultipleChoiceAdapter(getApplicationContext(), guessingRoundSongs, allSongs,
                mArtist);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(GameRoundActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();

    }
}

