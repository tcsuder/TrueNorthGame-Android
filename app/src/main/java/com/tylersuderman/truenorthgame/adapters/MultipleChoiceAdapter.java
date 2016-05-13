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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/3/16.
 */
public class MultipleChoiceAdapter  extends RecyclerView.Adapter<MultipleChoiceAdapter
        .ChoiceViewHolder>{
    public static final String TAG = MultipleChoiceAdapter.class.getSimpleName();
    private ArrayList<Song> mSongs = new ArrayList<>();
    private Artist mArtist = new Artist();
    private Context mContext;
    private ArrayList<Song> mAllSongs = new ArrayList<>();

    private SharedPreferences mSharedPreferences;
    private String mCurrentPlayerId;
    private Firebase mPlayerRef;
    private Player mCurrentPlayer;
    private int mRoundPoints;


    public MultipleChoiceAdapter(Context context, ArrayList<Song> songs, ArrayList<Song> allSongs,
                                 Artist artist) {
        mContext = context;
        mSongs = songs;
        mAllSongs = allSongs;
        mArtist = artist;
        mCurrentPlayer = getCurrentPlayer();
        mRoundPoints = 3000;
        decreaseRoundPointsTimer();
    }


    private int decreaseRoundPointsTimer() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (mRoundPoints > 215) {
                            mRoundPoints -= 215;
                            decreaseRoundPointsTimer();
                        }
                    }
                }, 500);
        Log.d(TAG, "ROUND POINTS DECREASING: " + mRoundPoints);
        return mRoundPoints;
    }


    private Player getCurrentPlayer() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCurrentPlayerId = mSharedPreferences.getString(Constants
                .PREFERENCES_PLAYER_KEY, null);
        Log.d(TAG, "CURRENT PLAYER ID: " + mCurrentPlayerId);
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
    public MultipleChoiceAdapter.ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_multiple_choice_item, parent, false);
        ChoiceViewHolder viewHolder = new ChoiceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MultipleChoiceAdapter.ChoiceViewHolder holder, int position) {
        holder.bindSong(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
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
                    Song song = mSongs.get(itemPosition);

                    if(song.isRightAnswer()){
                        mCurrentPlayer.addToScore(mRoundPoints);
                        Toast.makeText(mContext, "YEP!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mCurrentPlayer.getScore() > 50) {
                            mCurrentPlayer.subtractFromScore(mRoundPoints);
                        }
                        Toast.makeText(mContext, "NOPE!", Toast.LENGTH_SHORT).show();
                    }

                    mPlayerRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);
                    mPlayerRef.child(mCurrentPlayerId).setValue(mCurrentPlayer);
                    song.unsetRightAnswer();
                    final Intent intent = new Intent(mContext, GameRoundActivity.class);
                    intent.putExtra("songs", Parcels.wrap(mAllSongs));
                    intent.putExtra("artist", Parcels.wrap(mArtist));

                    new android.os.Handler().postDelayed(

                        new Runnable() {
                            public void run() {

                                mContext.startActivity(intent);

                            }
                        }, 350);
                }
            });
        }

        public void bindSong(Song song) {
            mSongTitleTextView.setText(song.getTitle());
        }
    }


}
