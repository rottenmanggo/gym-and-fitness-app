package com.gymbrut.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gymbrut.app.databinding.ItemTransactionBinding;
import com.gymbrut.app.model.PaymentTransaction;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final List<PaymentTransaction> transactions;
    public TransactionAdapter(List<PaymentTransaction> transactions) { this.transactions = transactions; }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentTransaction tx = transactions.get(position);
        holder.binding.tvTitle.setText(tx.getTitle() + " - " + tx.getAmount());
        holder.binding.tvMeta.setText(tx.getStatus());
    }

    @Override public int getItemCount() { return transactions.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTransactionBinding binding;
        ViewHolder(ItemTransactionBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
