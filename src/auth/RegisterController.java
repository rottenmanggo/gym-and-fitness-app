package auth;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import shared.SceneManager;

public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label alertLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Nama, email, dan password wajib diisi!", "danger");
            return;
        }

        if (authService.emailExists(email)) {
            showAlert("Email sudah digunakan!", "danger");
            return;
        }

        boolean success = authService.register(name, email, password, phone);

        if (success) {
            showAlert("Register berhasil! Silakan login.", "success");
            SceneManager.changeScene(emailField, "/auth/Login.fxml", "Login", 1100, 720);
        } else {
            showAlert("Register gagal!", "danger");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            System.out.println("Go to Login clicked");
            Node nodeToUse = emailField != null ? emailField : alertLabel;
            if (nodeToUse == null) {
                System.err.println("No valid node found for scene change");
                return;
            }
            SceneManager.changeScene(
                    nodeToUse,
                    "/auth/Login.fxml",
                    "Login",
                    1100,
                    720);
            System.out.println("Login scene change initiated");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Gagal membuka halaman login: " + e.getMessage(), "danger");
        }
    }

    private void showAlert(String message, String type) {
        alertLabel.setText(message);
        alertLabel.setVisible(true);
        alertLabel.setManaged(true);
        alertLabel.getStyleClass().removeAll("auth-alert-success", "auth-alert-danger");
        alertLabel.getStyleClass().add("success".equals(type) ? "auth-alert-success" : "auth-alert-danger");
    }
}