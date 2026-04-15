package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gymbrut.app.adapter.TransactionAdapter;
import com.gymbrut.app.databinding.FragmentMembershipBinding;
import com.gymbrut.app.model.Member;
import com.gymbrut.app.repository.MockRepository;

public class MembershipFragment extends Fragment {
    private FragmentMembershipBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMembershipBinding.inflate(inflater, container, false);
        Member member = MockRepository.getInstance().getMember();
        binding.tvTier.setText(member.getTier());
        binding.tvMemberMeta.setText("Member ID: " + member.getMemberId() + "\n" +
"Expires on: " + member.getExpiryDate());
        binding.recyclerTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTransactions.setAdapter(new TransactionAdapter(MockRepository.getInstance().getTransactions()));
        binding.btnRenew.setOnClickListener(v -> startActivity(new Intent(requireContext(), PaymentFlowActivity.class)));
        return binding.getRoot();
    }
}
