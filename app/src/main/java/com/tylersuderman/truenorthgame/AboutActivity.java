package com.tylersuderman.truenorthgame;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    @Bind(R.id.repoLinkTextView) TextView mRepoLinkTextView;
    @Bind(R.id.gitHubLinkTextView) TextView mGitHubLinkTextView;
    @Bind(R.id.linkdInTextView) TextView mLinkdInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mRepoLinkTextView.setOnClickListener(this);
        mGitHubLinkTextView.setOnClickListener(this);
        mLinkdInTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repoLinkTextView:
                break;
            case R.id.gitHubLinkTextView:
                break;
            case
        }
    }
}
