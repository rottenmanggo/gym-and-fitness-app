package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gymbrut.app.databinding.FragmentAccountBinding;
import com.gymbrut.app.model.Member;
import com.gymbrut.app.repository.MockRepository;
import com.gymbrut.app.repository.SessionManager;

public class AccountFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAccountBinding binding = FragmentAccountBinding.inflate(inflater, container, false);
        Member member = MockRepository.getInstance().getMember();
        binding.tvProfileInfo.setText("Name: " + member.getName() + "\n" +
"Email: " + member.getEmail() + "\n" +
"Member ID: " + member.getMemberId() + "\n" +
"Tier: " + member.getTier());
        binding.btnLogout.setOnClickListener(v -> {
            new SessionManager(requireContext()).logout();
            startActivity(new Intent(requireContext(), AuthActivity.class));
            requireActivity().finish();
        });
        return binding.getRoot();
    }
}
