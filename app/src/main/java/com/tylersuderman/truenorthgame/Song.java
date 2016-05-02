package com.tylersuderman.truenorthgame;

/**
 * Created by tylersuderman on 5/2/16.
 */
public class Song {
    private String mSongTitle;
    private String mArtistName;
    private String mAlbumTitle;
    private String mPreviewUrl;
    private String mTrackId;

    public Song(String trackId, String SongTitle, String artistName, String albumTitle,
                String previewUrl) {
        this.mTrackId = trackId;
        this.mSongTitle = SongTitle;
        this.mArtistName = artistName;
        this.mAlbumTitle = albumTitle;
        this.mPreviewUrl = previewUrl;
    }

    public String getTitle() {
        return mSongTitle;
    }

    public String getArtist() {
        return mArtistName;
    }

    public String getAlbum() {
        return mAlbumTitle;
    }

    public String getPreview() {
        return mPreviewUrl;
    }

    public String getId() {
        return mTrackId;
    }
}
