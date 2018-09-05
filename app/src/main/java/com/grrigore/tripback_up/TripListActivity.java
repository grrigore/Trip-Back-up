package com.grrigore.tripback_up;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.grrigore.tripback_up.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.SEVEN_DAYS_IN_MILISECONDS;
import static com.grrigore.tripback_up.utils.Constants.SHARED_PREFERENCES;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_DESCRIPTION;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_TITLE;

//TODO on screen rotate
//TODO think

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.rlvTrips)
    RecyclerView rlvTrips;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private ValueEventListener tripsReferenceListener;
    private DatabaseReference tripsReference;

    private List<Trip> recentTrips;
    private List<Trip> pastTrips;
    private List<StorageReference> imageRefsRecent;
    private List<StorageReference> imageRefsPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        //create instance of firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recentTrips = new ArrayList<>();
        pastTrips = new ArrayList<>();
        imageRefsRecent = new ArrayList<>();
        imageRefsPast = new ArrayList<>();

        getAllTrips();
    }

    private void getAllTrips() {
        final Date currentDate = new Date();
        final long currentTime = currentDate.getTime();

        tripsReference = databaseReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/");
        tripsReferenceListener = tripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot tripsDataSnapshot = dataSnapshot.child("trips");
                for (DataSnapshot tripDataSnapshot : tripsDataSnapshot.getChildren()) {
                    Trip trip = new Trip();

                    trip.setTitle((String) tripDataSnapshot.child("title").getValue());
                    trip.setDescription((String) tripDataSnapshot.child("description").getValue());

                    DataSnapshot dateDataSnapshot = tripDataSnapshot.child("date");
                    Date date = new Date();
                    if (dateDataSnapshot.child("time").getValue() != null) {
                        date.setTime((long) dateDataSnapshot.child("time").getValue());
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

                    if (currentTime - date.getTime() <= SEVEN_DAYS_IN_MILISECONDS) {
                        recentTrips.add(trip);
                        //get first image form each trip
                        imageRefsRecent.add(firebaseStorage.getReferenceFromUrl(imageList.get(0)));
                    } else {
                        pastTrips.add(trip);
                        //get first image form each trip
                        imageRefsPast.add(firebaseStorage.getReferenceFromUrl(imageList.get(0)));
                    }
                }

                provideRecentTripsUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("-----Error-----", databaseError.getMessage());
            }
        });
    }

    private void populateTripList(final List<Trip> tripList, List<StorageReference> imageRefs) {
        TripAdapter tripAdapter = new TripAdapter(tripList, imageRefs, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        //set on item click listener
        tripAdapter.setItemClickListener(new TripAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
                sharedPreferencesEditor.putString(TRIP_CLICKED_TITLE, tripList.get(position).getTitle());
                sharedPreferencesEditor.putString(TRIP_CLICKED_DESCRIPTION, tripList.get(position).getDescription());
                sharedPreferencesEditor.apply();

                Intent tripDetailIntent = new Intent(TripListActivity.this, TripDetailActivity.class);
                tripDetailIntent.putExtra("tripClicked", tripList.get(position));
                //todo get correct tripId
                tripDetailIntent.putExtra("tripId", position);
                tripDetailIntent.putExtra("userUID", firebaseAuth.getCurrentUser().getUid());
                startActivity(tripDetailIntent);
            }
        });
        rlvTrips.setLayoutManager(layoutManager);
        rlvTrips.setItemAnimator(new DefaultItemAnimator());
        rlvTrips.setHasFixedSize(true);
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
                provideRecentTripsUI();
                return true;
            case R.id.pastTrips:
                providePastTripsUI();
                return true;
            case R.id.favTrips:
                //todo fav trip selection
                return true;
            case R.id.allTrips:
                allTripsMode();
                return true;
            case R.id.addTrip:
                Intent intent = new Intent(this, TripAdderActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void provideRecentTripsUI() {
        if (recentTrips.size() != 0) {
            setContentView(R.layout.activity_trip_list);
            ButterKnife.bind(TripListActivity.this);
            populateTripList(recentTrips, imageRefsRecent);
        } else {
            setContentView(R.layout.no_recent_trips_layout);
        }
    }

    //todo no trips layout
    public void allTripsMode() {
        setContentView(R.layout.activity_trip_list);

        ButterKnife.bind(TripListActivity.this);
        List<Trip> allTrips = new ArrayList<>(recentTrips);
        List<StorageReference> imageRefsAll = new ArrayList<>(imageRefsRecent);

        allTrips.addAll(pastTrips);
        imageRefsAll.addAll(imageRefsPast);
        populateTripList(allTrips, imageRefsAll);

        if (allTrips.size() == 0) {
            ToastUtil.showToast("No trips.. Add your first trip!", this);
        }
    }

    private void providePastTripsUI() {
        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(TripListActivity.this);
        populateTripList(pastTrips, imageRefsPast);
        if (pastTrips.size() == 0) {
            ToastUtil.showToast("No past trips!", this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tripsReference.removeEventListener(tripsReferenceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tripsReference.removeEventListener(tripsReferenceListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tripsReference.removeEventListener(tripsReferenceListener);
    }
}
