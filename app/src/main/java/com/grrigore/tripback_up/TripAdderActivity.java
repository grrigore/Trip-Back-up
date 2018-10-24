package com.grrigore.tripback_up;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.model.Place;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.network.FirebaseDatabaseUtils;
import com.grrigore.tripback_up.network.FirebaseStorageUtils;
import com.grrigore.tripback_up.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.ID;
import static com.grrigore.tripback_up.utils.Constants.IMAGES;
import static com.grrigore.tripback_up.utils.Constants.IMG;
import static com.grrigore.tripback_up.utils.Constants.PLACE_LIST_KEY;
import static com.grrigore.tripback_up.utils.Constants.TRIP;
import static com.grrigore.tripback_up.utils.Constants.TRIPS;
import static com.grrigore.tripback_up.utils.Constants.TRIP_NUMBER;
import static com.grrigore.tripback_up.utils.Constants.USER;
import static com.grrigore.tripback_up.utils.Constants.USERS;

//todo on screen rotate

public class TripAdderActivity extends AppCompatActivity {


    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int PICK_PLACE_REQUEST = 2;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.lvMedia)
    ListView lvMedia;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayList<Uri> imageUris;
    private Trip trip;
    private Date date;
    private long tripId;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private List<Place> placeList;
    private boolean placesAdded = false;

    private FirebaseDatabaseUtils firebaseDatabaseUtils;
    private FirebaseStorageUtils firebaseStorageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_adder);

        //bind views
        ButterKnife.bind(this);

        trip = new Trip();
        imageUris = new ArrayList<>();

        //get current time
        date = Calendar.getInstance().getTime();

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        //get database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //get storage reference
        storageReference = firebaseStorage.getReference();

        firebaseDatabaseUtils = new FirebaseDatabaseUtils();
        firebaseStorageUtils = new FirebaseStorageUtils();


        //read number of trips from the database
        //error POSSIBLE REASON
        databaseReference.child(USERS).child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripId = (long) dataSnapshot.child(TRIP_NUMBER).getValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TripListActivity.class.getSimpleName(), "Failed to read trip.");
            }
        });
    }

    /**
     * @param view This method sends the user to a MapActivity.
     */
    public void addPlace(View view) {
        Intent intent = new Intent(this, MapAdderActivity.class);
        startActivityForResult(intent, PICK_PLACE_REQUEST);
    }

    /**
     * @param view This method uses an intent to allow the user to pick images that he wants to add to the Trip object
     *             and stores the images in firebase storage.
     */
    public void addMedia(View view) {
        //todo review add image to firebase storage
        //todo add video
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //todo review this method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }

                        imageUris = mArrayUri;
                    }
                }
            } else if (requestCode == PICK_IMAGE_REQUEST) {
                ToastUtil.showToast(getString(R.string.no_picked_image_warning), this);
            }

            if (requestCode == PICK_PLACE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                placeList = data.getParcelableArrayListExtra(PLACE_LIST_KEY);
                if (placeList.size() != 0) {
                    placesAdded = true;
                    ToastUtil.showToast(getString(R.string.places_added), getApplicationContext());
                }
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * @param view This method saves the Trip object to firebase database.
     */
    public void saveTrip(View view) {
        String title = null;
        String description = null;
        boolean ok;

        if ((!etTitle.getText().toString().isEmpty()) &&
                (!etDescription.getText().toString().isEmpty()) &&
                (imageUris.size() != 0) &&
                (placesAdded)) {
            title = etTitle.getText().toString();
            description = etDescription.getText().toString();
            ok = true;
        } else {
            ok = false;
        }

        if (ok) {
            trip.setTitle(title);
            trip.setDescription(description);
            trip.setTime(date.getTime());
            trip.setPlaces(placeList);
            List<String> imageList = new ArrayList<>();
            for (int i = 0; i < imageUris.size(); i++) {
                imageList.add(storageReference.child(USER).child(firebaseAuth.getCurrentUser().getUid())
                        .child(TRIPS).child(TRIP + tripId).child(IMAGES).child(IMG + i).toString());
            }
            trip.setImages(imageList);

            String currentUser = firebaseAuth.getUid();
            //create storage reference from our app
            //points to the root reference
            storageReference = firebaseStorage.getReference();
            firebaseStorageUtils.addImagesToStorage(imageUris, currentUser, storageReference, tripId);
            firebaseDatabaseUtils.addTripToDatabase(trip, currentUser, databaseReference, tripId, firebaseAuth, getApplicationContext());
            Intent intentRecentTrips = new Intent(this, TripListActivity.class);
            intentRecentTrips.putExtra(ID, tripId);
            startActivity(intentRecentTrips);
        } else {
            ToastUtil.showToast(getString(R.string.trip_not_saved), getApplicationContext());
        }

    }
}
