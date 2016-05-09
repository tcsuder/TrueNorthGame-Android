package com.tylersuderman.truenorthgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class Player {
    String playerName;
    String playerId;
    Integer playerScore;

    public Player(String name, String id) {
        this.playerName = name;
        this.playerId = id;
        this.playerScore = (int) Math.floor(Math.random() * 1001);
    }

    public String getName() {
        return playerName;
    }
    public Integer getScore() {
        return playerScore;
    }
    public String getId() { return playerId; }
    public Integer addToScore(int newScore) {
        this.playerScore += newScore;
        return playerScore;
    }


    //    FAKE DATA

    public static ArrayList<Player> getPlayers() {
        Player tom = new Player("Tom", "1");
        Player tracy = new Player("Tracy", "2");
        Player tim = new Player("Tim", "3");
        Player devona = new Player("Devona", "4");
        Player lawdyJean = new Player("Lawdy-Jean", "5");

        ArrayList<Player> players = new ArrayList<>();
        players.add(tom);
        players.add(tracy);
        players.add(tim);
        players.add(devona);
        players.add(lawdyJean);

        return players;
    }
}
