package admin.member;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
=======
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
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e

public class MemberController {

    @FXML
    private Label statTotalMember;
    @FXML
    private Label statActiveMember;
    @FXML
    private Label statPendingMember;
    @FXML
    private Label statExpiredMember;
    @FXML
    private Label totalDataLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, String> nameColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;
    @FXML
    private TableColumn<Member, String> phoneColumn;
    @FXML
    private TableColumn<Member, String> membershipColumn;
    @FXML
    private TableColumn<Member, String> joinDateColumn;
    @FXML
    private TableColumn<Member, String> statusColumn;
    @FXML
    private TableColumn<Member, Void> actionColumn;

    @FXML
    private TableColumn<Member, Void> actionColumn;

    @FXML
    private ComboBox<String> statusFilterCombo;

    private final MemberService memberService = new MemberService();
    private ObservableList<Member> allMembers;

    @FXML
    public void initialize() {
        setupTable();
<<<<<<< HEAD

        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameColumn.setPrefWidth(180);
        emailColumn.setPrefWidth(220);
        phoneColumn.setPrefWidth(140);
        membershipColumn.setPrefWidth(130);
        joinDateColumn.setPrefWidth(130);
        statusColumn.setPrefWidth(120);
        actionColumn.setPrefWidth(160);
        loadMembers();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
=======
        setupStatusFilter();
        loadMembers();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMembers());
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getName())));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getEmail())));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getPhone())));
        membershipColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getMembership())));
        joinDateColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getJoinDate())));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getStatus())));

<<<<<<< HEAD
        statusColumn.setCellFactory(column -> new TableCell<>() {
=======
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
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null || status.isBlank()) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(status);

                switch (status.toLowerCase()) {
<<<<<<< HEAD
                    case "aktif" -> {
                        badge.setText("Aktif");
                        badge.getStyleClass().add("status-active");
                    }
                    case "pending" -> {
                        badge.setText("Pending");
                        badge.getStyleClass().add("status-pending");
                    }
                    case "expired" -> {
                        badge.setText("Expired");
                        badge.getStyleClass().add("status-expired");
                    }
                    default -> {
                        badge.setText(status);
                        badge.getStyleClass().add("status-default");
                    }
=======
                    case "aktif" -> badge.getStyleClass().add("status-active");
                    case "pending" -> badge.getStyleClass().add("status-pending");
                    case "expired" -> badge.getStyleClass().add("status-expired");
                    case "nonaktif" -> badge.getStyleClass().add("status-expired");
                    default -> badge.getStyleClass().add("status-default");
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
                }

                badge.setAlignment(Pos.CENTER);
                setText(null);
                setGraphic(badge);
            }
        });
    }

<<<<<<< HEAD
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox box = new HBox(8, editButton, deleteButton);
=======
    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<Member, Void>() {

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox container = new HBox(8, editButton, deleteButton);
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e

            {
                editButton.getStyleClass().add("table-edit-btn");
                deleteButton.getStyleClass().add("table-delete-btn");
<<<<<<< HEAD
                box.setAlignment(Pos.CENTER_LEFT);

                editButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    openEditMember(member);
=======
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                editButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    openEditPage(member);
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
                });

                deleteButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    deleteMember(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
<<<<<<< HEAD

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
=======
                setGraphic(empty ? null : container);
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
            }
        });
    }

    private void loadMembers() {
<<<<<<< HEAD
        allMembers = memberService.getAllMembers();
        memberTable.setItems(allMembers);
        totalDataLabel.setText(allMembers.size() + " data");
        updateStats(allMembers);
    }

    private void handleSearch() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            memberTable.setItems(allMembers);
            totalDataLabel.setText(allMembers.size() + " data");
            updateStats(allMembers);
            return;
        }

        ObservableList<Member> filtered = allMembers
                .filtered(member -> safe(member.getName()).toLowerCase().contains(keyword)
                        || safe(member.getEmail()).toLowerCase().contains(keyword)
                        || safe(member.getPhone()).toLowerCase().contains(keyword)
                        || safe(member.getMembership()).toLowerCase().contains(keyword)
                        || safe(member.getJoinDate()).toLowerCase().contains(keyword)
                        || safe(member.getStatus()).toLowerCase().contains(keyword));
=======
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
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e

        memberTable.setItems(filtered);
        totalDataLabel.setText(filtered.size() + " data");
        updateStats(filtered);
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
<<<<<<< HEAD
        memberTable.setItems(allMembers);
        totalDataLabel.setText(allMembers.size() + " data");
        updateStats(allMembers);
=======
        statusFilterCombo.setValue("Semua");
        loadMembers();
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
    }

    @FXML
    private void openAddMember() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMember.fxml"));
            Parent root = loader.load();

<<<<<<< HEAD
            Stage stage = new Stage();
            stage.setTitle("Tambah Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadMembers();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form tambah member tidak bisa dibuka.");
        }
    }

    private void openEditMember(Member member) {
        if (member == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Member", "Pilih member yang ingin diedit.");
            return;
        }

        try {
            EditMemberController.setSelectedMember(member);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditMember.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadMembers();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form edit member tidak bisa dibuka.");
        }
    }

    private void deleteMember(Member member) {
        if (member == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Member", "Pilih member yang ingin dihapus.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus member?");
        confirm.setContentText("Data member \"" + member.getName()
                + "\" akan dihapus beserta membership, payment, check-in, progress, dan notifikasi.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = memberService.deleteMember(member);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Member berhasil dihapus.");
                loadMembers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Member gagal dihapus.");
            }
        }
    }

    private void updateStats(ObservableList<Member> members) {
        int total = members.size();
        int aktif = 0;
        int pending = 0;
        int expired = 0;

        for (Member member : members) {
            String status = safe(member.getStatus()).toLowerCase();

            if (status.equals("aktif")) {
                aktif++;
            } else if (status.equals("pending")) {
                pending++;
            } else if (status.equals("expired")) {
                expired++;
            }
        }

        statTotalMember.setText(String.valueOf(total));
        statActiveMember.setText(String.valueOf(aktif));
        statPendingMember.setText(String.valueOf(pending));
        statExpiredMember.setText(String.valueOf(expired));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
=======
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
>>>>>>> 5d6f7a3684f291f5acfa9f359c01e0e5d1a53d2e
}