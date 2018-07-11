package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.rlvTrips)
    RecyclerView rlvTrips;

    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        tripId = 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.recentTrips:
                //todo recent trip selection
                return true;
            case R.id.pastTrips:
                //todo past trip selection
                return true;
            case R.id.favTrips:
                //todo fav trip selection
                return true;
            case R.id.addTrip:
                Intent intent = new Intent(this, TripAdderActivity.class);
                intent.putExtra("tripId", tripId);
                tripId++;
                Log.d("TRIP ID", String.valueOf(tripId));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
