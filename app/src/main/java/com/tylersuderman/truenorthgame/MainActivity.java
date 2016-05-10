package com.tylersuderman.truenorthgame;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;
        import com.spotify.sdk.android.authentication.AuthenticationClient;
        import com.spotify.sdk.android.authentication.AuthenticationRequest;
        import com.spotify.sdk.android.authentication.AuthenticationResponse;

        import org.parceler.Parcels;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Map;

        import butterknife.Bind;
        import butterknife.ButterKnife;
        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.aboutButton) Button mAboutButton;
    @Bind(R.id.topScoresButton) Button mTopScoreButton;
    @Bind(R.id.quickPlayButton) Button mPlayButton;
    @Bind(R.id.artistNameEditText) EditText mArtistName;
    private Firebase mPlayerId;
    private ValueEventListener mSpotifyPlayerIdEventListener;
    private Player mPlayer;
    private Artist mArtist;
    private String mCurrentPlayerId;
    private boolean playerSaved;
    private Firebase mFirebasePlayersRef;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "truenorthgame.mainactivity://callback";
    String SPOTIFY_CLIENT_ID = Constants.SPOTIFY_CLIENT_ID;
    String SPOTIFY_ACCESS_TOKEN;


    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Artist> artistPackage = new ArrayList<>();
    private Artist artist;
    private String artistName;
    private TextView toastText;
    private View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPlayerId = new Firebase(Constants.FIREBASE_URL_PLAYER_ID);
        mPlayButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mTopScoreButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();




//        SPOTIFY AUTHENTICATION BUILDER
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

//        SET CURRENT PLAYER ID AFTER SPOTIFY AUTH PROCESS
//        mSpotifyPlayerIdEventListener = mPlayerId.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String playerId = dataSnapshot.getValue().toString();
//                Log.d("Player id updated", playerId);
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

    }


    private void addToSharedPreferences(String uid) {
        mEditor.putString(Constants.PREFERENCES_PLAYER_KEY, uid).apply();
    }


//    SPOTIFY AUTH TOKEN RETRIEVER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "I'm here");
        Log.d(TAG, ""+requestCode);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.d(TAG, "TYPE: " + response.getType());
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    SPOTIFY_ACCESS_TOKEN = response.getAccessToken();
                    SpotifyService.findUserId(SPOTIFY_ACCESS_TOKEN, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) { e.printStackTrace(); }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            mPlayer = SpotifyService.processUserResults(response).get(0);
                            Log.d(TAG, "THIS IS THE CURRENT PLAYER: " + mPlayer);
                            addToSharedPreferences(mPlayer.getPushId());

                            mFirebasePlayersRef = new Firebase(Constants.FIREBASE_URL_PLAYERS);
                            mFirebasePlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    playerSaved = snapshot.child(mPlayer.getPushId()).exists();

                                    if (!playerSaved) {
                                        mFirebasePlayersRef.child(mPlayer.getPushId()).setValue(mPlayer);
                                    }

                                    mCurrentPlayerId = mSharedPreferences.getString(Constants.PREFERENCES_PLAYER_KEY, null);
                                    Log.d(TAG, "Current Player: " + mCurrentPlayerId);
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

    public void saveIdToFirebase(String playerId) {
        Firebase playerIdRef = new Firebase(Constants.FIREBASE_URL_PLAYER_ID);
        playerIdRef.setValue(playerId);
    }

    private void searchArtist(final String userSearch) {
        SpotifyService.findArtist(userSearch, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                ArrayList<Artist> responseArray = SpotifyService.processArtistResults(response);
                int size = responseArray.size();
                mArtist = responseArray.get(0);

//                  IF ARTIST SEARCH RETURNS SPOTIFY ARTIST OBJECT PACKAGE SONGS AND GO TO GAME
                if (size > 0) {
                    getTracks(mArtist);

//                            IF ARTIST SEARCH IS UNSUCCESSFUL
                } else {

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("NO ARTIST FOUND");
                        }

                    });
                }

            }
        });
    }


    private void getTracks(Artist returnedArtist) {
        String artistId = returnedArtist.getId();
        SpotifyService.findSpotifySongs(artistId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                songs = SpotifyService.processSongIds(response);
                Intent intent = new Intent(MainActivity.this, GameRoundActivity.class);
                intent.putExtra("artist", Parcels.wrap(artist));
                intent.putExtra("songs", Parcels.wrap(songs));
                startActivity(intent);
            }
        });
    }


    public void toast(String string) {
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.no_artist_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        toastText = (TextView) layout.findViewById(R.id
                .toastText);
        toastText.setText(string);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 275);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
/*            case R.id.loginButton:
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);


//                String username = mUsernameEditText.getText().toString();
//                String password = mPasswordEditText.getText().toString();
//                if (username.equals("") || password.equals("")) {
//                    Toast.makeText(MainActivity.this, "No Username/Password", Toast.LENGTH_SHORT).show();
//                } else if (username.equals("tyler") && password.equals("password")) {
//                    Intent loginIntent = new Intent(MainActivity.this, GameRoundActivity.class);
//                    loginIntent.putExtra("username", username);
//                    startActivity(loginIntent);
//                } else {
//                    Toast.makeText(MainActivity.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
//                }

                break;*/

            case R.id.quickPlayButton:
                artistName = mArtistName.getText().toString();

                if (artistName.equals("")) {

                    toast("NO ARTIST GIVEN");

                } else {

//                    SEARCH CONTAINS SONGS RETRIEVAL AND NEW INTEN UPON SUCCESS
                    searchArtist(artistName);
                }
                break;
            case R.id.aboutButton:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.topScoresButton:
                Intent topScoresIntent = new Intent(MainActivity.this, TopScoresActivity.class);
                startActivity(topScoresIntent);
            default:
                break;

        }
    }

}