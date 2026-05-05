package com.gymbrut.member.payments;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.gymbrut.config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UploadPaymentController {

    private File selectedFile;

    private FileChooser fileChooser = new FileChooser();

    // sesuaikan jika beda

    // =========================
    // PILIH FILE
    // =========================
    @FXML
    private void handleChooseFile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("File dipilih: " + selectedFile.getName());
        }
    }

    // =========================
    // UPLOAD FILE
    // =========================
    @FXML
    private void handleUpload(ActionEvent event) {
        try {
            if (selectedFile == null) {
                System.out.println("Pilih file dulu!");
                return;
            }

            // =========================
            // 1. SIMPAN FILE DULU (PASTI JALAN)
            // =========================
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path targetPath = Paths.get("src/uploads/payments/" + fileName);

            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File berhasil disimpan!");

            // =========================
            // 2. BARU COBA DATABASE
            // =========================
            Connection connection = Database.getConnection();

            if (connection == null) {
                System.out.println("DB gagal, tapi file tetap tersimpan.");
                return;
            }

            String query = "INSERT INTO payments (membership_id, amount, payment_date, payment_method, status, proof_image_path) VALUES (?, ?, NOW(), 'transfer', 'pending', ?)";

            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, 1);
            ps.setDouble(2, 100000);
            ps.setString(3, targetPath.toString());

            int result = ps.executeUpdate();
            System.out.println("Rows inserted: " + result);

        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }
}