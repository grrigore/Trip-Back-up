package com.grrigore.tripback_up.network;

import android.net.Uri;

import java.util.ArrayList;

public interface FirebaseStorageUtils {

    //Firebase storage
    void addImagesToStorage(ArrayList<Uri> imageUri, String currentUser);

    void editImagesFromStorage(String tripId, String currentUser);

    void deleteImagesFromStorage(String tripId, String currentUser);

    void downloadImagesFromStorage(String tripId, String currentUser);

}
