package com.tylersuderman.truenorthgame;

/**
 * Created by tylersuderman on 5/1/16.
 */
public class Constants {
    public static final String SPOTIFY_CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    public static final String SPOTIFY_CLIENT_SECRET = BuildConfig.SPOTIFY_CLIENT_SECRET;
    public static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1/";


    public static final String FIREBASE_URL = BuildConfig.FIREBASE_ROOT_URL;
    public static final String FIREBASE_PLAYER_ID = "spotifyUserId";
    public static final String FIREBASE_URL_PLAYER_ID = FIREBASE_URL + "/" +
            FIREBASE_PLAYER_ID;

}
