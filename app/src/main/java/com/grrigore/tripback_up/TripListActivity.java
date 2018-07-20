package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.adapter.TripAdapter;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//TODO on screen rotate

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.rlvTrips)
    RecyclerView rlvTrips;

    private TripAdapter tripAdapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private long tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        ButterKnife.bind(this);


        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        //create instance of firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tripId = bundle.getLong("tripId");
            Log.d(TripListActivity.class.getSimpleName(), "Trip id sent from trip adder = " + tripId);
        } else {
            // Read from the database
            databaseReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tripId = (long) dataSnapshot.child("tripNumber").getValue();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TripListActivity.class.getSimpleName(), "Failed to read trip.");
                }
            });
        }

        setUI();
    }

    private void setUI() {
        databaseReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Trip> tripList = new ArrayList<>();
                final List<StorageReference> imageRefs = new ArrayList<>();

                DataSnapshot tripsDataSnapshot = dataSnapshot.child("trips");
                for (DataSnapshot tripDataSnapshot : tripsDataSnapshot.getChildren()) {
                    Trip trip = new Trip();
                    //Trip trip = tripDataSnapshot.getValue(Trip.class);

                    trip.setTitle((String) tripDataSnapshot.child("title").getValue());
                    trip.setDescription((String) tripDataSnapshot.child("description").getValue());


                    DataSnapshot dateDataSnapshot = tripDataSnapshot.child("date");
                    Date date = new Date();
                    if (dateDataSnapshot.child("time").getValue() != null) {
                        date.setTime((Long) dateDataSnapshot.child("time").getValue());
                    }
                    trip.setDate(date);

                    DataSnapshot imagesDataSnapshot = tripDataSnapshot.child("images");
                    List<String> imageList = new ArrayList<>();
                    for (int i = 1; i <= imagesDataSnapshot.getChildrenCount(); i++) {
                        imageList.add(String.valueOf(imagesDataSnapshot.child("img" + i).getValue()));
                    }
                    trip.setImages(imageList);

                    DataSnapshot placesDataSnapshot = tripDataSnapshot.child("places");
                    List<Place> placeList = new ArrayList<>();
                    for (int i = 0; i < placesDataSnapshot.getChildrenCount(); i++) {
                        Place place = new Place();
                        place.setLat((String) placesDataSnapshot.child(String.valueOf(i)).child("lat").getValue());
                        place.setLng((String) placesDataSnapshot.child(String.valueOf(i)).child("lng").getValue());
                        placeList.add(place);
                    }
                    trip.setPlaces(placeList);

                    tripList.add(trip);

                    //get first image form each trip
                    imageRefs.add(firebaseStorage.getReferenceFromUrl(imageList.get(0)));
                }

                tripAdapter = new TripAdapter(tripList, imageRefs, getApplicationContext());

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                //set on item click listener
                tripAdapter.setItemClickListener(new TripAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent tripDetailIntent = new Intent(TripListActivity.this, TripDetailActivity.class);
                        tripDetailIntent.putExtra("tripClicked", tripList.get(position));
                        tripDetailIntent.putExtra("tripId", position + 1);
                        tripDetailIntent.putExtra("userUID", firebaseAuth.getCurrentUser().getUid());
                        startActivity(tripDetailIntent);
                    }
                });
                rlvTrips.setLayoutManager(layoutManager);
                rlvTrips.setItemAnimator(new DefaultItemAnimator());
                rlvTrips.setAdapter(tripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("-----Error-----", databaseError.getMessage());
            }
        });
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
                tripId++;
                Log.d(TripListActivity.class.getSimpleName(), "Current trip id = " + tripId);
                Intent intent = new Intent(this, TripAdderActivity.class);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
