package com.tylersuderman.truenorthgame;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameStartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MAX_WIDTH = 700;
    private static final int MAX_HEIGHT = 700;


    @Bind(R.id.artistImageView) ImageView mArtistImageView;
    @Bind(R.id.artistNameTextView) TextView mArtistNameTextView;
    @Bind(R.id.startGameButton) Button mStartGameButton;

    public static final String TAG = GameStartActivity.class.getSimpleName();
    public ArrayList<Song> songs = new ArrayList<>();
    public boolean callBackDone = false;
    public Artist artist;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        mContext = this;
        ButterKnife.bind(this);

//        TAKE THIS OUT LATER!
        getArtistTracks("the beatles");
        mStartGameButton.setOnClickListener(GameStartActivity.this);

    }

    private void getArtistTracks(String artistName) {
        SpotifyService.findArtist(artistName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                artist = SpotifyService
                    .processArtistResults(response).get(0);
                String artistId = artist.getId();

                GameStartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(mContext)
                                .load(artist.getImage())
                                .resize(MAX_WIDTH, MAX_HEIGHT)
                                .centerCrop()
                                .into(mArtistImageView);
                        mArtistNameTextView.setText(artist.getName());
                    }

                });

                getTrackIds(artistId);

            }
        });
    }

    private void getTrackIds(String artistId) {
        SpotifyService.findSongIds(artistId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                songs = SpotifyService.processSongIds(response);
                callBackDone = true;

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startGameButton:
                Intent intent = new Intent(GameStartActivity.this, GameRoundActivity.class);
                intent.putExtra("songs", Parcels.wrap(songs));
                Log.d(TAG, "THIS IS AN ARRAY LIST OF SONG OBJECTS" + songs);
                //intent.putParcelableArrayListExtra("songs", songs);
                intent.putExtra("artist", Parcels.wrap(artist));
                startActivity(intent);
                break;
        }
    }
}
