package com.grrigore.tripback_up.network;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import static com.grrigore.tripback_up.utils.Constants.IMAGES;
import static com.grrigore.tripback_up.utils.Constants.IMG;
import static com.grrigore.tripback_up.utils.Constants.TRIP;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.USER;
import static com.grrigore.tripback_up.utils.Constants.USERS;

public class FirebaseStorageUtils {

    public FirebaseStorageUtils() {
        //empty constructor
    }

    //Firebase storage
    public void editImagesFromStorage(String tripId, String currentUser) {

    }

    public void addImagesToStorage(ArrayList<Uri> imageUris, String currentUser, StorageReference storageReference, long tripId) {
        //create storage reference for user folder
        //points to the trip folder
        StorageReference userReference = storageReference.child(USER).child(currentUser).child(TRIPS).child(TRIP + tripId);
        StorageReference imageReference;
        UploadTask uploadTask;

        //array list used to store images paths
        final ArrayList<String> imageNameList = new ArrayList<>();
        int i = 0;
        for (Uri imageUri : imageUris) {

            //create storage reference for user's image folder
            //points to the images folder
            imageReference = userReference.child(IMAGES).child(IMG + i);
            i++;
            uploadTask = imageReference.putFile(imageUri);
            imageNameList.add(imageUri.getPath());
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //todo change activity
                }
            });
        }
    }

    public void deleteImagesFromStorage(String tripId, String currentUser, DatabaseReference databaseReference, final FirebaseStorage firebaseStorage) {
        DatabaseReference imagesReference = databaseReference.child(USERS).child(currentUser)
                .child(TRIPS).child(tripId).child(IMAGES);
        imagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot image : dataSnapshot.getChildren()) {
                    final String imageRefecence = image.getValue().toString();

                    StorageReference imageStorageReference = firebaseStorage.getReferenceFromUrl(imageRefecence);

                    imageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void downloadImagesFromStorage(String tripId, String currentUser) {

    }

}
