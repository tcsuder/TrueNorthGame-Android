package com.tylersuderman.truenorthgame;

import com.tylersuderman.truenorthgame.models.Player;

import java.util.Comparator;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class PlayerScoreComparator implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2){
        return player2.getScore() - player1.getScore();
    }
}
