package com.tylersuderman.truenorthgame.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.PlayerScoreComparator;
import com.tylersuderman.truenorthgame.R;
import com.tylersuderman.truenorthgame.adapters.PlayerAdapter;
import com.tylersuderman.truenorthgame.models.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopScoresActivity extends AppCompatActivity {
    public static final String TAG = TopScoresActivity.class.getSimpleName();
    private ArrayList<Player> players = new ArrayList<>();
    private Firebase mFirebasePlayersRef;
    private ArrayList<Player> mfirebasePlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scores);
        getTopScores();
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
}
