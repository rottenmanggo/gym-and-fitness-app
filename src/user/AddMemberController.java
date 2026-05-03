package user;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddMemberController {

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtMembership;
    @FXML private TextField txtJoinDate;
    @FXML private TextField txtStatus;

    private AdminMemberController parentController;
    private Member selectedMember;

    public void setParentController(AdminMemberController controller) {
        this.parentController = controller;
    }

    public void setEditData(Member member) {
        this.selectedMember = member;

        txtName.setText(member.getName());
        txtEmail.setText(member.getEmail());
        txtPhone.setText(member.getPhone());
        txtMembership.setText(member.getMembership());
        txtJoinDate.setText(member.getJoinDate());
        txtStatus.setText(member.getStatus());
    }

    @FXML
    private void handleSave() {

        if (selectedMember != null) {
            // EDIT
            selectedMember.setName(txtName.getText());
            selectedMember.setEmail(txtEmail.getText());
            selectedMember.setPhone(txtPhone.getText());
            selectedMember.setMembership(txtMembership.getText());
            selectedMember.setJoinDate(txtJoinDate.getText());
            selectedMember.setStatus(txtStatus.getText());
        } else {
            // TAMBAH
            Member member = new Member(
                    MemberService.generateId(),
                    txtName.getText(),
                    txtEmail.getText(),
                    txtPhone.getText(),
                    txtMembership.getText(),
                    txtJoinDate.getText(),
                    txtStatus.getText()
            );

            MemberService.addMember(member);
        }

        parentController.showTable();
    }

    @FXML
    private void handleBack() {
        parentController.showTable();
    }
}