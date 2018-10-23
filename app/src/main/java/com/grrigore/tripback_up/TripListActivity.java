package com.grrigore.tripback_up;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.adapter.TripAdapter;
import com.grrigore.tripback_up.data.PlaceDao;
import com.grrigore.tripback_up.data.TripDao;
import com.grrigore.tripback_up.data.TripsDatabase;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.network.FirebaseDatabaseUtils;
import com.grrigore.tripback_up.network.FirebaseStorageUtils;
import com.grrigore.tripback_up.utils.SharedPrefs;
import com.grrigore.tripback_up.utils.ToastUtil;
import com.grrigore.tripback_up.widget.TripWidgetProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.DESC;
import static com.grrigore.tripback_up.utils.Constants.IMAGES;
import static com.grrigore.tripback_up.utils.Constants.IMG;
import static com.grrigore.tripback_up.utils.Constants.LAT;
import static com.grrigore.tripback_up.utils.Constants.LNG;
import static com.grrigore.tripback_up.utils.Constants.PLACE;
import static com.grrigore.tripback_up.utils.Constants.SEVEN_DAYS_IN_MILLISECONDS;
import static com.grrigore.tripback_up.utils.Constants.TIME;
import static com.grrigore.tripback_up.utils.Constants.TITLE;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_DESCRIPTION;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_TITLE;
import static com.grrigore.tripback_up.utils.Constants.TRIP_ID;
import static com.grrigore.tripback_up.utils.Constants.TRIP_NUMBER;
import static com.grrigore.tripback_up.utils.Constants.USERS;
import static com.grrigore.tripback_up.utils.Constants.CURRENT_USER;

//todo on screen rotate

