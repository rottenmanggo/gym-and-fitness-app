package admin.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import shared.AlertHelper;
import shared.SceneManager;

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
        membershipCombo.getItems().addAll("Bronze", "Silver", "Gold", "Platinum");
        statusCombo.getItems().addAll("Aktif", "Pending", "Nonaktif");

        if (selectedMember != null) {
            nameField.setText(selectedMember.getName());
            emailField.setText(selectedMember.getEmail());
            phoneField.setText(selectedMember.getPhone());
            membershipCombo.setValue(selectedMember.getMembership());
            statusCombo.setValue(selectedMember.getStatus());
        }
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

        memberService.updateMember(selectedMember, name, email, phone, membership, status);

        AlertHelper.showInfo("Berhasil", "Data member berhasil diperbarui.");

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/member/Member.fxml",
                "GYMBRUT - Data Member",
                1280,
                760);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/member/Member.fxml",
                "GYMBRUT - Data Member",
                1280,
                760);
    }
}