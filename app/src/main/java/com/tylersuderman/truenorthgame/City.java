package com.tylersuderman.truenorthgame;

import java.math.BigDecimal;

/**
 * Created by tylersuderman on 4/23/16.
 */
public class City {
    private String mCityName;
    private String mLongitude;
    private String mLatitude;

    public City(String name, String lng, String lat) {
        this.mCityName = name;
        this.mLongitude = lng;
        this.mLatitude = lat;
    }

    public String getLat() {
        return mLatitude;
    }

    public String getLng() {
        return mLongitude;
    }

    public String getName() {
        return mCityName;
    }
}
