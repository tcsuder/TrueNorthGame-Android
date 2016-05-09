package com.tylersuderman.truenorthgame;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;

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
    public String artistId;
    public boolean success;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        mContext = this;
        ButterKnife.bind(this);

        Intent intent = getIntent();
        artist = Parcels.unwrap(intent.getParcelableExtra("artist"));
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
                if (SpotifyService.processArtistResults(response).size() > 0) {
                    success = true;
                    artist = SpotifyService
                            .processArtistResults(response).get(0);
                    artistId = artist.getId();

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
//                    getTrackIds(artist);
                } else {
                    success = false;
                    Log.d(TAG, "that don't work no no no!");

                    GameStartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mArtistNameTextView.setText("NO ARTIST FOUND");
                            mStartGameButton.setText("");
                        }
                    });
                }

            }
        });
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startGameButton:
                Intent intent = new Intent(GameStartActivity.this, GameRoundActivity.class);
                intent.putExtra("songs", Parcels.wrap(songs));
                intent.putExtra("artist", Parcels.wrap(artist));
                startActivity(intent);
                break;
        }
    }
}
