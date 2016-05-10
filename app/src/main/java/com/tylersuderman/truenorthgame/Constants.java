package com.tylersuderman.truenorthgame;

/**
 * Created by tylersuderman on 5/1/16.
 */
public class Constants {
    public static final String SPOTIFY_CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    public static final int REQUEST_CODE = 1337;
    public static final String REDIRECT_URI = "truenorthgame.mainactivity://callback";
    public static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1/";


    public static final String FIREBASE_URL = BuildConfig.FIREBASE_ROOT_URL;
    public static final String FIREBASE_LOCATION_PLAYER_ID = "currentPlayerId";
    public static final String FIREBASE_URL_PLAYER_ID = FIREBASE_URL + "/" +
            FIREBASE_LOCATION_PLAYER_ID;
    public static final String KEY_UID = "UID";
    public static final String FIREBASE_LOCATION_PLAYERS = "players";
    public static final String FIREBASE_URL_PLAYERS = FIREBASE_URL + "/" +
            FIREBASE_LOCATION_PLAYERS;

    public static final String PREFERENCES_PLAYER_KEY = "currentPlayer";


}