public class TripListActivity extends AppCompatActivity implements TripAdapter.ItemClickListener,
        TripAdapter.ItemLongClickListener, FirebaseDatabaseUtils, FirebaseStorageUtils {

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
        final long currentTime = new Date().getTime();

        tripsReference = databaseReference.child(USERS).child(firebaseAuth.getCurrentUser().getUid());
        tripsReferenceListener = tripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot tripsDataSnapshot = dataSnapshot.child(TRIPS);
                for (DataSnapshot tripDataSnapshot : tripsDataSnapshot.getChildren()) {
                    Trip trip = new Trip();

                    trip.setId(tripDataSnapshot.getKey());
                    trip.setTitle((String) tripDataSnapshot.child(TITLE).getValue());
                    trip.setDescription((String) tripDataSnapshot.child(DESC).getValue());

                    trip.setTime((long) tripDataSnapshot.child(TIME).getValue());

                    DataSnapshot imagesDataSnapshot = tripDataSnapshot.child(IMAGES);
                    List<String> imageList = new ArrayList<>();
                    for (int i = 1; i <= imagesDataSnapshot.getChildrenCount(); i++) {
                        imageList.add(String.valueOf(imagesDataSnapshot.child(IMG + i).getValue()));
                    }
                    trip.setImages(imageList);

                    Log.d(getApplicationContext().getClass().getSimpleName(),
                            "\n" + "Image List: " + imageList.get(0) + "\n");

                    DataSnapshot placesDataSnapshot = tripDataSnapshot.child(PLACE);
                    List<Place> placeList = new ArrayList<>();
                    for (int i = 0; i < placesDataSnapshot.getChildrenCount(); i++) {
                        Place place = new Place();
                        place.setLat((String) placesDataSnapshot.child(String.valueOf(i))
                                .child(LAT).getValue());
                        place.setLng((String) placesDataSnapshot.child(String.valueOf(i))
                                .child(LNG).getValue());
                        placeList.add(place);
                    }
                    trip.setPlaces(placeList);

                    if (currentTime - trip.getTime() <= SEVEN_DAYS_IN_MILLISECONDS) {
                        recentTrips.add(trip);
                        //get first image form each trip
                        imageRefsRecent.add(firebaseStorage.getReferenceFromUrl(imageList.get(0)));
                    } else {
                        pastTrips.add(trip);
                        //get first image form each trip
                        imageRefsPast.add(firebaseStorage.getReferenceFromUrl(imageList.get(0)));
                    }
                }

                provideTripsForUi(recentTrips, imageRefsRecent, R.id.recentTrips);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("-----Error-----", databaseError.getMessage());
            }
        });
    }

    private void populateTripList(List<Trip> tripList, List<StorageReference> imageRefs) {
        TripAdapter tripAdapter = new TripAdapter(tripList, imageRefs, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),
                2);
        //set on item click listener
        tripAdapter.setItemClickListener(this);
        tripAdapter.setOnLongClickListener(this);
        tripAdapter.notifyDataSetChanged();

        //todo recycler view space between views
        rlvTrips.setLayoutManager(layoutManager);
        rlvTrips.setItemAnimator(new DefaultItemAnimator());
        rlvTrips.setHasFixedSize(true);
        rlvTrips.setLongClickable(true);
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
        //todo transitions between layouts
        switch (id) {
            case R.id.recentTrips:
                provideTripsForUi(recentTrips, imageRefsRecent, id);
                return true;
            case R.id.pastTrips:
                provideTripsForUi(pastTrips, imageRefsPast, id);
                return true;
            case R.id.favTrips:
                //todo fav trip selection

                TripDao tripDao = TripsDatabase.getInstance(getApplicationContext()).getTripDao();
                PlaceDao placeDao = TripsDatabase.getInstance(getApplicationContext()).getPlaceDao();

                List<Trip> favouriteTrips = new ArrayList<>();

                List<Trip> databaseTrips = tripDao.getAllTrips();
                for (Trip trip : databaseTrips) {
                    List<Place> tripPlaces = placeDao.getPlacesByTripId(trip.getId());
                    trip.setPlaces(tripPlaces);
                    favouriteTrips.add(trip);
                }

                provideTripsForUi(favouriteTrips, null, id);

                return true;
            case R.id.allTrips:
                List<Trip> allTrips = mergeTrips();
                List<StorageReference> imageRefsAll = mergeImageRefs();
                provideTripsForUi(allTrips, imageRefsAll, id);
                return true;
            case R.id.addTrip:
                Intent intent = new Intent(this, TripAdderActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void provideTripsForUi(List<Trip> trips, List<StorageReference> tripImages,
                                   int menuItemId) {
        if (trips.size() != 0) {
            setContentView(R.layout.activity_trip_list);
            ButterKnife.bind(TripListActivity.this);
            populateTripList(trips, tripImages);
        } else if (menuItemId == R.id.recentTrips) {
            setContentView(R.layout.no_recent_trips_layout);
        } else if (menuItemId == R.id.pastTrips) {
            //todo UI for no past trips
            ToastUtil.showToast("No past trips!", this);
        } else if (menuItemId == R.id.allTrips) {
            //todo UI for no trips
            ToastUtil.showToast("No trips.. Add your first trip!", this);
        }
    }

    public void allTripsMode(View view) {
        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(TripListActivity.this);

        List<Trip> allTrips = mergeTrips();
        List<StorageReference> imageRefsAll = mergeImageRefs();

        populateTripList(allTrips, imageRefsAll);

        if (allTrips.size() == 0) {
            //todo UI for no trips
            ToastUtil.showToast("No trips.. Add your first trip!", this);
        }
    }

    @NonNull
    private List<StorageReference> mergeImageRefs() {
        List<StorageReference> imageRefsAll = new ArrayList<>(imageRefsRecent);
        imageRefsAll.addAll(imageRefsPast);
        return imageRefsAll;
    }

    @NonNull
    private List<Trip> mergeTrips() {
        List<Trip> allTrips = new ArrayList<>(recentTrips);
        allTrips.addAll(pastTrips);
        return allTrips;
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

    @Override
    public void onItemClick(View view, Trip trip) {
        SharedPrefs.getInstance(getApplicationContext()).setValue(TRIP_CLICKED_TITLE, trip.getTitle());
        SharedPrefs.getInstance(getApplicationContext()).setValue(TRIP_CLICKED_DESCRIPTION, trip.getDescription());

        Intent tripDetailIntent = new Intent(TripListActivity.this, TripDetailActivity.class);
        tripDetailIntent.putExtra(TRIP_CLICKED, trip);
        tripDetailIntent.putExtra(TRIP_ID, trip.getId());
        tripDetailIntent.putExtra(CURRENT_USER, firebaseAuth.getCurrentUser().getUid());
        startActivity(tripDetailIntent);
    }

    @Override
    public void onLongItemClick(View view, final Trip trip) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.trip_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                String currentUser = firebaseAuth.getUid();
                switch (id) {
                    case R.id.editTrip:
                        Intent tripEditorIntent = new Intent(TripListActivity.this, TripEditorActivity.class);
                        tripEditorIntent.putExtra(TRIP_CLICKED, trip);
                        tripEditorIntent.putExtra(CURRENT_USER, currentUser);
                        startActivity(tripEditorIntent);
                        return true;
                    case R.id.deleteTrip:
                        deleteImagesFromStorage(trip.getId(), currentUser);
                        deleteTripFromDatabase(trip.getId(), currentUser);
                        ToastUtil.showToast(getString(R.string.trip_deleted), getApplicationContext());
                        //todo refresh UI after trip is deleted
                        finish();
                        startActivity(getIntent());
                        return true;
                    case R.id.addWidget:
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

                        Bundle bundle = new Bundle();
                        int widgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                        String tripTitle = SharedPrefs.getInstance(getApplicationContext()).getStringValue(TRIP_CLICKED_TITLE, null);
                        String tripDescription = SharedPrefs.getInstance(getApplicationContext()).getStringValue(TRIP_CLICKED_DESCRIPTION, null);
                        TripWidgetProvider.updateAppWidget(getApplicationContext(), appWidgetManager, widgetId, tripTitle, tripDescription);

                        ToastUtil.showToast(getString(R.string.wiget_set_for) + trip.getTitle() + getString(R.string.exclamation_mark), getApplicationContext());
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    public void deleteTripFromDatabase(String tripId, String currentUser) {
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

    @Override
    public void deleteImagesFromStorage(String tripId, String currentUser) {
        DatabaseReference imagesReference = databaseReference.child(USERS).child(currentUser)
                .child(TRIPS).child(tripId).child(IMAGES);
        imagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot image : dataSnapshot.getChildren()) {
                    final String imageRefecence = image.getValue().toString();

                    StorageReference imageStorageReference = firebaseStorage.getReferenceFromUrl(imageRefecence);

                    Log.d(getApplicationContext().getClass().getSimpleName(),
                            "Image storage reference: " + imageStorageReference);

                    imageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(getApplicationContext().getClass().getSimpleName(),
                                    "Deleted file: " + imageRefecence);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(getApplicationContext().getClass().getSimpleName(),
                                    "Cannot delete file: " + imageRefecence);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void downloadImagesFromStorage(String tripId, String currentUser) {

    }

    @Override
    public void addTripToDatabase(Trip trip, String currentUser) {
    }

    @Override
    public void editTripFromDatabase(String tripId, String currentUser) {
    }

    @Override
    public void addImagesToStorage(ArrayList<Uri> imageUris, String currentUser) {
    }

    @Override
    public void editImagesFromStorage(String tripId, String currentUser) {
    }
}
