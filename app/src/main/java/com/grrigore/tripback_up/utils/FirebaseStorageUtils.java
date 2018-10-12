package com.grrigore.tripback_up.utils;

import android.net.Uri;

import java.util.ArrayList;

public interface FirebaseStorageUtils {

    //Firebase storage
    void addImagesToStorage(ArrayList<Uri> imageURIs);

    void editImagesFromStorage(String tripId);

    void deleteImagesFromStorage(String tripId);

}
