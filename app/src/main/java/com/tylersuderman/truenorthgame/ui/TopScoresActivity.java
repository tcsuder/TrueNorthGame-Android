package com.tylersuderman.truenorthgame.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.PlayerScoreComparator;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.adapters.PlayerAdapter;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.services.SpotifyService;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TopScoresActivity extends AppCompatActivity {
    public static final String TAG = TopScoresActivity.class.getSimpleName();
    private static final int MAX_WIDTH = 700;
    private static final int MAX_HEIGHT = 700;
    private Firebase mFirebasePlayersRef;
    private Handler mHandler;
    private boolean mGameOver;
    private int mCalls;
    private ArrayList<Player> mfirebasePlayers = new ArrayList<>();

    @Bind(R.id.artistImageView) ImageView mArtistImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mGameOver = intent.getBooleanExtra("gameOver", false);
        setContentView(R.layout.activity_top_scores);
        ButterKnife.bind(this);
        getTopScores();
        mHandler = new Handler();
        scrollArtistPics();
    }

    private void getTopScores() {
        mFirebasePlayersRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);
        mFirebasePlayersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot player : dataSnapshot.getChildren()) {
                    String name = player.child("name").getValue().toString();
                    Integer topScore = Integer.parseInt(player.child("topScore").getValue().toString());
                    Player savedPlayer = new Player(name);
                    savedPlayer.setTopScore(topScore);
                    Log.d(TAG, "PLAYER: " + name);
                    mfirebasePlayers.add(savedPlayer);
                }
                populateFakePlayersList(mfirebasePlayers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void populateFakePlayersList(ArrayList<Player> playersFromFirebase) {
        // Construct the data source
        ArrayList<Player> arrayOfPlayers = Player.getPlayers();
        Log.d(TAG, "PLAYERS: " + playersFromFirebase.size());

        for (Player player : playersFromFirebase) {
            arrayOfPlayers.add(player);
        }
        // Sort players by high to low score using custom comparator
        Collections.sort(arrayOfPlayers, new PlayerScoreComparator());
        // Create the adapter to convert the array to views
        PlayerAdapter adapter = new PlayerAdapter(this, arrayOfPlayers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.topScoresListView);
        listView.setAdapter(adapter);
    }

    private void scrollArtistPics() {
        final String[] artists = {"elvis", "bob dylan", "kanye", "beatles", "white denim",
                "britney spears", "slipknot", "gaither vocal", "steve martin", "leonard cohen",
                "wutang", "shakira", "frank", "amos", "miley", "hozier", "whitney", "michael", "willie",
                "merle", "ray charles", "salt", "destiny's", "missy"};
        final int index = (int) Math.floor(Math.random() * artists.length);
        SpotifyService.findArtist(artists[index], new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                ArrayList<Artist> responseArray = SpotifyService.processArtistResults(response);
                Log.d(TAG, "response: " + responseArray.get(0).getName());
                int size = responseArray.size();

                if (mCalls == 3 && mGameOver) {
                    Intent intent = new Intent(TopScoresActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (size > 0) {
                        final Artist artist = responseArray.get(0);
                        setImage(TopScoresActivity.this, artist);
                        mCalls ++;
                    }
                    if (mCalls < 15) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollArtistPics();
                            }
                        }, 3000);
                    }
                }
            }
        });
    }

    private void setImage(Context context, Artist artist) {
        final Context imageContext = context;
        final Artist imageArtist = artist;
        final String image = imageArtist.getImage();
        TopScoresActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.with(imageContext)
                        .load(image)
                        .resize(MAX_WIDTH, MAX_HEIGHT)
                        .centerCrop()
                        .into(mArtistImageView);
            }

        });

    }
}
