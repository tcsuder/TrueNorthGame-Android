package com.tylersuderman.truenorthgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameRoundActivity extends AppCompatActivity {
    public static final String TAG = GameRoundActivity.class.getSimpleName();
    public String SPOTIFY_ACCESS_TOKEN;
    public String artistName;
    public ArrayList<Song> songs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_round);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        SPOTIFY_ACCESS_TOKEN = intent.getStringExtra("token");
        artistName = intent.getStringExtra("artistName");
        getArtistTracks("the beatles");
    }

    private void getArtistTracks(String artistName) {
        SpotifyService.findArtist(artistName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Artist artist = SpotifyService
                    .processArtistResults(response).get(0);
                String artistId = artist.getId();

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
            }
        });
    }
}
