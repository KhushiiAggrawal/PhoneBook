
package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView lottie = findViewById(R.id.lottie);

        new Handler((Looper.getMainLooper())).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent ihome = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(ihome);
                finish();
            }
        }, 2000);
    }
}