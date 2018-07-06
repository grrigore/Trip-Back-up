package com.grrigore.tripback_up;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grrigore.tripback_up.utils.AddImagesTask;

import java.net.URI;
import java.util.ArrayList;
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
    private List<URI> imageURIs;


    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_adder);

        //bind views
        ButterKnife.bind(this);

        imageURIs = new ArrayList<>();

        //create instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //create instance of firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

    }

    /**
     * @param view This method sends the user to a MapActivity.
     */
    public void addPlace(View view) {
        startActivity(new Intent(this, MapsAdderActivity.class));
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

        //create storage reference from our app
        //points to the root reference
        StorageReference storageReference = firebaseStorage.getReference();

        //create a child reference for images
        //points to "images"
        StorageReference imagesReference = storageReference.child("images");


        //create a child reference for videos
        //points to "videos"
        StorageReference videosReference = storageReference.child("videos");


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Log.d("IMAGEURI", selectedImageUri.toString());
            }

        }
    }

    /**
     * @param view This method saves the Trip object to firebase database.
     */
    public void saveTrip(View view) {

        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();


    }
}
