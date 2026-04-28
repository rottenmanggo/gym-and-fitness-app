package auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import auth.User;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = AuthService.login(email, password);

        if (user == null) {
            messageLabel.setText("Email / Password salah!");
            return;
        }

        Stage stage = (Stage) emailField.getScene().getWindow();

        app.Main mainApp = new app.Main();
        mainApp.showDashboard(stage);
    }

    @FXML
    private void goToRegister() {
        try {
            var url = getClass().getResource("/auth/Register.fxml");

            if (url == null) {
                messageLabel.setText("Register.fxml tidak ditemukan!");
                return;
            }

            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(url);

            stage.setScene(new Scene(loader.load(), 1000, 650));

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}