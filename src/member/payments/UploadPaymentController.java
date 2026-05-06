package member.payments;

import config.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UploadPaymentController {

    private File selectedFile;
    private FileChooser fileChooser = new FileChooser();

    @FXML
    private void handleChooseFile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("File dipilih: " + selectedFile.getName());
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        try {
            if (selectedFile == null) {
                System.out.println("Pilih file dulu!");
                return;
            }

            // simpan file
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path targetPath = Paths.get("src/uploads/payments/" + fileName);

            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File berhasil disimpan!");

            // simpan ke DB
            Connection conn = Database.getConnection();

            String query = """
                INSERT INTO payments 
                (membership_id, amount, payment_date, payment_method, status, proof_image_path)
                VALUES (?, ?, NOW(), 'transfer', 'pending', ?)
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, 1); // sementara
            ps.setDouble(2, 100000); // sementara
            ps.setString(3, targetPath.toString());

            int result = ps.executeUpdate();
            System.out.println("Rows inserted: " + result);

        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }
}