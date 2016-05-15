package com.tylersuderman.truenorthgame.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.tylersuderman.truenorthgame.Constants;
import com.tylersuderman.truenorthgame.models.Artist;
import com.tylersuderman.truenorthgame.models.Player;
import com.tylersuderman.truenorthgame.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tylersuderman on 5/2/16.
 */
public class SpotifyService extends AppCompatActivity {
    private static final int NUMBER_OF_ROUNDS = Constants.NUMBER_OF_ROUNDS;
    private static final String SPOTIFY_CLIENT_SECRET = Constants.SPOTIFY_CLIENT_SECRET;
    public static final String TAG = SpotifyService.class.getSimpleName();
    private String finalTitle;
    private ArrayList<String> characterArray;

    public static void unauthorizeUser(Context context) {
        AuthenticationClient.clearCookies(context);
        Log.d(TAG, "LOGOUT");
    }

    public static void spotifyUserAuth(Activity activity, String clientId, String redirectUri,
                                       int requestCode) {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(clientId, AuthenticationResponse.Type.TOKEN,
                        redirectUri);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(activity, requestCode, request);

    }

    public static void saveAuthorizedUser(int requestCode, int resultCode, Intent intent, final
    Context context) {


        final String accessToken;
        // Check if result comes from the correct activity
        if (requestCode == Constants.REQUEST_CODE) {

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.d(TAG, "RESPONSE TOKEN TYPE: " + response.getType());
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    accessToken = response.getAccessToken();
                    SpotifyService.findUserId(accessToken, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) { e.printStackTrace(); }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(context);
                            final SharedPreferences.Editor preferencesEditor = sharedPreferences
                                    .edit();
                            final Player authorizedPlayer = SpotifyService.processPlayerResults
                                    (response).get
                                    (0);

//                            UPDATE CURRENT USER
                            preferencesEditor.putString(Constants.PREFERENCES_PLAYER_KEY, authorizedPlayer
                                    .getPushId()).apply();


//                            UPDATE DATABASE WITH NEW USE IF APPLICABLE
                            final Firebase firebasePlayersRef = new Firebase(Constants
                                    .FIREBASE_URL_PLAYERS);
                            firebasePlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Boolean playerSaved = snapshot.child(authorizedPlayer
                                            .getPushId()).exists();


                                    if (!playerSaved) {

                                        firebasePlayersRef.child(authorizedPlayer.getPushId()).setValue
                                                (authorizedPlayer);

                                    }

                                    String playerId = sharedPreferences.getString(Constants
                                            .PREFERENCES_PLAYER_KEY, null);
                                    Log.d(TAG, "Current Player ID: " + playerId);
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });

                        }
                    });
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG, "Something went wrong while trying to log in: " + response.getError
                            ());
                    break;
            }
        }
    }


