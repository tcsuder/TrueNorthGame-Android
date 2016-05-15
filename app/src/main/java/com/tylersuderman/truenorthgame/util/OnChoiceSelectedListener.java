package com.tylersuderman.truenorthgame.util;

import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;

import java.util.ArrayList;

/**
 * Created by tylersuderman on 5/15/16.
 */
public interface OnChoiceSelectedListener {
    public void onChoiceSelected(Integer position, ArrayList<Song> roundSongs, Integer
            roundPoints, Player currentPlayer);
}
