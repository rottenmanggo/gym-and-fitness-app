package com.gymbrut.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gymbrut.app.databinding.ItemOnboardingBinding;
import com.gymbrut.app.model.OnboardingItem;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private final List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) { this.items = items; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.binding.imageView.setImageResource(item.getImageRes());
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvDescription.setText(item.getDescription());
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemOnboardingBinding binding;
        ViewHolder(ItemOnboardingBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
