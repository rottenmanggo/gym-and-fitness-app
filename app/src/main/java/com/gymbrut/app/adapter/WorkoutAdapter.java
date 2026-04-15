package com.gymbrut.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gymbrut.app.databinding.ItemWorkoutBinding;
import com.gymbrut.app.model.WorkoutModule;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    private final List<WorkoutModule> modules;
    public WorkoutAdapter(List<WorkoutModule> modules) { this.modules = modules; }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemWorkoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutModule module = modules.get(position);
        holder.binding.tvWorkoutName.setText(module.getName());
        holder.binding.tvWorkoutMeta.setText(module.getGoal() + " • " + module.getDuration());
        holder.binding.tvWorkoutSteps.setText(module.getSteps());
    }

    @Override public int getItemCount() { return modules.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemWorkoutBinding binding;
        ViewHolder(ItemWorkoutBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
