package com.tylersuderman.truenorthgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        Spanned html = Html.fromHtml("<a href='https://github.com/tcsuder/TrueNorthGame-Android'>True North Project</a>");
        mRepoLinkButton.setMovementMethod(LinkMovementMethod.getInstance());
        mRepoLinkButton.setText(html);
        Spanned html2 = Html.fromHtml("<a href='https://github.com/tcsuder/'>Other Projects</a>");
        mGitHubLinkButton.setMovementMethod(LinkMovementMethod.getInstance());
        mGitHubLinkButton.setText(html2);
        Spanned html3 = Html.fromHtml("<a href='https://www.linkedin.com/in/tylersuderman'>Tyler Suderman</a>");
        mLinkdInLinkButton.setMovementMethod(LinkMovementMethod.getInstance());
        mLinkdInLinkButton.setText(html3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
