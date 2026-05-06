package admin.workout;

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WorkoutController implements Initializable {

    @FXML
    private GridPane cardGrid;
    @FXML
    private TextField searchField;

    private final WorkoutService service = new WorkoutService();
    private final ObservableList<Workout> workoutList = FXCollections.observableArrayList();
    private FilteredList<Workout> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFromDatabase();
        filteredList = new FilteredList<>(workoutList, p -> true);

        searchField.textProperty().addListener((obs, o, newVal) -> {
            filteredList.setPredicate(w -> {
                if (newVal == null || newVal.isBlank()) {
                    return true;
                }
                String lower = newVal.toLowerCase();
                return w.getTitle().toLowerCase().contains(lower)
                        || w.getCategory().toLowerCase().contains(lower)
                        || (w.getEquipment() != null && w.getEquipment().toLowerCase().contains(lower));
            });
            renderCards();
        });

        renderCards();
    }

    private void loadFromDatabase() {
        workoutList.clear();
        workoutList.addAll(service.getAll());
    }

    public void refreshData() {
        loadFromDatabase();
        filteredList = new FilteredList<>(workoutList, p -> true);
        renderCards();
    }

    // ─── Render kartu ─────────────────────────────────────────────
    private void renderCards() {
        cardGrid.getChildren().clear();
        int col = 0, row = 0;
        for (Workout w : filteredList) {
            cardGrid.add(buildCard(w), col, row);
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
    }

    private VBox buildCard(Workout w) {
        VBox card = new VBox(10);
        card.getStyleClass().add("membership-card");
        card.setPadding(new Insets(20));

        // Header: judul + badge kategori
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(w.getTitle());
        title.getStyleClass().add("card-title");
        Label meta = new Label(w.getCategory() + "  •  " + w.getSets() + " Set  •  " + w.getReps() + " Reps");
        meta.getStyleClass().add("card-meta");
        titleBox.getChildren().addAll(title, meta);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label badge = new Label("Workout");
        badge.getStyleClass().addAll("status-badge", "badge-workout");
        header.getChildren().addAll(titleBox, badge);

        // Equipment
        Label eqTitle = new Label("Equipment");
        eqTitle.getStyleClass().add("card-benefit-title");
        Label eqText = new Label(w.getEquipment() != null ? w.getEquipment() : "-");
        eqText.getStyleClass().add("card-benefit-text");

        // Tutorial / Deskripsi (2 baris)
        Label tutTitle = new Label("Tutorial");
        tutTitle.getStyleClass().add("card-benefit-title");
        Label tutText = new Label(w.getDescription() != null ? w.getDescription() : "-");
        tutText.getStyleClass().add("card-benefit-text");
        tutText.setWrapText(true);
        tutText.setMaxWidth(320);

        // Tombol aksi
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnVideo = new Button("▶  Video");
        btnVideo.getStyleClass().addAll("card-btn", "btn-video");
        btnVideo.setOnAction(e -> openVideo(w));

        Button btnDetail = new Button("👁  Detail");
        btnDetail.getStyleClass().addAll("card-btn", "btn-detail");
        btnDetail.setOnAction(e -> openDetail(w));

        Button btnEdit = new Button("✏  Edit");
        btnEdit.getStyleClass().addAll("card-btn", "btn-edit");
        btnEdit.setOnAction(e -> openEdit(w));

        Button btnHapus = new Button("🗑  Hapus");
        btnHapus.getStyleClass().addAll("card-btn", "btn-hapus");
        btnHapus.setOnAction(e -> handleHapus(w));

        actions.getChildren().addAll(btnVideo, btnDetail, btnEdit, btnHapus);

        card.getChildren().addAll(header, eqTitle, eqText, tutTitle, tutText, actions);
        return card;
    }

    // ─── Tambah ───────────────────────────────────────────────────
    @FXML
    public void handleTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/admin/workout/AddWorkout.fxml"));
            Parent root = loader.load();

            AddWorkoutController ctrl = loader.getController();
            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Program Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal membuka form tambah workout.");
        }
    }

    // ─── Detail ───────────────────────────────────────────────────
    private void openDetail(Workout w) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/admin/workout/DetailWorkout.fxml"));
            Parent root = loader.load();

            DetailWorkoutController ctrl = loader.getController();
            // Load dengan steps dari DB
            Workout full = service.getById(w.getWorkoutId());
            ctrl.setData(full);

            Stage stage = new Stage();
            stage.setTitle("Detail Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── Edit ─────────────────────────────────────────────────────
    private void openEdit(Workout w) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/admin/workout/EditWorkout.fxml"));
            Parent root = loader.load();

            EditWorkoutController ctrl = loader.getController();
            ctrl.setData(w, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── Video ────────────────────────────────────────────────────
    private void openVideo(Workout w) {
        if (w.getVideoUrl() == null || w.getVideoUrl().isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Video tidak tersedia.");
            return;
        }
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(w.getVideoUrl()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Tidak dapat membuka video: " + w.getVideoUrl());
        }
    }

    // ─── Hapus ────────────────────────────────────────────────────
    private void handleHapus(Workout w) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Workout");
        confirm.setHeaderText("Hapus program \"" + w.getTitle() + "\"?");
        confirm.setContentText("Tindakan ini tidak dapat dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = service.delete(w.getWorkoutId());
                if (ok) {
                    refreshData(); 
                }else {
                    showAlert(Alert.AlertType.ERROR, "Gagal menghapus workout.");
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
