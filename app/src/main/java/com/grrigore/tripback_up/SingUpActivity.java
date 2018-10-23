package com.grrigore.tripback_up;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.TRIP_NUMBER;
import static com.grrigore.tripback_up.utils.Constants.USERS;
import static com.grrigore.tripback_up.utils.ToastUtil.showToast;

//todo on screen rotate

public class SingUpActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        //bind views with butterknife
        ButterKnife.bind(this);

        //hide toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * @param view This method allows the user to create a new account.
     */
    public void signup(View view) {
        String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast(getString(R.string.no_email), getApplicationContext());
        }

        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.no_password), getApplicationContext());
        }

        //toask este ok?
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (password.length() < 6) {
                        showToast(getString(R.string.password_shot), getApplicationContext());
                    } else {
                        showToast(getString(R.string.create_account_error), getApplicationContext());
                    }
                } else {
                    initialiseTripNumber();
                    showToast(getString(R.string.account_created), SingUpActivity.this);

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showToast(getString(R.string.email_verification), getApplicationContext());
                                        }
                                    }
                                });
                    }
                    startActivity(new Intent(SingUpActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    public void switchToLoginActivity(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(SingUpActivity.this, MainActivity.class), ActivityOptions.makeSceneTransitionAnimation(SingUpActivity.this).toBundle());
        } else {
            startActivity(new Intent(SingUpActivity.this, MainActivity.class));
        }
    }

    private void initialiseTripNumber() {
        databaseReference.child(USERS).child(firebaseAuth.getUid()).child(TRIP_NUMBER).setValue(0);
    }

}
