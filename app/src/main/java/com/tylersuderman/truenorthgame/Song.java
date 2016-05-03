package com.tylersuderman.truenorthgame;

import org.parceler.Parcel;

/**
 * Created by tylersuderman on 5/2/16.
 */

@Parcel
public class Song {
    private String mSongTitle;
    private String mArtistName;
    private String mAlbumTitle;
    private String mPreviewUrl;
    private String mTrackId;
    private boolean mPlayed;
    private boolean mRightAnswerSong;

    public Song() {}

    public Song(String trackId, String SongTitle, String artistName, String albumTitle,
                String previewUrl) {
        this.mTrackId = trackId;
        this.mSongTitle = SongTitle;
        this.mArtistName = artistName;
        this.mAlbumTitle = albumTitle;
        this.mPreviewUrl = previewUrl;
        this.mPlayed = false;
        this.mRightAnswerSong = false;
    }

    public String getTitle() {
        return mSongTitle;
    }

    public String getArtist() {
        return mArtistName;
    }

    public String getAlbum() { return mAlbumTitle; }

    public String getPreview() {
        return mPreviewUrl;
    }

    public String getId() {
        return mTrackId;
    }

    public boolean getPlayed() {
        return mPlayed;
    }

    public void setToPlayed() { mPlayed = true; }

    public void setRightAnswer() { mRightAnswerSong = true; }

    public void unsetRightAnswer() { mRightAnswerSong = false; }

}
