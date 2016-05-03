package com.tylersuderman.truenorthgame;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.spotify.sdk.android.authentication.AuthenticationClient;
        import com.spotify.sdk.android.authentication.AuthenticationRequest;
        import com.spotify.sdk.android.authentication.AuthenticationResponse;

        import butterknife.Bind;
        import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.quickPlayButton) Button mPlayButton;
    @Bind(R.id.aboutButton) Button mAboutButton;
    @Bind(R.id.topScoresButton) Button mTopScoreButton;
    @Bind(R.id.artistNameEditText) EditText mArtistName;

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "truenorthgame.mainactivity://callback";
    String SPOTIFY_CLIENT_ID = Constants.SPOTIFY_CLIENT_ID;
    String SPOTIFY_ACCESS_TOKEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        Log.d(TAG, SPOTIFY_CLIENT_ID);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();


        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        mPlayButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mTopScoreButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    SPOTIFY_ACCESS_TOKEN = response.getAccessToken();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quickPlayButton:
                String notUsername = " ";
                Intent intent = new Intent(MainActivity.this, GameStartActivity.class);
                String artistName = mArtistName.getText().toString();
                intent.putExtra("artistName", artistName);
                intent.putExtra("token", SPOTIFY_ACCESS_TOKEN);
                startActivity(intent);
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