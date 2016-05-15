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
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.ui.GameRoundActivity;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/15/16.
 */
public class ChoiceViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.songTitleTextView) TextView mSongTitleTextView;
    private Context mContext;
    private ArrayList<Song> mRoundSongs;
    private Player mCurrentPlayer;

    public ChoiceViewHolder(View itemView, final ArrayList<Song> roundSongs, Player
            currentPlayer, int mRoundPoints) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mRoundSongs = roundSongs;
        mCurrentPlayer = currentPlayer;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = getLayoutPosition();
                Song song = mRoundSongs.get(itemPosition);

                if(song.isRightAnswer()){
//                  CANT DO THIS YET FROM HERE  mCurrentPlayer.addToScore(mRoundPoints);
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
//                intent.putExtra("songs", Parcels.wrap(mAllSongs));
//                intent.putExtra("artist", Parcels.wrap(mArtist));

                new android.os.Handler().postDelayed(

                        new Runnable() {
                            public void run() {

                                mContext.startActivity(intent);

                            }
                        }, 200);
            }
        });
    }

    public void bindSong(Song song) {
        mSongTitleTextView.setText(song.getTitle());
    }
}
