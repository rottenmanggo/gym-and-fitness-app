package com.gymbrut.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gymbrut.app.databinding.ItemLogBinding;
import com.gymbrut.app.model.ExerciseLog;
import java.util.List;

public class ExerciseLogAdapter extends RecyclerView.Adapter<ExerciseLogAdapter.ViewHolder> {
    private final List<ExerciseLog> logs;
    public ExerciseLogAdapter(List<ExerciseLog> logs) { this.logs = logs; }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseLog log = logs.get(position);
        holder.binding.tvExercise.setText(log.getExercise());
        holder.binding.tvSetsReps.setText(log.getSetsReps());
        holder.binding.tvWeight.setText(log.getWeight());
    }

    @Override public int getItemCount() { return logs.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLogBinding binding;
        ViewHolder(ItemLogBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
