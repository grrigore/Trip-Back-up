package com.grrigore.tripback_up.model;

import java.util.List;

public class Trip {
    private String title;
    private String description;
    private List<String> photos;
    private List<String> videos;
    private List<Place> places;

    public Trip() {
    }

    public Trip(String title, String description, List<String> photos, List<String> videos, List<Place> places) {
        this.title = title;
        this.description = description;
        this.photos = photos;
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
