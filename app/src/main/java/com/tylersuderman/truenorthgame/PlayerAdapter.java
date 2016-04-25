package com.tylersuderman.truenorthgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class PlayerAdapter extends ArrayAdapter<Player> {
    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Player player = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_score, parent, false);
        }
        // Lookup view for data population
        TextView playerName = (TextView) convertView.findViewById(R.id.playerName);
        TextView playerScore = (TextView) convertView.findViewById(R.id.playerScore);
        // Populate the data into the template view using the data object
        playerName.setText(player.getName());
        playerScore.setText(player.getScore().toString());
        // Return the completed view to render on screen
        return convertView;
    }
}
