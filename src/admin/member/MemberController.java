package admin.member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class MemberController {

    @FXML
    private StackPane contentPane;
    @FXML
    private TableView<Member> tableMembers;
    @FXML
    private TableColumn<Member, String> colName;
    @FXML
    private TableColumn<Member, String> colEmail;
    @FXML
    private TableColumn<Member, String> colPhone;
    @FXML
    private TableColumn<Member, String> colMembership;
    @FXML
    private TableColumn<Member, String> colJoinDate;
    @FXML
    private TableColumn<Member, String> colStatus;
    @FXML
    private TableColumn<Member, Void> colAction;
    @FXML
    private Label lblHeaderTitle;
    @FXML
    private Label lblHeaderDesc;

    @FXML
    private Button btnAddMember;

    private ObservableList<Member> memberList;

    @FXML
    public void initialize() {

        memberList = FXCollections.observableArrayList(MemberService.getAllMembers());

        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colEmail.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        colMembership.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMembership()));
        colJoinDate.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getJoinDate()));
        colStatus.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Hapus");

            {
                btnEdit.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    openEditForm(member);
                });

                btnDelete.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    MemberService.deleteMember(member);
                    memberList.remove(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEdit, btnDelete));
            }
        });

        tableMembers.setItems(memberList);

        // 🔥 INI KUNCI FIX
        contentPane.getChildren().setAll(tableMembers);
    }

    // 🔥 TAMBAH
    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/AddMember.fxml"));
            Parent node = loader.load();

            AddMemberController controller = loader.getController();
            controller.setParentController(this);

            lblHeaderTitle.setText("Tambah Member");
            lblHeaderDesc.setText("Isi data member baru.");

            btnAddMember.setVisible(false);

            contentPane.getChildren().setAll(node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 EDIT
    private void openEditForm(Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/EditMember.fxml"));
            Parent node = loader.load();

            AddMemberController controller = loader.getController();
            controller.setParentController(this);
            controller.setEditData(member);

            lblHeaderTitle.setText("Edit Member");
            lblHeaderDesc.setText("Ubah data member yang sudah ada.");

            btnAddMember.setVisible(false);

            contentPane.getChildren().setAll(node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 BALIK KE TABEL
    public void showTable() {
        contentPane.getChildren().setAll(tableMembers);
        memberList.setAll(MemberService.getAllMembers());

        btnAddMember.setVisible(true);

        lblHeaderTitle.setText("Data Member");
        lblHeaderDesc.setText("Daftar member aktif, pending, dan nonaktif.");
    }
}