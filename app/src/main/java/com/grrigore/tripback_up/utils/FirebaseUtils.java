package com.grrigore.tripback_up.utils;

import com.grrigore.tripback_up.model.Trip;

public interface FirebaseUtils {

    //Firebase database
    void addTripToDatabase(Trip trip);
    void editTripFromDatabase(String tripId);
    void deleteTripFromDatabase(String tripId);

    //Firebase storage
    void addImagesToStorage();
    void editImagesFromStorage(String tripId);
    void deleteImagesFromStorage(String tripId);

}
