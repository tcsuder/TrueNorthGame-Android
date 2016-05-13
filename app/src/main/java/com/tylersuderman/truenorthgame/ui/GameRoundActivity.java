package com.tylersuderman.truenorthgame.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.adapters.MultipleChoiceAdapter;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameRoundActivity extends AppCompatActivity {
    public static final String TAG = GameRoundActivity.class.getSimpleName();
    private static final int MAX_WIDTH = 700;
    private static final int MAX_HEIGHT = 700;

    @Bind(R.id.countdownTextView) TextView mCountdownTextView;
    @Bind(R.id.artistImageView) ImageView mArtistImageView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private Artist mArtist;
    private ArrayList<Song> mGuessingRoundSongs = new ArrayList<>();
    private ArrayList<Song> mAllSongs = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private String mAudioPath;
    private int playSongForTime = 10000;
    private MultipleChoiceAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferenceEditor;
    private boolean unplayedSongLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        mArtist = Parcels.unwrap(intent.getParcelableExtra("artist"));
        setImage(this);
        mAllSongs = Parcels.unwrap(intent.getParcelableExtra("songs"));
//        CHECK ROUND USES ALL SONGS LENGTH TO END GAME SO IT MUST BE UNDER ALLSONGS INSTANTIATION
        checkRound();
        mGuessingRoundSongs = createSongArray(mAllSongs);
        playRightAnswerSong(mGuessingRoundSongs);

//        ALL GAME AND SCORING LOGIC LIVES WITH THE CLICK FUNCTION IN MULTIPLE CHOICE ADAPTER

    }


    private void checkRound() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(GameRoundActivity.this);
        mPreferenceEditor = mSharedPreferences.edit();
        final int previousRound = mSharedPreferences.getInt(Constants.PREFERENCES_ROUND_NUMBER_KEY,
                0);
        Log.d(TAG, "Previous round: " + previousRound);
        Log.d(TAG, "SONGS ARRAY SIZE: " + mAllSongs.size());
        if (previousRound == mAllSongs.size()) {
            Intent intent = new Intent(GameRoundActivity.this, TopScoresActivity.class);
            startActivity(intent);
        } else {
            int currentRound = previousRound + 1;
            mPreferenceEditor.putInt(Constants.PREFERENCES_ROUND_NUMBER_KEY, currentRound).apply();
        }
    }

    private ArrayList<Song> createSongArray(ArrayList<Song> allSongs) {
        unplayedSongLoaded = false;
        Collections.shuffle(allSongs);
        ArrayList<Song> roundSongs = new ArrayList<>();
        for (int i=0; i<allSongs.size(); i++) {
            Song song = allSongs.get(i);
            if (roundSongs.size() == 4) {
                break;
            } else if (roundSongs.size() < 3) {
                roundSongs.add(song);
                song.setToAdded();
                if(song.hasBeenPlayed() == false && !song.isAdded()) {
                    song.setToPlayed();
                    song.setRightAnswer();
                    roundSongs.add(song);
                    unplayedSongLoaded = true;
                }
            } else {
                if (song.hasBeenPlayed() == true && unplayedSongLoaded == false) {
                } else {
                    song.setToPlayed();
                    song.setRightAnswer();
                    roundSongs.add(song);
                }
            }

        }

        for (int i = 0; i<roundSongs.size(); i++) {
            Song song = roundSongs.get(i);
            song.unsetAdded();
        }

        return roundSongs;
    }

    private void playRightAnswerSong(ArrayList<Song> roundSongs) {
        final CountDownTimer countdownTimer;

        //        ERROR HANDLING FOR SONG RETREIVAL

        if (roundSongs.size() > 0) {
            for (int i=0; i<roundSongs.size(); i++) {
                Song song = roundSongs.get(i);
                if (song.isRightAnswer()) {
                    mAudioPath = song.getPreview();
                }
            }
        } else {
            Toast.makeText(GameRoundActivity.this, "Async error: please choose artist again.",
                    Toast.LENGTH_SHORT).show();
        }
        Collections.shuffle(roundSongs);

        //        SET UP MEDIA PLAYER
        try {

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mAudioPath);
            mMediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //        DELAY PLAY OF SONG AND SHOWING OF CHOICES
        new android.os.Handler().postDelayed(

                new Runnable() {
                    public void run() {

                        //        PLAY SONG
                        mMediaPlayer.start();

                        showGuessRoundSongs();

                    }
                }, 100);

//        END PLAY AFTER PLAYTIME IS UP

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mMediaPlayer.stop();
                    }
                },
                playSongForTime);

//        SHOW COUNTDOWN IN VIEW
        countdownTimer = new CountDownTimer(playSongForTime, 1000) {

            public void onTick(long millisUntilFinished) {
                mCountdownTextView.setText("time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mCountdownTextView.setText("OVER!");
            }
        }.start();
    }

    private void showGuessRoundSongs() {
        mAdapter = new MultipleChoiceAdapter(getApplicationContext(), mGuessingRoundSongs, mAllSongs,
                mArtist);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(GameRoundActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void setImage(Context context) {
        Picasso.with(context)
                .load(mArtist.getImage())
                .resize(MAX_WIDTH, MAX_HEIGHT)
                .centerCrop()
                .into(mArtistImageView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();

    }
}

