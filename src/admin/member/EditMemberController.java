package admin.member;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditMemberController {

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
    @FXML
    private ComboBox<String> statusCombo;

    private static Member selectedMember;

    private final MemberService memberService = new MemberService();

    public static void setSelectedMember(Member member) {
        selectedMember = member;
    }

    @FXML
    public void initialize() {
<<<<<<< HEAD
        membershipCombo.setItems(memberService.getPackageNames());
        statusCombo.getItems().addAll("aktif", "pending", "expired");
=======
        membershipCombo.getItems().addAll("Harian", "Mingguan", "Bulanan", "Tahunan");
        statusCombo.getItems().addAll("Aktif", "Pending", "Nonaktif");
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e

        if (selectedMember != null) {
            nameField.setText(selectedMember.getName());
            emailField.setText(selectedMember.getEmail());
            phoneField.setText(selectedMember.getPhone());
<<<<<<< HEAD
            membershipCombo.setValue(selectedMember.getMembership());
            statusCombo.setValue(selectedMember.getStatus());

            if (selectedMember.getStartDate() != null && !selectedMember.getStartDate().isBlank()) {
                startDatePicker.setValue(LocalDate.parse(selectedMember.getStartDate()));
            } else {
                startDatePicker.setValue(LocalDate.now());
            }
=======

            membershipCombo.setValue(selectedMember.getMembership());
            statusCombo.setValue(selectedMember.getStatus());
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedMember == null) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak ada member yang dipilih.");
            return;
        }

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String membership = membershipCombo.getValue();
        String status = statusCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || membership == null || status == null
                || startDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi",
                    "Nama, email, no HP, paket, tanggal mulai, dan status wajib diisi.");
            return;
        }

        boolean success = memberService.updateMember(
                selectedMember,
                name,
                email,
                phone,
                password,
                membership,
                startDatePicker.getValue().toString(),
                status);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Member berhasil diperbarui.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Member gagal diperbarui. Email mungkin sudah digunakan.");
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