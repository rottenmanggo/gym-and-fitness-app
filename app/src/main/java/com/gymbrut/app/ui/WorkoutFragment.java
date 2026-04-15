package com.gymbrut.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gymbrut.app.adapter.WorkoutAdapter;
import com.gymbrut.app.databinding.FragmentWorkoutBinding;
import com.gymbrut.app.repository.MockRepository;

public class WorkoutFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentWorkoutBinding binding = FragmentWorkoutBinding.inflate(inflater, container, false);
        binding.recyclerWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerWorkouts.setAdapter(new WorkoutAdapter(MockRepository.getInstance().getWorkoutModules()));
        return binding.getRoot();
    }
}
