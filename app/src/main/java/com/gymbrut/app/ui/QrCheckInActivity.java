package com.gymbrut.app.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.gymbrut.app.databinding.ActivityQrCheckInBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrCheckInActivity extends AppCompatActivity {
    private ActivityQrCheckInBinding binding;
    private final ActivityResultLauncher<Intent> qrLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                if (scanResult != null && scanResult.getContents() != null) {
                    binding.tvCheckInStatus.setText("Check-in successful\n" +
"Scanned code: " + scanResult.getContents());
                } else {
                    binding.tvCheckInStatus.setText("Scan cancelled or invalid QR code.");
                }
            }
    );

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCheckInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnStartScan.setOnClickListener(v -> startQrScanner());
    }

    private void startQrScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan gym QR code");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        qrLauncher.launch(integrator.createScanIntent());
    }
}
