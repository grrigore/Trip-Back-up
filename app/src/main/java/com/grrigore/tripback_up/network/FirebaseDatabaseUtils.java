package com.grrigore.tripback_up.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.grrigore.tripback_up.R;
import com.grrigore.tripback_up.TripAdderActivity;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.utils.ToastUtil;

import static com.grrigore.tripback_up.utils.Constants.DESC;
import static com.grrigore.tripback_up.utils.Constants.IMAGES;
import static com.grrigore.tripback_up.utils.Constants.IMG;
import static com.grrigore.tripback_up.utils.Constants.PLACE;
import static com.grrigore.tripback_up.utils.Constants.TIME;
import static com.grrigore.tripback_up.utils.Constants.TITLE;
import static com.grrigore.tripback_up.utils.Constants.TRIP;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.TRIP_NUMBER;
import static com.grrigore.tripback_up.utils.Constants.USERS;

public class FirebaseDatabaseUtils {

    public FirebaseDatabaseUtils() {
        //empty constructor
    }

    //Firebase database
    public void addTripToDatabase(Trip trip, String currentUser, DatabaseReference databaseReference, long tripId, FirebaseAuth firebaseAuth, Context context) {
        //toask cum pot scapa de astea? fac o variabila?
        DatabaseReference tripReference = databaseReference.child(USERS).child(currentUser).child(TRIPS).child(TRIP + tripId);
        int placeId = 0;
        int imageId = 1;

        tripReference.child(TITLE).setValue(trip.getTitle());
        tripReference.child(DESC).setValue(trip.getDescription());
        tripReference.child(TIME).setValue(trip.getTime());

        for (Place place : trip.getPlaces()) {
            tripReference.child(PLACE).child(String.valueOf(placeId)).setValue(place);
            placeId++;
        }

        for (String imageRef : trip.getImages()) {
            tripReference.child(IMAGES).child(IMG + imageId).setValue(imageRef);
            imageId++;
        }


        tripId++;
        databaseReference.child(USERS).child(firebaseAuth.getUid()).child(TRIP_NUMBER).setValue(tripId);

        ToastUtil.showToast(context.getString(R.string.trip_saved), context);

        Log.d(TripAdderActivity.class.getSimpleName(), "Current trip id = " + tripId);
    }

    public void editTripFromDatabase(Trip trip, String currentUser, DatabaseReference databaseReference, Context context) {

        DatabaseReference tripReference = databaseReference.child(USERS).child(currentUser).child(TRIPS).child(trip.getId());

        tripReference.child(TITLE).setValue(trip.getTitle());
        tripReference.child(DESC).setValue(trip.getDescription());

        ToastUtil.showToast(context.getString(R.string.trip_saved), context);
    }

    public void deleteTripFromDatabase(String tripId, String currentUser, DatabaseReference databaseReference) {
        DatabaseReference tripReference = databaseReference.child(USERS).child(currentUser)
                .child(TRIPS).child(tripId);
        tripReference.removeValue();
        final DatabaseReference tripNumberReference = databaseReference.child(USERS)
                .child(currentUser).child(TRIP_NUMBER);
        tripNumberReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfTrips = (long) dataSnapshot.getValue();
                numberOfTrips--;
                tripNumberReference.setValue(numberOfTrips);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error: ", databaseError.getMessage());
            }
        });
    }

}
