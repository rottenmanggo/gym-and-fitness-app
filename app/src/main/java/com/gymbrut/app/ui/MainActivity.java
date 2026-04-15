package com.gymbrut.app.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.gymbrut.app.R;
import com.gymbrut.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        switchFragment(new HomeFragment());
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) return switchFragment(new HomeFragment());
            if (item.getItemId() == R.id.nav_workout) return switchFragment(new WorkoutFragment());
            if (item.getItemId() == R.id.nav_progress) return switchFragment(new ProgressFragment());
            if (item.getItemId() == R.id.nav_membership) return switchFragment(new MembershipFragment());
            return switchFragment(new AccountFragment());
        });
    }

    private boolean switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        return true;
    }
}
