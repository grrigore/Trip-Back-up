package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

@Database(entities = {Trip.class, Place.class}, version = 1)
public abstract class TripsDatabase extends RoomDatabase {

    private static TripsDatabase INSTANCE;

    public abstract TripDao tripDao();

    public static TripsDatabase getTripDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TripsDatabase.class, "trip-database")
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
