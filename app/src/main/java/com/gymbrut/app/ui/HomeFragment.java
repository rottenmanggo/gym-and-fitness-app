package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gymbrut.app.databinding.FragmentHomeBinding;
import com.gymbrut.app.repository.MockRepository;
import com.gymbrut.app.utils.ChartUtils;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.tvTodayWorkout.setText("Upper Power • Bench Press 5x5, Pull-Ups 4x8, OHP 4x6");
        ChartUtils.bindLineChart(binding.lineChart, MockRepository.getInstance().getWeeklyProgress(), "Completion %");
        binding.btnCheckIn.setOnClickListener(v -> startActivity(new Intent(requireContext(), QrCheckInActivity.class)));
        return binding.getRoot();
    }
}
