package admin.member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import shared.AlertHelper;
import shared.SceneManager;
import shared.Session;

public class MemberController {

    @FXML
    private TextField searchField;

    @FXML
    private Label totalDataLabel;

    @FXML
    private TableView<Member> memberTable;

    @FXML
    private TableColumn<Member, Integer> idColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> membershipColumn;

    @FXML
    private TableColumn<Member, String> statusColumn;

    private final MemberService memberService = new MemberService();

    @FXML
public void initialize() {
    setupTable();
    loadMembers();

    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        handleSearch();
    });
}

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("membership"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadMembers() {
        memberTable.setItems(memberService.getAllMembers());
        totalDataLabel.setText(memberTable.getItems().size() + " data");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadMembers();
            return;
        }

        ObservableList<Member> filtered = FXCollections.observableArrayList();

        for (Member member : memberService.getAllMembers()) {
            boolean match = member.getName().toLowerCase().contains(keyword)
                    || member.getEmail().toLowerCase().contains(keyword)
                    || member.getMembership().toLowerCase().contains(keyword)
                    || member.getStatus().toLowerCase().contains(keyword);

            if (match) {
                filtered.add(member);
            }
        }

        memberTable.setItems(filtered);
        totalDataLabel.setText(filtered.size() + " data");
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        loadMembers();
    }

    @FXML
    private void openAddMember(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/member/AddMember.fxml",
                "GYMBRUT - Tambah Member",
                1280,
                760);
    }

    @FXML
    private void openEditMember(ActionEvent event) {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            AlertHelper.showWarning("Pilih Member", "Pilih member yang ingin diedit terlebih dahulu.");
            return;
        }

        EditMemberController.setSelectedMember(selectedMember);

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/member/EditMember.fxml",
                "GYMBRUT - Edit Member",
                1280,
                760);
    }

    @FXML
    private void handleDeleteMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            AlertHelper.showWarning("Pilih Member", "Pilih member yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirm(
                "Hapus Member",
                "Yakin ingin menghapus member " + selectedMember.getName() + "?");

        if (confirm) {
            boolean success = memberService.deleteMember(selectedMember);

            if (!success) {
                AlertHelper.showWarning("Gagal", "Member gagal dihapus.");
                return;
            }

            loadMembers();
            AlertHelper.showInfo("Berhasil", "Member berhasil dihapus.");
        }
    }

    @FXML
    private void openDashboard(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/dashboard/Dashboard.fxml",
                "GYMBRUT - Dashboard Admin",
                1280,
                760);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/auth/Login.fxml",
                "GYMBRUT - Login",
                1100,
                720);
    }
}