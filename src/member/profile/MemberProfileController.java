package member.profile;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MemberProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        nameField.setText("Zaif");
        emailField.setText("zaif@email.com");
    }

    @FXML
    private void handleUpdate() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Data tidak boleh kosong!");
            return;
        }

        showAlert("Profile berhasil diupdate!");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
