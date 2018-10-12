package com.grrigore.tripback_up.utils;

public interface FirebaseStorageUtils {

    //Firebase storage
    void addImagesToStorage();

    void editImagesFromStorage(String tripId);

    void deleteImagesFromStorage(String tripId);

}
