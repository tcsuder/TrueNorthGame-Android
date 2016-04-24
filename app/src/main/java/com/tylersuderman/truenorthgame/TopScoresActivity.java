package com.tylersuderman.truenorthgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TopScoresActivity extends AppCompatActivity extends  {
    public static final String TAG = TopScoresActivity.class.getSimpleName();
    private ListView mListView;
    private ArrayList<Player> players = Player.getPlayers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        for ( Player player : players) {
            Log.i(TAG, player.getName());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scores);

        mListView = (ListView) findViewById(R.id.topScoresListView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, players);
        mListView.setAdapter(adapter);
    }
}
