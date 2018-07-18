package com.grrigore.tripback_up;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.grrigore.tripback_up.model.Trip;

import butterknife.BindView;

public class TripDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvTripTitle)
    TextView tvTripTitle;
    @BindView(R.id.tvTripDescription)
    TextView tvTripDescription;
    @BindView(R.id.rvTripGallery)
    RecyclerView rvTripGallery;
    @BindView(R.id.mvTripPlaces)
    MapView mvTripPlaces;

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trip = bundle.getParcelable("tripClicked");
        }

        Log.d(getApplicationContext().getClass().getSimpleName(), trip.toString());

    }

    public void markAsFavourite(View view) {
    }

    public void openDetailMap(View view) {
    }
}
