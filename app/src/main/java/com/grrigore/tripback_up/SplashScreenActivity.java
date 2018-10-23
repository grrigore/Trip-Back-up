package com.grrigore.tripback_up;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import butterknife.ButterKnife;

import static com.grrigore.tripback_up.utils.Constants.ANIMATION_DURATION;
import static com.grrigore.tripback_up.utils.Constants.ANIMATION_OFFSET;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        //hide toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imageTranslation();
        new SplashScreenAsyncTask().execute();
    }

    private void imageTranslation() {
        ImageView ivProgress = findViewById(R.id.ivProgress);

        float width = getDisplayWidth();

        float xCurrentPos;
        float yCurrentPos;
        xCurrentPos = ivProgress.getLeft();
        yCurrentPos = ivProgress.getTop();

        TranslateAnimation animation = new TranslateAnimation(xCurrentPos - ANIMATION_OFFSET, xCurrentPos + width + ANIMATION_OFFSET, yCurrentPos, yCurrentPos);
        animation.setDuration(ANIMATION_DURATION);
        animation.setFillAfter(true);
        animation.setFillBefore(true);
        animation.setInterpolator(new LinearInterpolator());
        ivProgress.startAnimation(animation);
    }

    private float getDisplayWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (float) displayMetrics.widthPixels;
    }

    private class SplashScreenAsyncTask extends AsyncTask<Void, Void, Void> {

        //todo check network connection
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(ANIMATION_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
