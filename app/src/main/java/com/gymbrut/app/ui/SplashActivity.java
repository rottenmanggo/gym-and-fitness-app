package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.gymbrut.app.databinding.ActivitySplashBinding;
import com.gymbrut.app.repository.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding.inflate(getLayoutInflater());
        SessionManager sessionManager = new SessionManager(this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Class<?> next = !sessionManager.isOnboardingDone() ? OnboardingActivity.class :
                    (sessionManager.isLoggedIn() ? MainActivity.class : AuthActivity.class);
            startActivity(new Intent(this, next));
            finish();
        }, 1500);
    }
}
