package com.tylersuderman.truenorthgame.models;


import java.util.ArrayList;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class Player {
    String name;
    Integer score;
    String pushId;

    public Player() {}

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }
    public Integer getScore() {
        return score;
    }
    public String getPushId() { return pushId; }
    public Integer addToScore(int roundPoints) {
        this.score += roundPoints;
        return score;
    }

    public void subtractFromScore(int roundPoints) {
        final int score = (int) Math.floor(roundPoints/5);
        this.score -= score;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }


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
}
