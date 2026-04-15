package com.gymbrut.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gymbrut.app.adapter.ExerciseLogAdapter;
import com.gymbrut.app.databinding.FragmentProgressBinding;
import com.gymbrut.app.model.ExerciseLog;
import com.gymbrut.app.repository.MockRepository;
import com.gymbrut.app.utils.ChartUtils;
import java.util.List;

public class ProgressFragment extends Fragment {
    private FragmentProgressBinding binding;
    private List<ExerciseLog> logs;
    private ExerciseLogAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        logs = MockRepository.getInstance().getExerciseLogs();
        adapter = new ExerciseLogAdapter(logs);
        binding.recyclerLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLogs.setAdapter(adapter);
        ChartUtils.bindLineChart(binding.weightChart, MockRepository.getInstance().getWeightTrend(), "Body Weight");
        ChartUtils.bindCompletionChart(binding.completionChart, 72f, 28f);
        binding.btnAddLog.setOnClickListener(v -> showAddLogDialog());
        return binding.getRoot();
    }

    private void showAddLogDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = 32;
        layout.setPadding(pad, pad, pad, pad);
        EditText exercise = new EditText(requireContext()); exercise.setHint("Exercise");
        EditText sets = new EditText(requireContext()); sets.setHint("Sets x Reps");
        EditText weight = new EditText(requireContext()); weight.setHint("Weight");
        layout.addView(exercise); layout.addView(sets); layout.addView(weight);
        new AlertDialog.Builder(requireContext())
            .setTitle("Add Exercise Log")
            .setView(layout)
            .setPositiveButton("Save", (d, w) -> {
                logs.add(0, new ExerciseLog(exercise.getText().toString(), sets.getText().toString(), weight.getText().toString()));
                adapter.notifyItemInserted(0);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
