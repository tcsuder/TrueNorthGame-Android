package com.tylersuderman.truenorthgame;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    public static final String TAG = SpotifyService.class.getSimpleName();

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
            Log.d(TAG, "TYPE: " + response.getType());
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
                            final Player authorizedPlayer = SpotifyService.processUserResults
                                    (response).get
                                    (0);

//                            UPDATE CURRENT USER
                            preferencesEditor.putString(Constants.PREFERENCES_PLAYER_KEY, authorizedPlayer
                                    .getPushId()).apply();


//                            UPDATE DATABASE WITH NEW USE IF APPLICABLE
                            final Firebase firebasePlayersRef = new Firebase(Constants
                                    .FIREBASE_URL_PLAYERS);
                            firebasePlayersRef.addListenerForSingleValueEvent(new
                                                                                      ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Boolean playerSaved = snapshot.child(authorizedPlayer
                                            .getPushId()).exists();

                                    firebasePlayersRef.child(authorizedPlayer.getPushId()).setValue
                                            (authorizedPlayer);

                                    if (!playerSaved) {

                                    }

                                    String playerId = sharedPreferences.getString(Constants
                                            .PREFERENCES_PLAYER_KEY, null);
                                    Log.d(TAG, "Current Player: " + playerId);
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
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }


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

    public static ArrayList<Player> processUserResults(Response response) {
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
                        .getJSONObject(1).getString("url");
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


    public static ArrayList<Song> processSongIds(Response response) {
        ArrayList<Song> songs = new ArrayList<>();
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
                    String artist = artistArray.getJSONObject(0).getString("name");
                    String album = song.getJSONObject("album").getString("name");
                    String preview = song.getString("preview_url");

                    Song newSong = new Song(id, title, artist, album, preview);
                    songs.add(newSong);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.shuffle(songs);
        return songs;
    }
}
