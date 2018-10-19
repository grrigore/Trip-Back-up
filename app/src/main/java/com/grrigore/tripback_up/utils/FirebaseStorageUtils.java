package com.grrigore.tripback_up.utils;

import android.net.Uri;

import java.util.ArrayList;

public interface FirebaseStorageUtils {

    //Firebase storage
    void addImagesToStorage(ArrayList<Uri> imageURIs, String currentUser);

    void editImagesFromStorage(String tripId, String currentUser);

    void deleteImagesFromStorage(String tripId, String currentUser);

}
