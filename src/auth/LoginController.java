package auth;

import includes.AuthAdmin;
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

    // @FXML
    // private void handleLogin() {
    //     String email = emailField.getText().trim();
    //     String password = passwordField.getText().trim();

    //     if (email.isEmpty() || password.isEmpty()) {
    //         showAlert("Email dan password wajib diisi.", "danger");
    //         return;
    //     }

    //     User user = authService.login(email, password);

    //     if (user == null) {
    //         showAlert("Email atau password salah.", "danger");
    //         return;
    //     }

    //     Session.setUser(user);

    //     if (!AuthAdmin.check()) {
    //         showAlert("Akun ini bukan admin.", "danger");
    //         return;
    //     }

    //     SceneManager.changeScene(
    //             emailField,
    //             "/admin/dashboard/Dashboard.fxml",
    //             "GYMBRUT - Dashboard Admin",
    //             1280,
    //             760);
    // }

    @FXML
private void handleLogin() {
    String email = emailField.getText().trim();
    String password = passwordField.getText().trim();

<<<<<<< HEAD
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Email dan password wajib diisi", "danger");
            return;
        }

        User user = authService.login(email, password);

        if (user == null) {
            showAlert("Gagal login: Email atau password salah", "danger");
            return;
        }

        Session.setUser(user);

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
        if (user.isAdmin()) {
            SceneManager.changeScene(emailField, "/admin/dashboard/Dashboard.fxml", "Dashboard Admin", 1280, 760);
        } else {
            SceneManager.changeScene(emailField, "/member/dashboard/MemberDashboard.fxml", "Dashboard Member", 1280,
                    760);
        }
>>>>>>> 7c2aef1530dd76ffab9d85aa76448403dd75423b
=======
    if (email.isEmpty() || password.isEmpty()) {
        showAlert("Email dan password wajib diisi.", "danger");
        return;
>>>>>>> 670e3cbdbfb48d9e3ab03092ead00a83b4c875e1
    }

    User user = authService.login(email, password);

    if (user == null) {
        showAlert("Email atau password salah.", "danger");
        return;
    }

    Session.setUser(user);

    // 🔥 CEK ROLE
    if (AuthAdmin.check()) {
        // ADMIN
        SceneManager.changeScene(
                emailField,
                "/admin/dashboard/Dashboard.fxml",
                "GYMBRUT - Dashboard Admin",
                1280,
                760);
    } else {
        // MEMBER
        SceneManager.changeScene(
            emailField,
            "/member/payments/MemberPayments.fxml",
            "GYMBRUT - Member Payments",
            800,
            600);
    }
}

    @FXML
    private void handleRegisterLink() {
        showAlert("Halaman register belum dibuat.", "success");
    }

    private void showAlert(String message, String type) {
        alertLabel.setText(message);
        alertLabel.setVisible(true);
        alertLabel.setManaged(true);

        alertLabel.getStyleClass().removeAll("auth-alert-danger", "auth-alert-success");

        if (type.equals("success")) {
            alertLabel.getStyleClass().add("auth-alert-success");
        } else {
            alertLabel.getStyleClass().add("auth-alert-danger");
        }
    }
}