package com.grrigore.tripback_up;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.adapter.GalleryAdapter;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//todo on screen rotation

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.tvTripTitle)
    TextView tvTripTitle;
    @BindView(R.id.tvTripDescription)
    TextView tvTripDescription;
    @BindView(R.id.rvTripGallery)
    RecyclerView rvTripGallery;
    @BindView(R.id.mvTripPlaces)
    MapView mvTripPlaces;

    private FirebaseStorage firebaseStorage;

    private Trip trip;
    private String userUID;
    private int tripId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        ButterKnife.bind(this);

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trip = bundle.getParcelable("tripClicked");
            userUID = bundle.getString("userUID");
            tripId = bundle.getInt("tripId");
        }

        Log.d(getApplicationContext().getClass().getSimpleName(), trip.toString());

        setUI();
    }

    private void setUI() {
        tvTripTitle.setText(trip.getTitle());
        tvTripDescription.setText(trip.getDescription());

        final List<StorageReference> imageStorageReferences = new ArrayList<>();
        for (String imageUrl : trip.getImages()) {
            imageStorageReferences.add(firebaseStorage.getReferenceFromUrl(imageUrl));
        }

        GalleryAdapter galleryAdapter = new GalleryAdapter(imageStorageReferences, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        galleryAdapter.setItemClickListener(new GalleryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                firebaseStorage.getReference().child("user/" + userUID + "/trips/trip" + tripId + "/images/img" + position).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        rvTripGallery.setLayoutManager(layoutManager);
        rvTripGallery.setItemAnimator(new DefaultItemAnimator());
        rvTripGallery.setAdapter(galleryAdapter);

        mvTripPlaces.onCreate(null);
        mvTripPlaces.getMapAsync(this);
    }

    public void markAsFavourite(View view) {
        //todo save in local database
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(getApplicationContext(), MapsDetailActivity.class);
                intent.putParcelableArrayListExtra("places", (ArrayList<? extends Parcelable>) places);
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
}