//    API REQUESTS


    public static void findArtist(String artistName, Callback callback) {
        Log.d(TAG, "I'm here");

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.SPOTIFY_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("search");
        urlBuilder.addQueryParameter("q", artistName);
        urlBuilder.addQueryParameter("type", "artist");
        urlBuilder.addQueryParameter("limit", "1");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "FIND ARTIST URL " + url);

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Artist> processArtistResults(Response response) {
//        OBJECT MUST BE SENT IN ARRAY... WHY?
        ArrayList<Artist> artistArray = new ArrayList<>();
        Artist instance;
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {

//                CREATE ARTIST OBJECT FROM SUCCESSFUL CALL
                JSONObject spotifyJSON = new JSONObject(jsonData);
                JSONObject artistDetailsJSON = spotifyJSON.getJSONObject("artists")
                        .getJSONArray("items").getJSONObject(0);

                String name = artistDetailsJSON.getString("name");
                String imageUrl = artistDetailsJSON.getJSONArray("images")
                        .getJSONObject(0).getString("url");
                String id = artistDetailsJSON.getString("id");
                String page = artistDetailsJSON.getJSONObject("external_urls")
                        .getString("spotify");
                instance = new Artist(name, imageUrl, id, page);
                artistArray.add(instance);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return artistArray;
    }


    public static void findUserId(String token, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.SPOTIFY_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("me");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        Log.d(TAG, "FIND USER REQUEST" + request);

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Player> processPlayerResults(Response response) {
//        OBJECT MUST BE SENT IN ARRAY... WHY?
        ArrayList<Player> playerArray = new ArrayList<>();
        String playerId;
        String playerName;
        Player instance;
        try {
            String jsonData = response.body().string();
            if(response.isSuccessful()) {
                JSONObject artistJSON = new JSONObject(jsonData);
                playerId = artistJSON.getString("id");
                playerName = artistJSON.getString("display_name");
                instance = new Player(playerName);
                instance.setPushId(playerId);
                playerArray.add(instance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playerArray;
    }


    public static void findSpotifySongs(String id, Callback callback) {

        OkHttpClient spotify = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.SPOTIFY_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("artists");
        urlBuilder.addPathSegment(id);
        urlBuilder.addPathSegment("top-tracks");
        urlBuilder.addQueryParameter("country", "US");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = spotify.newCall(request);
        call.enqueue(callback);
    }

//    RECURSIVE METHOD CHECKING FOR PRINS

    private static String recursiveRemovePrins(String title) {
        title = title.replaceAll("[\\[]", "(");
        title = title.replaceAll("[\\]]", ")");

        int firstPrin = title.indexOf("(");
        int secondPrin = title.indexOf(")");

        if (firstPrin >= 0 && secondPrin > 0) {
            ArrayList<String> characterArray = new ArrayList<>
                    (Arrays.asList(title.split("")));
            final StringBuilder resultWord = new StringBuilder(characterArray.size());

            for (int i=secondPrin+1; i>=firstPrin; i--) {
                characterArray.remove(i);
                Log.d(TAG, "CHARACTER ARRAY BEING SHORTENED: " + characterArray);
            }
            for (String s : characterArray) {
                resultWord.append(s);
            }
            final String newTitle = resultWord.toString();
            return recursiveRemovePrins(newTitle);
        } else {
            return title;
        }
    }

    private static String removeUnwantedSubstrings(String title) {
        ArrayList<String> chopItUp;
        String[] takeOutStrings = {" - Single", " - Pt", "Part 1", "part 1", " - From", " - 20",
                " - Live", " - Feat", " - feat", ";", "/L", " - Remastered"};

        for (int i=0; i<takeOutStrings.length; i++) {
            chopItUp = new ArrayList<>(Arrays.asList(title.split
                    (takeOutStrings[i])));
            title = chopItUp.get(0);
            Log.d(TAG, "CHOP CHOP: " + chopItUp);
        }

        return title;
    }

    private static String shortenTitle(String title) {
        Log.d(TAG, "OLD TITLE: " + title);
        String noUnwantedSubstringstitle = removeUnwantedSubstrings(title);
        String noPrinsTitle = recursiveRemovePrins(noUnwantedSubstringstitle);
        Log.d(TAG, "NEW TITLE: " + noPrinsTitle);
        return noPrinsTitle;
    }


    public static ArrayList<Song> processSongResults(Response response) {
        ArrayList<Song> songs = new ArrayList<>();
        Boolean songAlreadyAdded = false;
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject spotifyJSON = new JSONObject(jsonData);
                JSONArray tracksJSON = spotifyJSON.getJSONArray("tracks");

                for (int i = 0; i < tracksJSON.length(); i++) {
                    JSONObject song = tracksJSON.getJSONObject(i);
                    JSONArray artistArray = song.getJSONArray("artists");

                    String id = song.getString("id");
                    String title = song.getString("name");
                    String shortTitle = shortenTitle(title);

                    String artist = artistArray.getJSONObject(0).getString("name");
                    String album = song.getJSONObject("album").getString("name");
                    String preview = song.getString("preview_url");

                    Song newSong = new Song(id, shortTitle, artist, album, preview);

                    if (songs.size() == 0) {
                        songs.add(newSong);
                    } else {
                        if (songs.size() < NUMBER_OF_ROUNDS) {
                            for (int j=0; j<songs.size(); j++) {
                                String newSongTitle = newSong.getTitle();
                                Song songInList = songs.get(j);
                                String addedSongTitle = songInList.getTitle();
                                if (newSongTitle.equalsIgnoreCase(addedSongTitle)) {
                                    songAlreadyAdded = true;
                                }
                            }
                            if (!songAlreadyAdded) {
                                songs.add(newSong);
                            }
                        }

                    }
                    songAlreadyAdded = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.shuffle(songs);
        Log.d(TAG, "SONG LIST LENGTH FROM BUILDER: " + songs.size());
        return songs;
    }
}
