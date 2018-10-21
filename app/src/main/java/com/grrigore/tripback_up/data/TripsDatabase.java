package com.grrigore.tripback_up.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

@Database(entities = {Trip.class, Place.class}, version = 1, exportSchema = false)
public abstract class TripsDatabase extends RoomDatabase {
    private static final String DB_NAME = "tripDatabase.db";
    private static volatile TripsDatabase instance;

    public static synchronized TripsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static TripsDatabase create(final Context context) {
        //todo not on the main thread
        return Room.databaseBuilder(
                context,
                TripsDatabase.class,
                DB_NAME).allowMainThreadQueries().build();
    }

    public abstract TripDao getTripDao();

    public abstract PlaceDao getPlaceDao();
}
