package com.tylersuderman.truenorthgame.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tylersuderman.truenorthgame.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.repoLinkButton) Button mRepoLinkButton;
    @Bind(R.id.gitHubLinkButton) Button mGitHubLinkButton;
    @Bind(R.id.linkdInLinkButton) Button mLinkdInLinkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mRepoLinkButton.setOnClickListener(this);
        mGitHubLinkButton.setOnClickListener(this);
        mLinkdInLinkButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.repoLinkButton:
                Uri gameRepo = Uri.parse("https://github.com/tcsuder/TrueNorthGame-Android");
                Intent repoIntent = new Intent(Intent.ACTION_VIEW, gameRepo);
                startActivity(repoIntent);
                break;
            case R.id.gitHubLinkButton:
                Uri github = Uri.parse("https://github.com/tcsuder/");
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, github);
                startActivity(gitIntent);
                break;
            case R.id.linkdInLinkButton:
                Uri linkedIn = Uri.parse("https://www.linkedin.com/in/tylersuderman");
                Intent linkedIntent = new Intent(Intent.ACTION_VIEW, linkedIn);
                startActivity(linkedIntent);
                break;
        }
    }

}
