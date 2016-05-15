package com.tylersuderman.truenorthgame.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.ui.GameRoundActivity;
import com.tylersuderman.truenorthgame.util.OnChoiceSelectedListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/3/16.
 */
public class MultipleChoiceAdapter  extends RecyclerView.Adapter<MultipleChoiceAdapter
.ChoiceViewHolder>{
    public static final String TAG = MultipleChoiceAdapter.class.getSimpleName();
    public ChoiceViewHolder viewHolder;
    private OnChoiceSelectedListener mOnChoiceSelectedListener;
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


    public MultipleChoiceAdapter(Context context, Player currentPlayer, ArrayList<Song> songs,
                                ArrayList<Song> allSongs, Artist artist, OnChoiceSelectedListener
                                onChoiceSelectedListener) {
        mContext = context;
        mRoundSongs = songs;
        mCurrentPlayer = currentPlayer;
        mAllSongs = allSongs;
        mArtist = artist;
        mOnChoiceSelectedListener = onChoiceSelectedListener;
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

    public int getRoundPoints() { return mRoundPoints; }


//    RECURSIVE METHOD AS INTERVAL TIMER
    private void recursiveDecreaseRoundPointsTimer() {
        new android.os.Handler().postDelayed(mPointTimer, 100);
    }

    
    @Override
    public MultipleChoiceAdapter.ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_multiple_choice_item, parent, false);
        ChoiceViewHolder viewHolder = new ChoiceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MultipleChoiceAdapter.ChoiceViewHolder holder, int position) {
        holder.bindSong(mRoundSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mRoundSongs.size();
    }

    public class ChoiceViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.songTitleTextView) TextView mSongTitleTextView;
        private Context mContext;

        public ChoiceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getLayoutPosition();
                    Song song = mRoundSongs.get(itemPosition);
                    mOnChoiceSelectedListener.onChoiceSelected(song);
                }
            });
        }

        public void bindSong(Song song) {
            mSongTitleTextView.setText(song.getTitle());
        }
    }


}
