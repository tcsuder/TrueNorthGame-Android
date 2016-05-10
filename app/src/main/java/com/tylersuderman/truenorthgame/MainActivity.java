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
    @Bind(R.id.artistNameEditText) EditText mArtistNameEditText;


    private static final int REQUEST_CODE = Constants.REQUEST_CODE;
    private static final String REDIRECT_URI = Constants.REDIRECT_URI;
    private static final String SPOTIFY_CLIENT_ID = Constants.SPOTIFY_CLIENT_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPlayButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mTopScoreButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);


        SpotifyService.spotifyUserAuth(MainActivity.this, SPOTIFY_CLIENT_ID,
                REDIRECT_URI, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        SpotifyService.saveAuthorizedUser(requestCode, resultCode, intent, MainActivity.this);

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
                final Artist artist = responseArray.get(0);

//                  IF ARTIST SEARCH RETURNS SPOTIFY ARTIST OBJECT PACKAGE SONGS AND GO TO GAME
                if (size > 0) {
                    getTracks(artist);

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


    private void getTracks(final Artist artist) {
        String artistId = artist.getId();
        SpotifyService.findSpotifySongs(artistId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ArrayList<Song> songs = SpotifyService.processSongIds(response);
                Intent intent = new Intent(MainActivity.this, GameRoundActivity.class);
                intent.putExtra("artist", Parcels.wrap(artist));
                intent.putExtra("songs", Parcels.wrap(songs));
                startActivity(intent);
            }
        });
    }


    public void toast(String string) {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.no_artist_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        final TextView toastText = (TextView) layout.findViewById(R.id
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

                break;*/

            case R.id.quickPlayButton:
                final String artistName = mArtistNameEditText.getText().toString();

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