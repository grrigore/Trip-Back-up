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
import com.grrigore.tripback_up.model.Place;

import java.util.ArrayList;
import java.util.List;

public class MapsAdderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Place> places;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_adder);

        places = new ArrayList<>();

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
        //todo save markers to firebase database
        for (Place place : places) {
            Log.d("PLACES", place.getLat() + " " + place.getLng());
        }
    }
}
