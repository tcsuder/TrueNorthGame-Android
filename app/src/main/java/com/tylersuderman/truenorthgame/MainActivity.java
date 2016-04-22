package com.tylersuderman.truenorthgame;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import butterknife.Bind;
        import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.playButton) Button mPlayButton;
    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.aboutButton) Button mAboutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPlayButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                Intent intent = new Intent(MainActivity.this, GameRoundActivity.class);
                startActivity(intent);
                break;
            case R.id.loginButton:
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                Intent fakeIntent = new Intent(MainActivity.this, GameRoundActivity.class);
                startActivity(fakeIntent);
                break;

        }
    }

}