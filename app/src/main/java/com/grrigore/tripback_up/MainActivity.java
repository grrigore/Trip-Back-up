package com.grrigore.tripback_up;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.ToastUtil.showToast;


//todo on screen rotate
//todo network state check

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvCreateAccount)
    TextView tvCreateAccount;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();


        //bind views with butterknife
        ButterKnife.bind(this);

        //hide toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        onCreateAccountClick();
    }

    private void onCreateAccountClick() {
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(MainActivity.this, SingUpActivity.class),
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                } else {
                    startActivity(new Intent(MainActivity.this, SingUpActivity.class));
                }
            }
        });
    }

    /**
     * @param view This method allows the user to login using his credentials.
     */
    public void login(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast(getString(R.string.no_email), getApplicationContext());
        }

        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.no_password), getApplicationContext());
        }

        if (password.length() < 6) {
            showToast(getString(R.string.wrong_credentials), getApplicationContext());
        }

        //toask este ok?
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            showToast(getString(R.string.wrong_credentials), getApplicationContext());
                        } else {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            Log.d(getClass().getSimpleName(), "Is verified:  " + currentUser.isEmailVerified());
                            if (currentUser.isEmailVerified()) {
                                Intent intent = new Intent(MainActivity.this, TripListActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast(getString(R.string.email_verification), getApplicationContext());
                            }
                        }
                    }
                });

    }
}
