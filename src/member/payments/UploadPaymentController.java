package member.payments;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UploadPaymentController {

    private File selectedFile;

    private FileChooser fileChooser = new FileChooser();

    private Connection connection = DatabaseConnection.getConnection(); // sesuaikan jika beda

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

            // 1. Nama file unik
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();

            // 2. Path tujuan
            Path targetPath = Paths.get("src/uploads/payments/" + fileName);

            // 3. Copy file
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Simpan ke database
            String query = "INSERT INTO payments (membership_id, amount, status, proof_image_path) VALUES (?, ?, 'pending', ?)";

            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, 1); // sementara (membership_id)
            ps.setDouble(2, 100000); // sementara (amount)
            ps.setString(3, targetPath.toString());

            ps.executeUpdate();

            System.out.println("Upload berhasil!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}