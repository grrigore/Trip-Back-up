package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripEditorActivity extends AppCompatActivity {


    public static final int EDIT_PLACES_REQUEST = 1;

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;

    private Trip trip;
    private List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_editor);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trip = bundle.getParcelable("tripClicked");
            if (trip != null) {
                places = trip.getPlaces();
            }
        }

        setUI();
    }

    private void setUI() {
        etTitle.setText(trip.getTitle());
        etDescription.setText(trip.getDescription());
    }

    public void saveTrip(View view) {

    }

    public void editPlaces(View view) {
    }

    public void editMedia(View view) {
    }
}
