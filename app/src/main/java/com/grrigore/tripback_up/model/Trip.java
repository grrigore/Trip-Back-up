package com.grrigore.tripback_up.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private String title = null;
    private String description = null;
    private Date date = null;
    private List<String> photos = new ArrayList<>();
    private List<String> videos = new ArrayList<>();
    private List<Place> places = new ArrayList<>();

    public Trip() {
    }

    public Trip(String title, String description, Date date, List<String> photos, List<String> videos, List<Place> places) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.photos = photos;
        this.videos = videos;
        this.places = places;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
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
}
