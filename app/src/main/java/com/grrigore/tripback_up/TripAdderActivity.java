package com.grrigore.tripback_up;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grrigore.tripback_up.model.Trip;
import com.grrigore.tripback_up.utils.AddImagesTask;
import com.grrigore.tripback_up.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripAdderActivity extends AppCompatActivity {


    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.lvMedia)
    ListView lvMedia;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;

    private ArrayList<Uri> imageURIs;
    private Trip trip;
    private Date date;
    private int tripId;

    public static final int PICK_IMAGE_REQUEST = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_adder);

        //bind views
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        tripId = bundle.getInt("tripId");
        trip = new Trip();
        imageURIs = new ArrayList<>();

        //get current time
        date = Calendar.getInstance().getTime();

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        //get database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    /**
     * @param view This method sends the user to a MapActivity.
     */
    public void addPlace(View view) {
        Intent intent = new Intent(this, MapsAdderActivity.class);
        intent.putExtra("tripId", tripId);
        startActivity(intent);
    }

    /**
     * @param view This method uses an intent to allow the user to pick images that he wants to add to the Trip object
     *             and stores the images in firebase storage.
     */
    public void addMedia(View view) {
        (new AddImagesTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }).execute();


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
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
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
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
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());

                        imageURIs = mArrayUri;

                        uploadImagesToFirebase();
                    }
                }
            } else {
                ToastUtil.showToast("You haven't picked Image", this);
            }
        } catch (Exception e) {
            ToastUtil.showToast("Something went wrong", this);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method is used to upload images to Firebase Storage.
     */
    private void uploadImagesToFirebase() {
        //create storage reference from our app
        //points to the root reference
        StorageReference storageReference = firebaseStorage.getReference();
        //create storage reference for user folder
        //points to the trip folder
        StorageReference userReference = storageReference.child("user/" + firebaseAuth.getCurrentUser().getUid()).child("trip" + tripId);
        StorageReference imageReference;
        UploadTask uploadTask;

        //array list used to store images paths
        final ArrayList<String> strings = new ArrayList<>();
        int i = 0;
        for (Uri imageURI : imageURIs) {

            //create storage reference for user's image folder
            //points to the images folder
            imageReference = userReference.child("images/" + "img" + i);
            i++;
            uploadTask = imageReference.putFile(imageURI);
            strings.add(imageURI.getPath());
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    ToastUtil.showToast("Upload fail!", getApplicationContext());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ToastUtil.showToast("Upload success!", getApplicationContext());

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, strings);
                    lvMedia.setAdapter(adapter);
                }
            });

            databaseReference.child("users").child(firebaseAuth.getUid()).child("trip" + tripId).child("images").child("img" + i).setValue(imageReference.toString());
        }
    }

    /**
     * @param view This method saves the Trip object to firebase database.
     */
    public void saveTrip(View view) {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        trip.setTitle(title);
        trip.setDescription(description);
        trip.setDate(date);
        databaseReference.child("users").child(firebaseAuth.getUid()).child("trip" + tripId).child("title").setValue(trip.getTitle());
        databaseReference.child("users").child(firebaseAuth.getUid()).child("trip" + tripId).child("description").setValue(trip.getDescription());
        databaseReference.child("users").child(firebaseAuth.getUid()).child("trip" + tripId).child("date").setValue(trip.getDate());

        ToastUtil.showToast("Trip saved!", getApplicationContext());
    }
}
