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

import com.squareup.picasso.Picasso;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.adapters.MultipleChoiceAdapter;
import com.tylersuderman.truenorthgame.models.Artist;
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
    private static final int POINTS_PER_ROUND = 3000;
    private static final int MILLIS_PER_ROUND = 6000;

    @Bind(R.id.countdownTextView) TextView mCountdownTextView;
    @Bind(R.id.pointsTextView) TextView mPointsTextView;
    @Bind(R.id.artistImageView) ImageView mArtistImageView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private Artist mArtist;
    private ArrayList<Song> mGuessingRoundSongs = new ArrayList<>();
    private ArrayList<Song> mAllSongs = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private String mAudioPath;
    private int mPlaySongForTime;
    private MultipleChoiceAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferenceEditor;
    private boolean unplayedSongLoaded;
    private int mPointsScorable;
    private android.os.Handler mTimerHandler;
    private Runnable mTimer;

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
        mPlaySongForTime = MILLIS_PER_ROUND;
        mPointsScorable = POINTS_PER_ROUND;
        mGuessingRoundSongs = createSongArray(mAllSongs);
        mPointsTextView.setText("points: "+ mPointsScorable);
        mCountdownTextView.setText("time: " + (mPlaySongForTime/1000));
        mTimerHandler = new android.os.Handler();
        mTimer = new Runnable() {
            @Override
            public void run() {
                runTimer();
            }
        };
        syncRoundTimerWithSongAndPoints(mGuessingRoundSongs);

//        ALL GAME AND SCORING LOGIC LIVES WITH THE CLICK FUNCTION IN MULTIPLE CHOICE ADAPTER


    }

    private void runTimer() {
        if (mPlaySongForTime > 500) {
            mPlaySongForTime -= Math.floor(MILLIS_PER_ROUND/60);
            if ((mPlaySongForTime % 1000) == 0 && mPlaySongForTime < (MILLIS_PER_ROUND - 50)) {
                mCountdownTextView.setText("time: "+ mPlaySongForTime/1000);
            }
            if ((mPlaySongForTime % 300) == 0) {
                if (mPointsScorable > Math.floor(POINTS_PER_ROUND /15)) {
                    Log.d(TAG, "POINTS FROM ACTIVITY: " + mPointsScorable);
                    mPointsScorable -= ((POINTS_PER_ROUND /15) + 5);
                    mPointsTextView.setText("points: "+ mPointsScorable);
                } else {
                    mPointsScorable = POINTS_PER_ROUND /60;
                    mPointsTextView.setText("points: "+ mPointsScorable);
                }
            }
            recursiveDisplayRoundPointsTimer();
        } else {
            Intent intent = new Intent(GameRoundActivity.this, GameRoundActivity.class);
            intent.putExtra("songs", Parcels.wrap(mAllSongs));
            intent.putExtra("artist", Parcels.wrap(mArtist));
            startActivity(intent);
        }
    }

    private void recursiveDisplayRoundPointsTimer() {
        mTimerHandler.postDelayed(mTimer, 100);
    }


    private void checkRound() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(GameRoundActivity.this);
        mPreferenceEditor = mSharedPreferences.edit();
        final int previousRound = mSharedPreferences.getInt(Constants.PREFERENCES_ROUND_NUMBER_KEY,
                0);
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

    private void syncRoundTimerWithSongAndPoints(ArrayList<Song> roundSongs) {
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
                        recursiveDisplayRoundPointsTimer();

                    }
                }, 100);

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
        mTimerHandler.removeCallbacks(mTimer);
        super.onPause();
        mMediaPlayer.stop();

    }
}

