package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.grrigore.tripback_up.model.Trip;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM Trip")
    List<Trip> getTrips();

    @Query("SELECT * FROM Trip WHERE id LIKE :id")
    Trip findById(String id);

    @Insert
    void insertTrip(Trip trip);

    @Delete
    void deleteTrip(Trip trip);
}
