package com.grrigore.tripback_up;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.grrigore.tripback_up.adapter.GalleryAdapter;
import com.grrigore.tripback_up.data.PlaceDao;
import com.grrigore.tripback_up.data.TripDao;
import com.grrigore.tripback_up.data.TripsDatabase;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.network.FirebaseDatabaseUtils;
import com.grrigore.tripback_up.network.FirebaseStorageUtils;
import com.grrigore.tripback_up.utils.Constants;
import com.grrigore.tripback_up.utils.ToastUtil;
import com.grrigore.tripback_up.widget.TripWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.IMAGES;
import static com.grrigore.tripback_up.utils.Constants.PLACE_LIST_KEY_MDA_TDA;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.TRIP_NUMBER;
import static com.grrigore.tripback_up.utils.Constants.USERS;


//todo on screen rotation

//todo rethink trip gallery
public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        FirebaseDatabaseUtils, FirebaseStorageUtils {

    @BindView(R.id.tvTripTitle)
    TextView tvTripTitle;
    @BindView(R.id.tvTripDescription)
    TextView tvTripDescription;
    @BindView(R.id.rvTripGallery)
    RecyclerView rvTripGallery;
    @BindView(R.id.mvTripPlaces)
    MapView mvTripPlaces;

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;

    private Trip trip;
    private String userUID;
    private String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        ButterKnife.bind(this);

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //todo constants files per class with var names connected with the entity
            trip = bundle.getParcelable("tripClicked");
            userUID = bundle.getString("userUID");
            tripId = bundle.getString("tripId");
            Log.d(this.getApplicationContext().getClass().getSimpleName(), "Trip id = " + tripId);
        }

        Log.d(getApplicationContext().getClass().getSimpleName(), trip.toString());

        setUI();
    }

    private void setUI() {
        tvTripTitle.setText(trip.getTitle());
        tvTripDescription.setText(trip.getDescription());

        final List<StorageReference> imageStorageReferences = new ArrayList<>();
        for (String imageUrl : trip.getImages()) {
            Log.d(getApplicationContext().getClass().getSimpleName(), "\n" + "Image Url: " + imageUrl + "\n");
            imageStorageReferences.add(firebaseStorage.getReferenceFromUrl(imageUrl));
        }

        GalleryAdapter galleryAdapter = new GalleryAdapter(imageStorageReferences, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        galleryAdapter.setItemClickListener(new GalleryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                firebaseStorage.getReference().child("user/" + userUID + "/trips/" + tripId + "/images/img" + position).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent displayImageIntent = new Intent();
                        displayImageIntent.setAction(Intent.ACTION_VIEW);
                        displayImageIntent.setDataAndType(uri, "image/*");
                        startActivity(displayImageIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(getApplicationContext().getClass().getSimpleName(), e.getMessage());
                    }
                });
            }
        });
        //todo recycler view space between views
        rvTripGallery.setLayoutManager(layoutManager);
        rvTripGallery.setItemAnimator(new DefaultItemAnimator());
        rvTripGallery.setHasFixedSize(true);
        rvTripGallery.setAdapter(galleryAdapter);

        mvTripPlaces.onCreate(null);
        mvTripPlaces.getMapAsync(this);
    }

    public void markAsFavourite(View view) {
        //todo save in local database
        TripDao tripDao = TripsDatabase.getInstance(getApplicationContext()).getTripDao();
        PlaceDao placeDao = TripsDatabase.getInstance(getApplicationContext()).getPlaceDao();

        //set 1 to mark trip as favourite
        trip.setFavourite(1);
        Log.d(getClass().getSimpleName(), "Trip id: " + trip.getId());
        Log.d(getClass().getSimpleName(), "Trip title: " + trip.getTitle());
        Log.d(getClass().getSimpleName(), "Trip time: " + trip.getTime());
        Log.d(getClass().getSimpleName(), "Trip description: " + trip.getDescription());
        Log.d(getClass().getSimpleName(), "Trip favourite: " + trip.getFavourite());


        tripDao.insert(trip);

        for (Place place : trip.getPlaces()) {
            //set trip id for each place
            place.setTripId(trip.getId());

            Log.d(getClass().getSimpleName(), "Place id:" + place.getId());
            Log.d(getClass().getSimpleName(), "Place lat:" + place.getLat());
            Log.d(getClass().getSimpleName(), "Place lng:" + place.getLng());
            Log.d(getClass().getSimpleName(), "Place tripId:" + place.getTripId());

            placeDao.insert(place);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap gMap = googleMap;
        final List<Place> places = trip.getPlaces();
        List<Marker> markers = new ArrayList<>();
        for (Place place : places) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(Double.parseDouble(place.getLat()), Double.parseDouble(place.getLng())));
            Marker marker = googleMap.addMarker(markerOptions);
            markers.add(marker);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 50;
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                gMap.moveCamera(cu);
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(getApplicationContext(), MapsDetailActivity.class);
                intent.putParcelableArrayListExtra(PLACE_LIST_KEY_MDA_TDA, (ArrayList<? extends Parcelable>) places);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        mvTripPlaces.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mvTripPlaces.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mvTripPlaces.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvTripPlaces.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.deleteTrip:
                String currentUser = FirebaseAuth.getInstance().getUid();
                deleteImagesFromStorage(tripId, currentUser);
                deleteTripFromDatabase(tripId, currentUser);
                Intent intent = new Intent(this, TripListActivity.class);
                startActivity(intent);
                return true;
            case R.id.addWidget:
                SharedPreferences sharedPreferences = TripDetailActivity.this.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                Bundle bundle = new Bundle();

                int widgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                TripWidgetProvider.updateAppWidget(getApplicationContext(), appWidgetManager, widgetId, sharedPreferences.getString(Constants.TRIP_CLICKED_TITLE, null),
                        sharedPreferences.getString(Constants.TRIP_CLICKED_DESCRIPTION, null));
                ToastUtil.showToast("Widget set for " + trip.getTitle() + "!", this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void deleteTripFromDatabase(String tripId, String currentUser) {
        DatabaseReference tripReference = firebaseDatabase.getReference().child(USERS).child(currentUser).child(TRIPS).child(tripId);
        tripReference.removeValue();
        final DatabaseReference tripNumberReference = firebaseDatabase.getReference().child(USERS).child(currentUser).child(TRIP_NUMBER);
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
        DatabaseReference imagesReference = firebaseDatabase.getReference().child(USERS).child(currentUser).child(TRIPS).child(tripId).child(IMAGES);
        imagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot image : dataSnapshot.getChildren()) {
                    final String imageRefecence = image.getValue().toString();

                    StorageReference imageStorageReference = firebaseStorage.getReferenceFromUrl(imageRefecence);
                    Log.d(getApplicationContext().getClass().getSimpleName(), "Image storage reference: " + imageStorageReference);

                    imageStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(getApplicationContext().getClass().getSimpleName(), "Deleted file: " + imageRefecence);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(getApplicationContext().getClass().getSimpleName(), "Cannot delete file: " + imageRefecence);
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
    public void addTripToDatabase(Trip trip, String currentUser) {
    }

    @Override
    public void editTripFromDatabase(String tripId, String currentUser) {
    }

    @Override
    public void addImagesToStorage(ArrayList<Uri> imageURIs, String currentUser) {
    }

    @Override
    public void editImagesFromStorage(String tripId, String currentUser) {
    }
}
