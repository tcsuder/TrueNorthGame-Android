package com.tylersuderman.truenorthgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class TopScoresActivity extends AppCompatActivity {
    public static final String TAG = TopScoresActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scores);
        ArrayList<Player> players = Player.getPlayers();
        populatePlayersList();
    }

    private void populatePlayersList() {
        // Construct the data source
        ArrayList<Player> arrayOfPlayers = Player.getPlayers();
        // Sort players by high to low score using custom comparator
        Collections.sort(arrayOfPlayers, new PlayerScoreComparator());
        // Create the adapter to convert the array to views
        PlayerAdapter adapter = new PlayerAdapter(this, arrayOfPlayers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.topScoresListView);
        listView.setAdapter(adapter);
    }
}
