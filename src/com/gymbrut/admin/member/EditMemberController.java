package com.gymbrut.admin.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import com.gymbrut.shared.AlertHelper;
import com.gymbrut.shared.SceneManager;

public class EditMemberController {

    private static Member selectedMember;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> membershipCombo;

    @FXML
    private ComboBox<String> statusCombo;

    private final MemberService memberService = new MemberService();

    public static void setSelectedMember(Member member) {
        selectedMember = member;
    }

    @FXML
    public void initialize() {
        membershipCombo.getItems().addAll("Harian", "Mingguan", "Bulanan", "Tahunan");
        statusCombo.getItems().addAll("Aktif", "Pending", "Nonaktif");
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedMember == null) {
            AlertHelper.showWarning("Data Kosong", "Tidak ada member yang dipilih.");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String membership = membershipCombo.getValue();
        String status = statusCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || membership == null || status == null) {
            AlertHelper.showWarning("Validasi", "Semua field wajib diisi.");
            return;
        }

        if (name.length() < 3) {
            AlertHelper.showWarning("Validasi", "Nama minimal 3 karakter.");
            return;
        }

        if (!isValidEmail(email)) {
            AlertHelper.showWarning("Validasi", "Format email tidak valid.");
            return;
        }

        if (!isValidPhone(phone)) {
            AlertHelper.showWarning("Validasi", "No HP harus angka dan terdiri dari 10-15 digit.");
            return;
        }

        boolean success = memberService.updateMember(selectedMember, name, email, phone, membership, status);

        if (!success) {
            AlertHelper.showWarning("Gagal", "Data member gagal diperbarui. Email mungkin sudah digunakan.");
            return;
        }

        AlertHelper.showInfo("Berhasil", "Data member berhasil diperbarui.");

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/com/gymbrut/resources/fxml/admin/member/Member.fxml",
                "GYMBRUT - Data Member",
                1280,
                760);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/com/gymbrut/resources/fxml/admin/member/Member.fxml",
                "GYMBRUT - Data Member",
                1280,
                760);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10,15}");
    }

}