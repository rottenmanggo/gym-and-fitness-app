package admin.membership;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MembershipController {

    @FXML
    private TextField searchField;
    @FXML
    private Label totalDataLabel;
    @FXML
    private GridPane cardGrid;

    private final MembershipService service = new MembershipService();
    private final ObservableList<Membership> membershipList = FXCollections.observableArrayList();
    private FilteredList<Membership> filteredList;

    @FXML
    public void initialize() {
        setupGrid();
        loadFromDatabase();

        filteredList = new FilteredList<>(membershipList, membership -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });

        renderCards();
    }

    private void setupGrid() {
        cardGrid.getColumnConstraints().clear();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        cardGrid.getColumnConstraints().addAll(col1, col2);
    }

    private void loadFromDatabase() {
        membershipList.clear();

        List<Membership> data = service.getAll();
        membershipList.addAll(data);

        updateTotalLabel(membershipList.size());
    }

    public void refreshData() {
        loadFromDatabase();

        filteredList = new FilteredList<>(membershipList, membership -> true);

        if (searchField != null) {
            searchField.clear();
        }

        renderCards();
    }

    private void handleSearch(String keyword) {
        String search = keyword == null ? "" : keyword.toLowerCase().trim();

        filteredList.setPredicate(membership -> {
            if (search.isEmpty()) {
                return true;
            }

            return safe(membership.getPackageName()).toLowerCase().contains(search)
                    || safe(membership.getDescription()).toLowerCase().contains(search)
                    || String.valueOf(membership.getDurationDays()).contains(search)
                    || membership.getPriceFormatted().toLowerCase().contains(search);
        });

        renderCards();
    }

    private void renderCards() {
        cardGrid.getChildren().clear();

        if (filteredList == null || filteredList.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(Pos.CENTER_LEFT);
            emptyBox.getStyleClass().add("empty-card");

            Label title = new Label("Belum ada paket membership.");
            title.getStyleClass().add("empty-title");

            Label subtitle = new Label("Tambahkan paket membership baru agar bisa dipilih oleh member.");
            subtitle.getStyleClass().add("empty-subtitle");

            emptyBox.getChildren().addAll(title, subtitle);

            cardGrid.add(emptyBox, 0, 0, 2, 1);
            updateTotalLabel(0);
            return;
        }

        int col = 0;
        int row = 0;

        for (Membership membership : filteredList) {
            VBox card = buildCard(membership);

            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);

            cardGrid.add(card, col, row);

            col++;

            if (col > 1) {
                col = 0;
                row++;
            }
        }

        updateTotalLabel(filteredList.size());
    }

    private VBox buildCard(Membership membership) {
        VBox card = new VBox(16);
        card.getStyleClass().add("membership-card");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label title = new Label(membership.getPackageName());
        title.getStyleClass().add("card-title");

        Label meta = new Label(membership.getDurationDays() + " Hari • " + membership.getPriceFormatted());
        meta.getStyleClass().add("card-meta");

        titleBox.getChildren().addAll(title, meta);

        Label badge = new Label("Active");
        badge.getStyleClass().addAll("badge-soft", "badge-active");

        header.getChildren().addAll(titleBox, badge);

        VBox benefitBox = new VBox(6);
        benefitBox.getStyleClass().add("card-list");

        Label benefitTitle = new Label("Benefit Paket");
        benefitTitle.getStyleClass().add("list-row-title");

        Label benefitText = new Label(membership.getDescriptionText());
        benefitText.setWrapText(true);
        benefitText.getStyleClass().add("list-row-subtitle");

        benefitBox.getChildren().addAll(benefitTitle, benefitText);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("btn-outline-soft");
        editButton.setOnAction(event -> openEditDialog(membership));

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("btn-outline-soft");
        deleteButton.setOnAction(event -> handleDelete(membership));

        actionBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(header, benefitBox, actionBox);

        return card;
    }

    @FXML
    private void handleTambahPaket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/membership/AddMembership.fxml"));
            Parent root = loader.load();

            AddMembershipController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Paket Membership");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form tambah paket tidak bisa dibuka.");
        }
    }

    private void openEditDialog(Membership membership) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/membership/EditMembership.fxml"));
            Parent root = loader.load();

            EditMembershipController controller = loader.getController();
            controller.setData(membership, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Paket Membership");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form edit paket tidak bisa dibuka.");
        }
    }

    private void handleDelete(Membership membership) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Paket");
        confirm.setHeaderText("Hapus paket \"" + membership.getPackageName() + "\"?");
        confirm.setContentText("Paket hanya bisa dihapus kalau belum dipakai oleh data membership member.");

        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                boolean success = service.delete(membership.getPackageId());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Paket berhasil dihapus.");
                    refreshData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Paket gagal dihapus karena masih dipakai oleh member.");
                }
            }
        });
    }

    private void updateTotalLabel(int total) {
        if (totalDataLabel != null) {
            totalDataLabel.setText(total + " paket");
        }
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
}