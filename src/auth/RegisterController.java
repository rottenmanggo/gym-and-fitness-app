package auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    // =========================
    // HANDLE REGISTER
    // =========================
    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Semua field wajib diisi!");
            return;
        }

        boolean success = AuthService.register(name, email, password);

        if (success) {
            messageLabel.setText("Register berhasil! Silakan login.");
        } else {
            messageLabel.setText("Email sudah digunakan!");
        }
    }

    // =========================
    // BALIK KE LOGIN
    // =========================
    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/auth/Login.fxml"));

            Scene scene = new Scene(loader.load(), 1000, 650);
            stage.setScene(scene);
            stage.setTitle("GYMBRUT - Login");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Gagal kembali ke login!");
        }
    }
}