package auth;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import shared.SceneManager;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Alert;

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
    private ComboBox<String> genderBox;
    @FXML
    private TextField ageField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private TextField targetField;

    @FXML
    private Label alertLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {

        genderBox.getItems().addAll(
                "Laki-laki",
                "Perempuan");
    }

    @FXML
    private void focusEmail() {
        emailField.requestFocus();
    }

    @FXML
    private void focusPassword() {
        passwordField.requestFocus();
    }

    @FXML
    private void focusPhone() {
        phoneField.requestFocus();
    }

    @FXML
    private void focusGender() {
        genderBox.requestFocus();
        genderBox.show();
    }

    @FXML
    private void focusAge() {
        ageField.requestFocus();
    }

    @FXML
    private void focusHeight() {
        heightField.requestFocus();
    }

    @FXML
    private void focusWeight() {
        weightField.requestFocus();
    }

    @FXML
    private void focusTarget() {
        targetField.requestFocus();
    }

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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Registrasi Berhasil");
            alert.setHeaderText(null);

            alert.setContentText(
                    "Akun berhasil dibuat.\nSilakan login."
            );

            alert.showAndWait();

            SceneManager.changeScene(
                    emailField,
                    "/auth/Login.fxml",
                    "Login",
                    1100,
                    720
            );

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Registrasi Gagal");
            alert.setHeaderText(null);

            alert.setContentText(
                    "Registrasi gagal."
            );

            alert.showAndWait();
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
