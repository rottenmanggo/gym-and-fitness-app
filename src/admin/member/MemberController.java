package admin.member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

    @FXML
    private TableColumn<Member, Void> actionColumn;

    @FXML
    private ComboBox<String> statusFilterCombo;

    private final MemberService memberService = new MemberService();

    @FXML
    public void initialize() {
        setupTable();
        setupStatusFilter();
        loadMembers();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMembers());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("membership"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupStatusColumn();
        setupActionColumn();

        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void setupStatusFilter() {
        statusFilterCombo.getItems().setAll("Semua", "Aktif", "Pending", "Expired");
        statusFilterCombo.setValue("Semua");
    }

    private void setupStatusColumn() {
        statusColumn.setCellFactory(column -> new TableCell<Member, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(status);

                switch (status.toLowerCase()) {
                    case "aktif" -> badge.getStyleClass().add("status-active");
                    case "pending" -> badge.getStyleClass().add("status-pending");
                    case "expired" -> badge.getStyleClass().add("status-expired");
                    case "nonaktif" -> badge.getStyleClass().add("status-expired");
                    default -> badge.getStyleClass().add("status-default");
                }

                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<Member, Void>() {

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox container = new HBox(8, editButton, deleteButton);

            {
                editButton.getStyleClass().add("table-edit-btn");
                deleteButton.getStyleClass().add("table-delete-btn");
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                editButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    openEditPage(member);
                });

                deleteButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    deleteMember(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadMembers() {
        ObservableList<Member> members = memberService.getAllMembers();
        memberTable.setItems(members);
        totalDataLabel.setText(members.size() + " data");
    }

    private void filterMembers() {
        String keyword = searchField.getText().trim().toLowerCase();
        String statusFilter = statusFilterCombo.getValue();

        ObservableList<Member> filtered = FXCollections.observableArrayList();

        for (Member member : memberService.getAllMembers()) {
            boolean matchesKeyword =
                    member.getName().toLowerCase().contains(keyword)
                            || member.getEmail().toLowerCase().contains(keyword)
                            || member.getMembership().toLowerCase().contains(keyword)
                            || member.getStatus().toLowerCase().contains(keyword);

            boolean matchesStatus =
                    statusFilter == null
                            || statusFilter.equals("Semua")
                            || member.getStatus().equalsIgnoreCase(statusFilter);

            if (matchesKeyword && matchesStatus) {
                filtered.add(member);
            }
        }

        memberTable.setItems(filtered);
        totalDataLabel.setText(filtered.size() + " data");
    }

    private void openEditPage(Member member) {
        EditMemberController.setSelectedMember(member);

        SceneManager.changeScene(
                memberTable,
                "/admin/member/EditMember.fxml",
                "GYMBRUT - Edit Member",
                1280,
                760);
    }

    private void deleteMember(Member member) {
        boolean confirm = AlertHelper.showConfirm(
                "Hapus Member",
                "Yakin ingin menghapus member " + member.getName() + "?");

        if (!confirm) {
            return;
        }

        boolean success = memberService.deleteMember(member);

        if (!success) {
            AlertHelper.showWarning("Gagal", "Member gagal dihapus.");
            return;
        }

        loadMembers();
        AlertHelper.showInfo("Berhasil", "Member berhasil dihapus.");
    }

    @FXML
    private void handleFilterStatus() {
        filterMembers();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        statusFilterCombo.setValue("Semua");
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