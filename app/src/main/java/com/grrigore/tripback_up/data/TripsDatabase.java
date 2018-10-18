package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

@Database(entities = {Trip.class, Place.class}, version = 1)
public abstract class TripsDatabase extends RoomDatabase {

    public static final String DB_NAME = "tripDatabase.db";
    private static TripsDatabase INSTANCE;

    public abstract TripDao tripDao();

    public static TripsDatabase getTripDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TripsDatabase.class, DB_NAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract TripDao getTripDao();

    public abstract PlaceDao getPlaceDao();
}
