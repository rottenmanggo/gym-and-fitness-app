package auth;

import javafx.fxml.FXML;
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

        // ⚡ set session user
        Session.setUser(user);

<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        // ⚡ redirect sesuai role
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        // if (user.isAdmin()) {
        //     SceneManager.changeScene(emailField, "/admin/dashboard/Dashboard.fxml", "Dashboard Admin", 1280, 760);
        // } else {
        //     SceneManager.changeScene(emailField, "/member/dashboard/MemberDashboard.fxml", "Dashboard Member", 1280,
        //             760);
        // }ini yg asli, dibawah ini untuk testing punya ari, jangan di push ya yang bawah ini

<<<<<<< HEAD
if (user.getRole().equalsIgnoreCase("admin")) {
    SceneManager.changeScene(
            emailField,
            "/admin/dashboard/Dashboard.fxml",
            "GYMBRUT - Dashboard Admin",
            1280,
            760);
} else if (user.getRole().equalsIgnoreCase("member")) {
    SceneManager.changeScene(
            emailField,
            "/member/dashboard/MemberDashboard.fxml",
            "GYMBRUT - Dashboard Member",
            1280,
            760);
} else {
    showAlert("Role akun tidak dikenali.", "danger");
}
=======
>>>>>>> Stashed changes
        if (user.isAdmin()) {
            SceneManager.changeScene(
                    emailField,
                    "/admin/dashboard/Dashboard.fxml",
                    "Dashboard Admin",
                    1280,
                    760);
        } else if (user.isMember()) {
            SceneManager.changeScene(
                    emailField,
                    "/member/dashboard/MemberDashboard.fxml",
                    "Dashboard Member",
                    1280,
                    760);
        } else {
            showAlert("Role user tidak dikenali", "danger");
        }
    }

    @FXML
    private void handleRegisterLink() {
        SceneManager.changeScene(
                emailField,
                "/auth/Register.fxml",
                "Register",
                1100,
                720);
    }

    private void showAlert(String message, String type) {
        if (alertLabel == null)
            return;

        alertLabel.setText(message);
        alertLabel.setVisible(true);
        alertLabel.setManaged(true);

        alertLabel.getStyleClass().removeAll("auth-alert-success", "auth-alert-danger");
        alertLabel.getStyleClass().add("success".equals(type) ? "auth-alert-success" : "auth-alert-danger");
    }
}