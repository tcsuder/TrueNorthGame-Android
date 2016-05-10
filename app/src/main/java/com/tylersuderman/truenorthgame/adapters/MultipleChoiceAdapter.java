package com.tylersuderman.truenorthgame.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.ui.GameRoundActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/3/16.
 */
public class MultipleChoiceAdapter  extends RecyclerView.Adapter<MultipleChoiceAdapter
        .ChoiceViewHolder>{
    private ArrayList<Song> mSongs = new ArrayList<>();
    private Artist mArtist = new Artist();
    private Context mContext;
    private ArrayList<Song> mAllSongs = new ArrayList<>();

    public MultipleChoiceAdapter(Context context, ArrayList<Song> songs, ArrayList<Song> allSongs,
                                 Artist artist) {
        mContext = context;
        mSongs = songs;
        mAllSongs = allSongs;
        mArtist = artist;
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
                    song.unsetRightAnswer();
                    Intent intent = new Intent(mContext, GameRoundActivity.class);
                    intent.putExtra("songs", Parcels.wrap(mAllSongs));
                    intent.putExtra("artist", Parcels.wrap(mArtist));
                    mContext.startActivity(intent);
                }
            });
        }

        public void bindSong(Song song) {
            mSongTitleTextView.setText(song.getTitle());
        }
    }


}
