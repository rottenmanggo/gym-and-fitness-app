package auth;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import shared.SceneManager;
import shared.Session;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label alertLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Email dan password wajib diisi", "danger");
            return;
        }

        // Login ke database
        User user = authService.login(email, password);

        if (user == null) {
            showAlert("Gagal login: Email atau password salah", "danger");
            return;
        }

        System.out.println("Login berhasil - User: " + user.getName() + ", Role: " + user.getRole());

        // Set session user
        Session.setUser(user);

        // Redirect ke LayoutTop (single-shell).
        // LayoutTopController akan load sidebar & konten sesuai role dari Session.
        if (user.isAdmin() || user.isMember()) {
            System.out.println("Redirecting to LayoutTop (role: " + user.getRole() + ")");
            SceneManager.changeScene(
                    emailField,
                    "/includes/LayoutTop.fxml",
                    "GYMBRUT",
                    1280,
                    760);
        } else {
            System.out.println("Role tidak dikenali: " + user.getRole());
            showAlert("Role user tidak dikenali", "danger");
        }
    }

    @FXML
    private void focusPassword() {
        passwordField.requestFocus();
    }

    @FXML
    private void handleRegisterLink() {
        try {
            System.out.println("Register link clicked");
            SceneManager.changeScene(
                    emailField,
                    "/auth/Register.fxml",
                    "Register",
                    1100,
                    720);
            System.out.println("Register scene change initiated");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Gagal membuka halaman register: " + e.getMessage(), "danger");
        }
    }

    private void showAlert(String message, String type) {

        alertLabel.setText(message);

        alertLabel.setVisible(true);
        alertLabel.setManaged(true);

        // Tengah
        alertLabel.setMaxWidth(Double.MAX_VALUE);
        alertLabel.setStyle("-fx-alignment: center; -fx-text-alignment: center;");

        alertLabel.getStyleClass().removeAll(
                "auth-alert-success",
                "auth-alert-danger");

        alertLabel.getStyleClass().add(
                "success".equals(type)
                        ? "auth-alert-success"
                        : "auth-alert-danger");
    }
}