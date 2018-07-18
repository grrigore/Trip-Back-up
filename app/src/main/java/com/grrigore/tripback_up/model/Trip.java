package com.grrigore.tripback_up.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private String title = null;
    private String description = null;
    private Date date = null;
    private List<String> images = new ArrayList<>();
    private List<String> videos = new ArrayList<>();
    private List<Place> places = new ArrayList<>();

    public Trip() {
    }

    public Trip(String title, String description, Date date, List<String> images, List<String> videos, List<Place> places) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.images = images;
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
                ", images=" + images +
                ", videos=" + videos +
                ", places=" + places +
                '}';
    }
}
