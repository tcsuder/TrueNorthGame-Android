package com.tylersuderman.truenorthgame;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by tylersuderman on 4/24/16.
 */
public class Player {
    String playerName;
    Integer playerScore;
    private String pushId;
    private boolean playerCheck;
    private Firebase mFirebasePlayersRef;

    public Player(String name) {
        this.playerName = name;
        this.playerScore = (int) Math.floor(Math.random() * 1001);
    }

    public String getName() {
        return playerName;
    }
    public Integer getScore() {
        return playerScore;
    }
    public String getPushId() { return pushId; }
    public Integer addToScore(int newScore) {
        this.playerScore += newScore;
        return playerScore;
    }
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public boolean alreadyExists() {
        playerCheck = false;
        mFirebasePlayersRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);
        mFirebasePlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(pushId).exists()) {
                    playerCheck = true;
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return playerCheck;
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
