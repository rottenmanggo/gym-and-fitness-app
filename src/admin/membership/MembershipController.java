package admin.membership;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MembershipController implements Initializable {

    @FXML private GridPane  cardGrid;
    @FXML private TextField searchField;

    private final MembershipService service = new MembershipService();
    private final ObservableList<Membership> membershipList = FXCollections.observableArrayList();
    private FilteredList<Membership> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFromDatabase();
        filteredList = new FilteredList<>(membershipList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(m -> {
                if (newVal == null || newVal.isBlank()) return true;
                String lower = newVal.toLowerCase();
                return m.getPackageName().toLowerCase().contains(lower)
                    || m.getDescription().toLowerCase().contains(lower);
            });
            renderCards();
        });

        renderCards();
    }

    private void loadFromDatabase() {
        membershipList.clear();
        List<Membership> data = service.getAll();
        membershipList.addAll(data);
    }

    public void refreshData() {
        loadFromDatabase();
        filteredList = new FilteredList<>(membershipList, p -> true);
        renderCards();
    }

    // ─── Render kartu ke GridPane ─────────────────────────────────
    private void renderCards() {
        cardGrid.getChildren().clear();
        int col = 0, row = 0;
        for (Membership m : filteredList) {
            cardGrid.add(buildCard(m), col, row);
            col++;
            if (col > 1) { col = 0; row++; }
        }
    }

    private VBox buildCard(Membership m) {
        VBox card = new VBox(12);
        card.getStyleClass().add("membership-card");
        card.setPadding(new Insets(20));

        // Header: judul + badge Active
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(m.getPackageName());
        title.getStyleClass().add("card-title");
        Label meta = new Label(m.getDurationDays() + " Hari  •  " + m.getPriceFormatted());
        meta.getStyleClass().add("card-meta");
        titleBox.getChildren().addAll(title, meta);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label badge = new Label("Active");
        badge.getStyleClass().addAll("status-badge", "status-aktif");
        header.getChildren().addAll(titleBox, badge);

        // Benefit / Deskripsi
        Label benefitTitle = new Label("Benefit Paket");
        benefitTitle.getStyleClass().add("card-benefit-title");
        Text benefitText = new Text(m.getDescription() != null ? m.getDescription() : "-");
        benefitText.getStyleClass().add("card-benefit-text");
        benefitText.setWrappingWidth(320);

        // Tombol aksi
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnEdit = new Button("✏  Edit");
        btnEdit.getStyleClass().addAll("card-btn", "btn-edit");
        btnEdit.setOnAction(e -> openEditDialog(m));

        Button btnHapus = new Button("🗑  Hapus");
        btnHapus.getStyleClass().addAll("card-btn", "btn-hapus");
        btnHapus.setOnAction(e -> handleHapus(m));

        actions.getChildren().addAll(btnEdit, btnHapus);
        card.getChildren().addAll(header, new Separator(), benefitTitle, benefitText, actions);
        return card;
    }

    // ─── Tambah Paket ─────────────────────────────────────────────
    @FXML
    public void handleTambahPaket() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/admin/membership/AddMembership.fxml"));
            Parent root = loader.load();

            AddMembershipController ctrl = loader.getController();
            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Paket Membership");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal membuka form tambah paket.");
        }
    }

    // ─── Edit Paket ───────────────────────────────────────────────
    private void openEditDialog(Membership m) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/admin/membership/EditMembership.fxml"));
            Parent root = loader.load();

            EditMembershipController ctrl = loader.getController();
            ctrl.setData(m, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Paket Membership");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal membuka form edit paket.");
        }
    }

    // ─── Hapus Paket ──────────────────────────────────────────────
    private void handleHapus(Membership m) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Paket");
        confirm.setHeaderText("Hapus paket \"" + m.getPackageName() + "\"?");
        confirm.setContentText("Tindakan ini tidak dapat dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = service.delete(m.getPackageId());
                if (ok) {
                    refreshData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal menghapus paket. Mungkin masih dipakai oleh member.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}