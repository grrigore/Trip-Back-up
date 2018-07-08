package com.grrigore.tripback_up;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grrigore.tripback_up.model.Place;

import java.util.ArrayList;
import java.util.List;

public class MapsAdderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Place> places;
    private Place place;
    int id = 0;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_adder);

        //array list used to store the places added
        places = new ArrayList<>();

        //get database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //move the camera to the center of the map
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0, 0)));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
                place = new Place(Double.toString(latLng.latitude), Double.toString(latLng.longitude));
            }
        });
    }

    public void addMarkerToMap(View view) {
        places.add(place);
    }

    public void saveMarkers(View view) {
        if (id != 0) {
            for (int i = id; i < places.size(); i++) {
                Log.d("PLACES", place.getLat() + " " + place.getLng());
                writeNewPlace(places.get(i).getLat(), places.get(i).getLng());
            }
        } else {
            for (int i = 0; i < places.size(); i++) {
                Log.d("PLACES", place.getLat() + " " + place.getLng());
                writeNewPlace(places.get(i).getLat(), places.get(i).getLng());
            }
        }

    }

    private void writeNewPlace(String lat, String lng) {
        Place place = new Place(lat, lng);
        databaseReference.child("users").child(firebaseAuth.getUid()).child("places").child(String.valueOf(id)).setValue(place);
        id++;
    }

    public void cleanMarkers(View view) {
        places.clear();
        id = 0;
        databaseReference.child("users").child(firebaseAuth.getUid()).child("places").removeValue();
    }
}
