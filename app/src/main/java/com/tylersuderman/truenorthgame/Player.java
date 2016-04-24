package com.tylersuderman.truenorthgame;

import java.util.ArrayList;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class Player {
    private String mPlayerName;
    private Integer mPlayerScore;

//    FAKE DATA

    public static ArrayList<Player> getPlayers() {
        Player tom = new Player("Tom");
        Player tracy = new Player("Tracy");
        Player tim = new Player("Tim");
        Player devona = new Player("Devona");
        Player lawdyJean = new Player("Lawdy-Jean");

        ArrayList<Player> players = new ArrayList<>();
        players.add(tom);
        players.add(tracy);
        players.add(tim);
        players.add(devona);
        players.add(lawdyJean);

        return players;
    }




    public Player(String name) {
        this.mPlayerName = name;
        this.mPlayerScore = (int) Math.floor(Math.random() * 101);
    }

    public String getName() {
        return mPlayerName;
    }
    public Integer getScore() {
        return mPlayerScore;
    }
    public Integer addScore(int newScore) {
        this.mPlayerScore += newScore;
        return mPlayerScore;
    }
}
