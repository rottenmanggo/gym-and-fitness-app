package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.gymbrut.app.databinding.ActivityAuthBinding;
import com.gymbrut.app.repository.SessionManager;
import com.google.android.material.tabs.TabLayout;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;
    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Login"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Register"));
        binding.layoutName.setVisibility(View.GONE);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                isRegister = tab.getPosition() == 1;
                binding.layoutName.setVisibility(isRegister ? View.VISIBLE : View.GONE);
                binding.btnSubmit.setText(isRegister ? "Register" : "Login");
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        String email = String.valueOf(binding.etEmail.getText()).trim();
        String password = String.valueOf(binding.etPassword.getText()).trim();
        if (email.isEmpty() || password.isEmpty() || (isRegister && String.valueOf(binding.etName.getText()).trim().isEmpty())) {
            Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        new SessionManager(this).setLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
