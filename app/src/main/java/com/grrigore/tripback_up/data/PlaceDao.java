package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.grrigore.tripback_up.model.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM place")
    List<Place> getAllPlaces();

    @Query("SELECT * FROM place WHERE id = :id")
    Place getPlaceById(int id);

    @Query("SELECT * FROM place WHERE trip_id LIKE :tripId")
    List<Place> getPlacesByTripId(String tripId);

    @Insert
    void insert(Place place);

}
