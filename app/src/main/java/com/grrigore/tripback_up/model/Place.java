package com.grrigore.tripback_up.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "place",
        indices = @Index("trip_id"),
        foreignKeys = @ForeignKey(entity = Trip.class,
                parentColumns = "id",
                childColumns = "trip_id"))
public class Place implements Parcelable {

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
    @PrimaryKey
    @NonNull
    private String id;
    private String lat = null;
    private String lng = null;
    @ColumnInfo(name = "trip_id")
    private String tripId;

    @Ignore
    public Place() {
    }

    @Ignore
    public Place(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Place(String id, String lat, String lng, String tripId) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.tripId = tripId;
    }

    protected Place(Parcel in) {
        id = in.readString();
        lat = in.readString();
        lng = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lng);
    }
}