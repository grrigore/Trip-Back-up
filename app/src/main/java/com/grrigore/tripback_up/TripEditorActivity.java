package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.network.FirebaseDatabaseUtils;
import com.grrigore.tripback_up.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.DESC;
import static com.grrigore.tripback_up.utils.Constants.TITLE;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED;
import static com.grrigore.tripback_up.utils.Constants.USERS;
import static com.grrigore.tripback_up.utils.Constants.CURRENT_USER;

public class TripEditorActivity extends AppCompatActivity implements FirebaseDatabaseUtils {


    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;

    private FirebaseDatabase firebaseDatabase;

    private Trip trip;
    private List<Place> places;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_editor);

        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            trip = bundle.getParcelable(TRIP_CLICKED);
            if (trip != null) {
                places = trip.getPlaces();
            }
            currentUser = bundle.getString(CURRENT_USER);
        }

        setUI();
    }

    private void setUI() {
        etTitle.setText(trip.getTitle());
        etDescription.setText(trip.getDescription());
    }

    public void saveTrip(View view) {
        trip.setTitle(String.valueOf(etTitle.getText()));
        trip.setDescription(String.valueOf(etDescription.getText()));
        editTripFromDatabase(trip.getId(), currentUser);
        Intent tripListIntent = new Intent(TripEditorActivity.this, TripListActivity.class);
        startActivity(tripListIntent);
    }

    //todo edit places
    public void editPlaces(View view) {
    }

    //todo edit images
    public void editMedia(View view) {
    }

    @Override
    public void editTripFromDatabase(String tripId, String currentUser) {
        DatabaseReference tripReference = firebaseDatabase.getReference().child(USERS).child(currentUser).child(TRIPS).child(tripId);

        tripReference.child(TITLE).setValue(trip.getTitle());
        tripReference.child(DESC).setValue(trip.getDescription());

        ToastUtil.showToast(getString(R.string.trip_saved), getApplicationContext());
    }

    @Override
    public void addTripToDatabase(Trip trip, String currentUser) {

    }

    @Override
    public void deleteTripFromDatabase(String tripId, String currentUser) {

    }
}
