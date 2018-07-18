package com.grrigore.tripback_up;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.grrigore.tripback_up.model.Trip;

public class TripDetailActivity extends AppCompatActivity {

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
}
