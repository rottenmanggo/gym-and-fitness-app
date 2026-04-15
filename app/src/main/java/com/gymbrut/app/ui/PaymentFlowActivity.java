package com.gymbrut.app.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.gymbrut.app.databinding.ActivityPaymentFlowBinding;

public class PaymentFlowActivity extends AppCompatActivity {
    private ActivityPaymentFlowBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] packages = {"Monthly - Rp 850.000", "Quarterly - Rp 2.300.000", "Yearly - Rp 8.000.000"};
        String[] methods = {"Bank Transfer", "Credit Card", "E-Wallet"};
        binding.spinnerPackage.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, packages));
        binding.spinnerMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods));
        updateSummary();
        binding.spinnerPackage.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateSummary));
        binding.spinnerMethod.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateSummary));
        binding.btnSubmitPayment.setOnClickListener(v -> {
            binding.tvPaymentStatus.setText("Awaiting Admin Verification\n" +
"Reference: PAY-GB-2024-1001");
            Toast.makeText(this, "Payment proof submitted", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSummary() {
        binding.tvOrderSummary.setText("Selected package: " + binding.spinnerPackage.getSelectedItem() + "\n" +
"Payment method: " + binding.spinnerMethod.getSelectedItem() + "\n" +
"Gateway simulation fee: Rp 0\n" +
"Total: included in selected package");
    }
}
