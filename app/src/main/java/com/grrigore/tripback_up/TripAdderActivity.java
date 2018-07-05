package com.grrigore.tripback_up;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripAdderActivity extends AppCompatActivity {

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.gvMedia)
    GridView gvMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_adder);

        //bind views
        ButterKnife.bind(this);
    }

    public void addPlace(View view) {
    }

    public void addMedia(View view) {
    }

    public void saveTrip(View view) {
    }
}
