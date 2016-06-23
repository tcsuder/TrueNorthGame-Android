package com.tylersuderman.truenorthgame.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.adapters.MultipleChoiceAdapter;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.util.OnChoiceSelectedListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameRoundActivity extends AppCompatActivity implements OnChoiceSelectedListener{
    public static final String TAG = GameRoundActivity.class.getSimpleName();
    private static final int MAX_WIDTH = 700;
    private static final int MAX_HEIGHT = 700;
    private static final int POINTS_PER_ROUND = Constants.POINTS_PER_ROUND;
    private static final int MILLIS_PER_ROUND = Constants.MILLIS_PER_ROUND;
    private static int ROUNDS_IN_CURRENT_GAME;

    @Bind(R.id.countdownTextView) TextView mCountdownTextView;
    @Bind(R.id.pointsTextView) TextView mPointsTextView;
    @Bind(R.id.artistImageView) ImageView mArtistImageView;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private Artist mArtist;
    private ArrayList<Song> mRoundSongs = new ArrayList<>();
    private ArrayList<Song> mAllSongs = new ArrayList<>();
    private Song mRightAnswerSong;
    private Player mCurrentPlayer;
    private Firebase mFirebasePlayerRef;
    private MediaPlayer mMediaPlayer;
    private Boolean mOrientationChange = false;
    private int mCurrentRound;
    private String mAudioPath;
    private int mCountdownTime;
    private int mPointsScorable;
    private android.os.Handler mTimerHandler;
    private Runnable mTimer;
    private OnChoiceSelectedListener mOnChoiceSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);

        if(savedInstanceState !=null) {
            mOrientationChange = true;
            mArtist = Parcels.unwrap(savedInstanceState.getParcelable("artist"));
            mAllSongs = Parcels.unwrap(savedInstanceState.getParcelable("allSongs"));
            mRoundSongs = Parcels.unwrap(savedInstanceState.getParcelable("roundSongs"));
            mRightAnswerSong = Parcels.unwrap(savedInstanceState.getParcelable("rightAnswerSong"));
            mCurrentPlayer = Parcels.unwrap(savedInstanceState.getParcelable("currentPlayer"));
            mCurrentRound = savedInstanceState.getInt("currentRound");
            mCountdownTime = savedInstanceState.getInt("countdownTime");
            mPointsScorable = savedInstanceState.getInt("pointsScorable");
            mFirebasePlayerRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);

        } else {
            Intent intent = getIntent();
            mArtist = Parcels.unwrap(intent.getParcelableExtra("artist"));
            mAllSongs = Parcels.unwrap(intent.getParcelableExtra("songs"));
            ROUNDS_IN_CURRENT_GAME = mAllSongs.size();
            mCurrentPlayer = Parcels.unwrap(intent.getParcelableExtra("player"));
            mCurrentRound = checkRound();
            mRoundSongs = createSongArray(mAllSongs);
            mCountdownTime = MILLIS_PER_ROUND;
            mPointsScorable = POINTS_PER_ROUND;
        }


        setImage(this);
        mPointsTextView.setText("points: "+ mPointsScorable);
        mCountdownTextView.setText("time: " + (mCountdownTime/1000));
        mTimerHandler = new android.os.Handler();
        mTimer = new Runnable() {
            @Override
            public void run() {
                runTimer();
            }
        };
        syncRoundTimerWithSongAndPoints(mRoundSongs);
        try {
            mOnChoiceSelectedListener = (OnChoiceSelectedListener) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString() + e.getMessage());
        }
    }

    private int checkRound() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (GameRoundActivity
                .this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
        int previousRound = sharedPreferences.getInt(Constants.PREFERENCES_ROUND_NUMBER_KEY, 0);
        mCurrentRound = previousRound + 1;
        preferenceEditor.putInt(Constants.PREFERENCES_ROUND_NUMBER_KEY, mCurrentRound).apply();
        mFirebasePlayerRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);

        if (mCurrentRound >= ROUNDS_IN_CURRENT_GAME) {
            mCurrentPlayer.resetScore();
            mFirebasePlayerRef.child(mCurrentPlayer.getPushId()).setValue(mCurrentPlayer);
            Intent intent = new Intent(GameRoundActivity.this, TopScoresActivity.class);
            intent.putExtra("gameOver", true);
            startActivity(intent);
        }
        return mCurrentRound;
    }


    private ArrayList<Song> createSongArray(ArrayList<Song> allSongs) {
        boolean unplayedSongLoaded = false;
        Collections.shuffle(allSongs);
        ArrayList<Song> roundSongs = new ArrayList<>();
        for (int i=0; i<allSongs.size(); i++) {
            Song song = allSongs.get(i);
            String title = song.getTitle();
            boolean played = song.hasBeenPlayed();
            if (roundSongs.size() == 4) {
                break;
            } else if (roundSongs.size() < 3) {
                roundSongs.add(song);
                song.setToAdded();
                if(!song.hasBeenPlayed() && !unplayedSongLoaded) {
                    song.setToPlayed();
                    song.setRightAnswer();
                    mRightAnswerSong = song;
                    unplayedSongLoaded = true;
                }
            } else {
                if (unplayedSongLoaded) {
                    roundSongs.add(song);
                } else if (!song.hasBeenPlayed()) {
                    song.setToPlayed();
                    song.setRightAnswer();
                    mRightAnswerSong = song;
                    roundSongs.add(song);
                } else {
                    Log.d(TAG, "skipped song");
                }
            }

        }

        for (int i = 0; i<roundSongs.size(); i++) {
            Song song = roundSongs.get(i);
            song.unsetAdded();
        }

        return roundSongs;
    }


    @Override
    public void onChoiceSelected(Song selection) {
        Song selectedSong = selection;
        if(selectedSong.isRightAnswer()){
            mCurrentPlayer.addToScore(mPointsScorable);
            final String[] strings = {"YEP!", "GREAT JOB!", "WAY TO GO!", "SO SMART!", "NOT BAD!"};
            final int index = (int) Math.floor(Math.random() * strings.length);
            final String string = strings[index];
            customToast(string, "long");
        } else {
            if (mCurrentPlayer.getScore() > 50) {
                mCurrentPlayer.subtractFromScore(mPointsScorable);
            }
            final String[] strings = {"NOT QUITE!", "NOPE!", "NOT RIGHT!"};
            final int index = (int) Math.floor(Math.random() * strings.length);
            final String string = strings[index];
            customToast(string, "long");
        }

        if (mCurrentPlayer.getScore() > mCurrentPlayer.getTopScore()) {
            mCurrentPlayer.setTopScore(mCurrentPlayer.getScore());
        }

        mFirebasePlayerRef.child(mCurrentPlayer.getPushId()).setValue(mCurrentPlayer);

        mRightAnswerSong.unsetRightAnswer();

        if (mCurrentRound != ROUNDS_IN_CURRENT_GAME) {
            final Intent intent = getIntent();
            intent.putExtra("songs", Parcels.wrap(mAllSongs));
            intent.putExtra("artist", Parcels.wrap(mArtist));
            intent.putExtra("player", Parcels.wrap(mCurrentPlayer));

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {

                            startActivity(intent);

                        }
                    }, 200);
        }

    }

    private void syncRoundTimerWithSongAndPoints(ArrayList<Song> roundSongs) {

        //        ERROR HANDLING FOR SONG RETREIVAL
        if (roundSongs.size() > 0) {
            mAudioPath = mRightAnswerSong.getPreview();
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

        // GAME END TIED TO ALLSONGS DIRECTLY
        if (mCurrentRound != mAllSongs.size()) {
            //        DELAY PLAY OF SONG AND SHOWING OF CHOICES
            new android.os.Handler().postDelayed(

                    new Runnable() {
                        public void run() {

                            //        PLAY SONG
                            if (mOrientationChange) {
                                final int lastPause = MILLIS_PER_ROUND - mCountdownTime;
                                mMediaPlayer.seekTo(lastPause);
                            }
                            mMediaPlayer.start();
                            showGuessRoundSongs();
                            recursiveDisplayRoundPointsTimer();

                        }
                    }, 100);
        }

    }

    private void showGuessRoundSongs() {
        MultipleChoiceAdapter adapter = new MultipleChoiceAdapter(getApplicationContext(),
                mRoundSongs,
                mOnChoiceSelectedListener);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(GameRoundActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void runTimer() {
        if (mCountdownTime > 500) {
            mCountdownTime -= Math.floor(MILLIS_PER_ROUND/60);
            if ((mCountdownTime % 1000) == 0 && mCountdownTime < (MILLIS_PER_ROUND - 50)) {
                mCountdownTextView.setText("time: "+ mCountdownTime/1000);
            }
            if ((mCountdownTime % 300) == 0) {
                if (mPointsScorable > Math.floor(POINTS_PER_ROUND /15)) {
                    mPointsScorable -= ((POINTS_PER_ROUND /15) + 5);
                    mPointsTextView.setText("points: "+ mPointsScorable);
                } else {
                    mPointsScorable = POINTS_PER_ROUND /60;
                    mPointsTextView.setText("points: "+ mPointsScorable);
                }
            }
            recursiveDisplayRoundPointsTimer();
        } else {

            for (int i=0; i<mRoundSongs.size(); i++) {
                Song song = mRoundSongs.get(i);
                if (song.isRightAnswer()) {
                    song.unsetRightAnswer();
                }
            }

            Intent intent = new Intent(GameRoundActivity.this, GameRoundActivity.class);
            intent.putExtra("songs", Parcels.wrap(mAllSongs));
            intent.putExtra("artist", Parcels.wrap(mArtist));
            intent.putExtra("player", Parcels.wrap(mCurrentPlayer));
            startActivity(intent);
        }
    }

    private void recursiveDisplayRoundPointsTimer() {
        // mTimer set to a funciton runTimer()
        mTimerHandler.postDelayed(mTimer, 100);
    }

    private void setImage(Context context) {
        Picasso.with(context)
                .load(mArtist.getImage())
                .resize(MAX_WIDTH, MAX_HEIGHT)
                .centerCrop()
                .into(mArtistImageView);
    }

    public void customToast(String string, String length) {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_custom_game_round,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        final TextView toastText = (TextView) layout.findViewById(R.id
                .toastText);
        toastText.setText(string);
        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 275);
        if(length.equalsIgnoreCase("short")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 1500);
        }
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimerHandler.removeCallbacks(mTimer);
        mMediaPlayer.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("artist", Parcels.wrap(mArtist));
        outState.putParcelable("roundSongs", Parcels.wrap(mRoundSongs));
        outState.putParcelable("allSongs", Parcels.wrap(mAllSongs));
        outState.putParcelable("rightAnswerSong", Parcels.wrap(mRightAnswerSong));
        outState.putParcelable("currentPlayer", Parcels.wrap(mCurrentPlayer));
        outState.putInt("currentRound", mCurrentRound);
        outState.putInt("countdownTime", mCountdownTime);
        outState.putInt("pointsScorable", mPointsScorable);
    }

    @Override
    public void onBackPressed() {
        customToast("no cheating...", "short");
    }
}

