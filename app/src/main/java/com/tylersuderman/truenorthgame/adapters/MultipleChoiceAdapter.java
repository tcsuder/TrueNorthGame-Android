package com.tylersuderman.truenorthgame.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.models.Song;
import com.tylersuderman.truenorthgame.util.OnChoiceSelectedListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tylersuderman on 5/3/16.
 */
public class MultipleChoiceAdapter  extends RecyclerView.Adapter<MultipleChoiceAdapter
.ChoiceViewHolder>{
    public static final String TAG = MultipleChoiceAdapter.class.getSimpleName();
    private OnChoiceSelectedListener mOnChoiceSelectedListener;
    private ArrayList<Song> mRoundSongs = new ArrayList<>();
    private Context mContext;


    public MultipleChoiceAdapter(Context context, ArrayList<Song> songs,
                                 OnChoiceSelectedListener
                                onChoiceSelectedListener) {
        mContext = context;
        mRoundSongs = songs;
        mOnChoiceSelectedListener = onChoiceSelectedListener;
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
