package com.tylersuderman.truenorthgame.models;
import org.parceler.Parcel;

/**
 * Created by tylersuderman on 5/2/16.
 */
@Parcel
public class Artist {
    private String mArtistName;
    private String mArtistImageUrl;
    private String mArtistId;
    private String mArtistPageUrl;

    public Artist() {}

    public Artist(String name, String imageUrl, String id, String pageUrl) {
        this.mArtistName = name;
        this.mArtistImageUrl = imageUrl;
        this.mArtistId = id;
        this.mArtistPageUrl = pageUrl;
    }

    public String getName() {
        return mArtistName;
    }

    public String getImage() {
        return mArtistImageUrl;
    }

    public String getId() {
        return mArtistId;
    }

    public String getPage() {
        return mArtistPageUrl;
    }
}
