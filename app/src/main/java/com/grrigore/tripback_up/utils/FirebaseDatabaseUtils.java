package com.grrigore.tripback_up.utils;

import com.grrigore.tripback_up.model.Trip;

public interface FirebaseDatabaseUtils {

    //Firebase database
    void addTripToDatabase(Trip trip);

    void editTripFromDatabase(String tripId);

    void deleteTripFromDatabase(String tripId);

}
