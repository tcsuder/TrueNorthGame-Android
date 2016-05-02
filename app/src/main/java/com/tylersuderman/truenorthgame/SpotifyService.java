package com.tylersuderman.truenorthgame;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
    String SPOTIFY_CLIENT_ID = Constants.SPOTIFY_CLIENT_ID;
    String SPOTIFY_CLIENT_SECRET = Constants.SPOTIFY_CLIENT_SECRET;
    String SPOTIFY_BASE_URL = Constants.SPOTIFY_BASE_URL;
    String SPOTIFY_TRACKS_QUERY_PARAMETER_ = Constants.SPOTIFY_TRACKS_QUERY_PARAMETER;
    Artist artist;


    public static void findArtist(String artistName, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.SPOTIFY_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("search");
        urlBuilder.addQueryParameter("q", "beatles");
        urlBuilder.addQueryParameter("type", "artist");
        urlBuilder.addQueryParameter("limit", "1");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Artist> processArtistResults(Response response) {
        ArrayList<Artist> artist = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject spotifyJSON = new JSONObject(jsonData);
                JSONObject artistDetailsJSON = spotifyJSON.getJSONObject("artists")
                        .getJSONArray("items").getJSONObject(0);

                String name = artistDetailsJSON.getString("name");
                String imageUrl = artistDetailsJSON.getJSONArray("images")
                        .getJSONObject(1).getString("url");
                String id = artistDetailsJSON.getString("id");
                String page = artistDetailsJSON.getJSONObject("external_urls")
                        .getString("spotify");
                Artist instance = new Artist(name, imageUrl, id, page);
                artist.add(instance);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return artist;
    }

    public static void findTrackIds(String id, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.SPOTIFY_BASE_URL).newBuilder();
        urlBuilder.addPathSegment("artists");
        urlBuilder.addPathSegment(id);
        urlBuilder.addPathSegment("top-tracks");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Artist> processIds(Response response) {
        ArrayList<Artist> artist = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject spotifyJSON = new JSONObject(jsonData);
                JSONObject artistDetailsJSON = spotifyJSON.getJSONObject("artists")
                        .getJSONArray("items").getJSONObject(0);

                String name = artistDetailsJSON.getString("name");
                String imageUrl = artistDetailsJSON.getJSONArray("images")
                        .getJSONObject(1).getString("url");
                String id = artistDetailsJSON.getString("id");
                String page = artistDetailsJSON.getJSONObject("external_urls")
                        .getString("spotify");
                Artist instance = new Artist(name, imageUrl, id, page);
                artist.add(instance);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return artist;
    }
}
