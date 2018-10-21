package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.grrigore.tripback_up.model.Trip;

import java.util.List;

//toask nu stiu cum sa abordez toata treaba asta :(
@Dao
public interface TripDao {
    @Query("SELECT * FROM trip")
    List<Trip> getAllTrips();

    @Query("SELECT * FROM trip WHERE id LIKE :id")
    Trip getTripById(String id);

    @Insert
    void insert(Trip trip);

    @Delete
    void delete(Trip trip);
}
