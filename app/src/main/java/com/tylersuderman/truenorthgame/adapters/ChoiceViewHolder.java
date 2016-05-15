package com.tylersuderman.truenorthgame.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.ui.GameRoundActivity;
import com.tylersuderman.truenorthgame.util.OnChoiceSelectedListener;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/15/16.
 */
public class ChoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @Bind(R.id.songTitleTextView) TextView mSongTitleTextView;
    private Context mContext;
    private ArrayList<Song> mRoundSongs;
    private Player mCurrentPlayer;
    private Artist mCurrentArtist;
    private ArrayList<Song> mAllSongs;
    private OnChoiceSelectedListener mOnChoiceSelectedListener;

    public ChoiceViewHolder(View itemView, final ArrayList<Song> roundSongs, final ArrayList<Song>
            allSongs, Player currentPlayer, Artist currentArtist, int mRoundPoints,
                            OnChoiceSelectedListener onChoiceSelectedListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mRoundSongs = roundSongs;
        mCurrentArtist = currentArtist;
        mAllSongs = allSongs;
        mCurrentPlayer = currentPlayer;
        mOnChoiceSelectedListener = onChoiceSelectedListener;
        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int itemPosition = getLayoutPosition();
        Song song = mRoundSongs.get(itemPosition);

        if(song.isRightAnswer()){
//            mCurrentPlayer.addToScore(mRoundPoints);
            Toast.makeText(mContext, "YEP!", Toast.LENGTH_SHORT).show();
        } else {
            if (mCurrentPlayer.getScore() > 50) {
//                        mCurrentPlayer.subtractFromScore(mRoundPoints);
            }
            Toast.makeText(mContext, "NOPE!", Toast.LENGTH_SHORT).show();
        }

//                int round = mSharedPreferences.getInt(Constants.PREFERENCES_ROUND_NUMBER_KEY,
//                        mAllSongs.size());
//                mPlayerRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);

//                if (round == 10) {
//
//                    if (mCurrentPlayer.getScore() > mCurrentPlayer.getTopScore()) {
//                        mCurrentPlayer.setTopScore(mCurrentPlayer.getScore());
//                    }
//                    mCurrentPlayer.resetScore();
//
//                }
//
//                mPlayerRef.child(mCurrentPlayerId).setValue(mCurrentPlayer);
//
//
//                song.unsetRightAnswer();
//                mPointTimerHandler.removeCallbacks(mPointTimer);

        final Intent intent = new Intent(mContext, GameRoundActivity.class);
                intent.putExtra("songs", Parcels.wrap(mAllSongs));
                intent.putExtra("artist", Parcels.wrap(mCurrentArtist));

        new android.os.Handler().postDelayed(

                new Runnable() {
                    public void run() {

                        mContext.startActivity(intent);

                    }
                }, 200);
    }


    public void bindSong(Song song) {
        mSongTitleTextView.setText(song.getTitle());
    }
}
