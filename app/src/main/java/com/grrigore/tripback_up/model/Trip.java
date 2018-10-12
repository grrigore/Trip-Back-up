package com.grrigore.tripback_up.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Trip implements Parcelable {
    @ColumnInfo(name = "title")
    private String title = null;
    @ColumnInfo(name = "description")
    private String description = null;
    //todo change Date to long - just time
    @ColumnInfo(name = "time")
    private Date date = null;
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id = null;
    @Ignore
    private List<String> images = new ArrayList<>();
    @Ignore
    private List<String> videos = new ArrayList<>();
    @Ignore
    private List<Place> places = new ArrayList<>();

    public Trip() {
    }

    public Trip(String title, String description, Date date, String id, List<String> images, List<String> videos, List<Place> places) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.id = id;
        this.images = images;
        this.videos = videos;
        this.places = places;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", id='" + id + '\'' +
                ", images=" + images +
                ", videos=" + videos +
                ", places=" + places +
                '}';
    }

    protected Trip(Parcel in) {
        title = in.readString();
        description = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        id = in.readString();
        if (in.readByte() == 0x01) {
            images = new ArrayList<String>();
            in.readList(images, String.class.getClassLoader());
        } else {
            images = null;
        }
        if (in.readByte() == 0x01) {
            videos = new ArrayList<String>();
            in.readList(videos, String.class.getClassLoader());
        } else {
            videos = null;
        }
        if (in.readByte() == 0x01) {
            places = new ArrayList<Place>();
            in.readList(places, Place.class.getClassLoader());
        } else {
            places = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(id);
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }
        if (videos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(videos);
        }
        if (places == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(places);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}