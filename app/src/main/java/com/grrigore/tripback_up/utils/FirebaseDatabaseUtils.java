package com.grrigore.tripback_up.utils;

import com.grrigore.tripback_up.model.Trip;

public interface FirebaseDatabaseUtils {

    //Firebase database
    void addTripToDatabase(Trip trip, String currentUser);

    void editTripFromDatabase(String tripId, String currentUser);

    void deleteTripFromDatabase(String tripId, String currentUser);

}
