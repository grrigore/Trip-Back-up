package com.grrigore.tripback_up.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String lat = null;
    private String lng = null;

    public Place() {
    }

    public Place(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    protected Place(Parcel in) {
        lat = in.readString();
        lng = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lat);
        dest.writeString(lng);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}