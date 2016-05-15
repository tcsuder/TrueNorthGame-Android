package com.tylersuderman.truenorthgame.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;

import java.util.ArrayList;

/**
 * Created by tylersuderman on 5/3/16.
 */
public class MultipleChoiceAdapter  extends RecyclerView.Adapter<ChoiceViewHolder>{
    public static final String TAG = MultipleChoiceAdapter.class.getSimpleName();
    private ArrayList<Song> mRoundSongs = new ArrayList<>();
    private Artist mArtist = new Artist();
    private Context mContext;
    private ArrayList<Song> mAllSongs = new ArrayList<>();

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferenceEditor;
    private String mCurrentPlayerId;
    private Firebase mPlayerRef;
    private Player mCurrentPlayer;
    private int mRoundPoints;
    private android.os.Handler mPointTimerHandler;
    private Runnable mPointTimer;


    public MultipleChoiceAdapter(Context context, ArrayList<Song> songs, ArrayList<Song> allSongs,
                                 Artist artist) {
        mContext = context;
        mRoundSongs = songs;
        mAllSongs = allSongs;
        mArtist = artist;
        mCurrentPlayer = getCurrentPlayer();
        mRoundPoints = 3500;
        recursiveDecreaseRoundPointsTimer();
        mPointTimerHandler = new android.os.Handler();
        mPointTimer = new Runnable() {
            @Override
            public void run() {
                runPointTimer();
            }
        };
        recursiveDecreaseRoundPointsTimer();
    }

    private void runPointTimer() {
        if (mRoundPoints > 115) {
            Log.d(TAG, "POINTS FROM ADAPTER: " + mRoundPoints);
            mRoundPoints -= 115;
            recursiveDecreaseRoundPointsTimer();
        } else {
            mRoundPoints = 50;
        }
    }


//    RECURSIVE METHOD AS INTERVAL TIMER
    private void recursiveDecreaseRoundPointsTimer() {
        new android.os.Handler().postDelayed(mPointTimer, 100);
    }


    private Player getCurrentPlayer() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPreferenceEditor = mSharedPreferences.edit();
        mCurrentPlayerId = mSharedPreferences.getString(Constants
                .PREFERENCES_PLAYER_KEY, null);
//        Log.d(TAG, "CURRENT PLAYER ID: " + mCurrentPlayerId);
        mPlayerRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);

        mPlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentPlayer = dataSnapshot.child(mCurrentPlayerId).getValue(Player
                        .class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return mCurrentPlayer;
    }

    @Override
    public ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_multiple_choice_item, parent, false);
        ChoiceViewHolder viewHolder = new ChoiceViewHolder(view, mRoundSongs, mCurrentPlayer, mRoundPoints);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChoiceViewHolder holder, int position) {
        holder.bindSong(mRoundSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mRoundSongs.size();
    }



}
