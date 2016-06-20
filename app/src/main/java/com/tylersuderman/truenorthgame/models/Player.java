package com.tylersuderman.truenorthgame.models;


import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by tylersuderman on 4/24/16.
 */
@Parcel
public class Player {
    String name;
    Integer score;
    String pushId;
    Integer topScore;

    public Player() {}

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.topScore = 0;
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
    public void resetScore() { score = 0; }
    public Integer getTopScore() { return topScore; }
    public void setTopScore(int score) { topScore = score; }
    public void setFakeScore() { topScore = (int) Math.floor(Math.random() * 50000); }

    //    FAKE DATA

    public static ArrayList<Player> getPlayers() {
        Player tom = new Player("Tom");
        tom.setFakeScore();
        Player tracy = new Player("Tracy");
        tracy.setFakeScore();
        Player tim = new Player("Tim");
        tim.setFakeScore();
        Player devona = new Player("Devona");
        devona.setFakeScore();
        Player lawdyJean = new Player("Jean");
        lawdyJean.setFakeScore();

        ArrayList<Player> players = new ArrayList<>();
        players.add(tom);
        players.add(tracy);
        players.add(tim);
        players.add(devona);
        players.add(lawdyJean);

        return players;
    }
}
