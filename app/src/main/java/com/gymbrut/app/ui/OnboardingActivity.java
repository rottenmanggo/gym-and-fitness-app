package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gymbrut.app.adapter.OnboardingAdapter;
import com.gymbrut.app.databinding.ActivityOnboardingBinding;
import com.gymbrut.app.repository.MockRepository;
import com.gymbrut.app.repository.SessionManager;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);

        OnboardingAdapter adapter = new OnboardingAdapter(MockRepository.getInstance().getOnboardingItems());
        binding.viewPager.setAdapter(adapter);
        setupDots(0, adapter.getItemCount());

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                setupDots(position, adapter.getItemCount());
                binding.btnNext.setText(position == adapter.getItemCount() - 1 ? "Get Started" : "Next");
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int pos = binding.viewPager.getCurrentItem();
            if (pos < adapter.getItemCount() - 1) binding.viewPager.setCurrentItem(pos + 1);
            else finishOnboarding();
        });
        binding.tvSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        sessionManager.setOnboardingDone(true);
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    private void setupDots(int selected, int total) {
        binding.dotsLayout.removeAllViews();
        for (int i = 0; i < total; i++) {
            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(14f);
            dot.setTextColor(getColor(i == selected ? android.R.color.holo_green_light : android.R.color.darker_gray));
            dot.setPadding(8, 0, 8, 0);
            binding.dotsLayout.addView(dot);
        }
    }
}
