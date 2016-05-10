package com.tylersuderman.truenorthgame;

import android.util.Log;

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
public class SpotifyService {
    public static final String TAG = SpotifyService.class.getSimpleName();

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
