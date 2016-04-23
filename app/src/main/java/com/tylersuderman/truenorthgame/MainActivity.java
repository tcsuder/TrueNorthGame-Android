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

        import butterknife.Bind;
        import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.playButton) Button mPlayButton;
    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.aboutButton) Button mAboutButton;
    @Bind(R.id.usernameEditText) EditText mUsernameEditText;
    @Bind(R.id.passwordEditText) EditText mPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPlayButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                Intent intent = new Intent(MainActivity.this, GameRoundActivity.class);
                startActivity(intent);
                break;
            case R.id.loginButton:
//                CLOSE KEYBOARD ON CLICK
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);


                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "No Username/Password", Toast.LENGTH_SHORT).show();
                } else if (username.equals("tyler") && password.equals("password")) {
                    Intent loginIntent = new Intent(MainActivity.this, GameRoundActivity.class);
                    loginIntent.putExtra("username", username);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.aboutButton:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            default:
                break;

        }
    }

}