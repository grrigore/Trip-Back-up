package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

//todo startActivityOnResult
//todo rethink class structure
//todo search for places/address
public class MapsAdderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Place> places;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_adder);

        //array list used to store the places added
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
        ToastUtil.showToast("Marker added.", this);
    }

    public void saveMarkers(View view) {
        if (places.size() == 0) {
            ToastUtil.showToast("You haven't added any marker.", this);
        } else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("placeList", (ArrayList<? extends Parcelable>) places);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void cleanMarkers(View view) {
        places.clear();
        ToastUtil.showToast("Markers deleted.", this);
    }
}
