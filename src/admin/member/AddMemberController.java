package admin.member;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddMemberController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> membershipCombo;
    @FXML
    private DatePicker startDatePicker;

    private final MemberService memberService = new MemberService();

    @FXML
    public void initialize() {
        membershipCombo.setItems(memberService.getPackageNames());
        startDatePicker.setValue(LocalDate.now());

        if (!membershipCombo.getItems().isEmpty()) {
            membershipCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String membership = membershipCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || membership == null
                || startDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        boolean success = memberService.addMember(
                name,
                email,
                phone,
                password,
                membership,
                startDatePicker.getValue().toString());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Member berhasil ditambahkan.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Member gagal ditambahkan. Email mungkin sudah digunakan.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}