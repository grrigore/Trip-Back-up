package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.adapter.TripAdapter;
import com.grrigore.tripback_up.model.Trip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.rlvTrips)
    RecyclerView rlvTrips;

    private TripAdapter tripAdapter;
    private List<Trip> tripList;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        ButterKnife.bind(this);

        tripId = 0;

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        //create instance of firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //points to the root reference
        StorageReference storageReference = firebaseStorage.getReference();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl("gs://trip-back-up-1530375802363.appspot.com/user/" + firebaseAuth.getCurrentUser().getUid() + "/trip" + tripId + "/images/img0");

        databaseReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                tripList.add(trip);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tripAdapter = new TripAdapter(tripList, imageRef, this);
        rlvTrips.setAdapter(tripAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.recentTrips:
                //todo recent trip selection
                return true;
            case R.id.pastTrips:
                //todo past trip selection
                return true;
            case R.id.favTrips:
                //todo fav trip selection
                return true;
            case R.id.addTrip:
                Intent intent = new Intent(this, TripAdderActivity.class);
                intent.putExtra("tripId", tripId);
                tripId++;
                Log.d("TRIP ID", String.valueOf(tripId));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
