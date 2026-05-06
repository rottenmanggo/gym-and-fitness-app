package admin.workout;

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

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class WorkoutController {

    @FXML
    private TextField searchField;
    @FXML
    private Label totalDataLabel;
    @FXML
    private GridPane cardGrid;

    private final WorkoutService service = new WorkoutService();
    private final ObservableList<Workout> workoutList = FXCollections.observableArrayList();
    private FilteredList<Workout> filteredList;

    @FXML
    public void initialize() {
        setupGrid();
        loadFromDatabase();

        filteredList = new FilteredList<>(workoutList, workout -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));

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
        workoutList.clear();

        List<Workout> data = service.getAll();
        workoutList.addAll(data);

        updateTotalLabel(workoutList.size());
    }

    public void refreshData() {
        loadFromDatabase();

        filteredList = new FilteredList<>(workoutList, workout -> true);

        if (searchField != null) {
            searchField.clear();
        }

        renderCards();
    }

    private void handleSearch(String keyword) {
        String search = keyword == null ? "" : keyword.toLowerCase().trim();

        filteredList.setPredicate(workout -> {
            if (search.isEmpty()) {
                return true;
            }

            return safe(workout.getTitle()).toLowerCase().contains(search)
                    || safe(workout.getCategory()).toLowerCase().contains(search)
                    || safe(workout.getEquipment()).toLowerCase().contains(search)
                    || safe(workout.getDescription()).toLowerCase().contains(search)
                    || safe(workout.getReps()).toLowerCase().contains(search)
                    || String.valueOf(workout.getSets()).contains(search);
        });

        renderCards();
    }

    @FXML
    private void handleReset() {
        searchField.clear();

        if (filteredList != null) {
            filteredList.setPredicate(workout -> true);
        }

        renderCards();
    }

    private void renderCards() {
        cardGrid.getChildren().clear();

        if (filteredList == null || filteredList.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.getStyleClass().add("empty-card");

            Label title = new Label("Belum ada data workout.");
            title.getStyleClass().add("empty-title");

            Label subtitle = new Label("Tambahkan program workout baru agar member bisa melihat daftar latihan.");
            subtitle.getStyleClass().add("empty-subtitle");

            emptyBox.getChildren().addAll(title, subtitle);
            cardGrid.add(emptyBox, 0, 0, 2, 1);

            updateTotalLabel(0);
            return;
        }

        int col = 0;
        int row = 0;

        for (Workout workout : filteredList) {
            VBox card = buildCard(workout);

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

    private VBox buildCard(Workout workout) {
        VBox card = new VBox(16);
        card.getStyleClass().add("workout-card");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label title = new Label(workout.getTitle());
        title.getStyleClass().add("card-title");

        Label meta = new Label(workout.getMetaInfo());
        meta.getStyleClass().add("card-meta");

        titleBox.getChildren().addAll(title, meta);

        Label badge = new Label("Workout");
        badge.getStyleClass().addAll("badge-soft", "badge-info");

        header.getChildren().addAll(titleBox, badge);

        VBox cardList = new VBox(10);
        cardList.getStyleClass().add("card-list");

        VBox equipmentBox = new VBox(4);

        Label equipmentTitle = new Label("Equipment");
        equipmentTitle.getStyleClass().add("list-row-title");

        Label equipmentText = new Label(workout.getEquipmentText());
        equipmentText.getStyleClass().add("list-row-subtitle");
        equipmentText.setWrapText(true);

        equipmentBox.getChildren().addAll(equipmentTitle, equipmentText);

        VBox tutorialBox = new VBox(4);

        Label tutorialTitle = new Label("Tutorial");
        tutorialTitle.getStyleClass().add("list-row-title");

        Label tutorialText = new Label(workout.getDescriptionText());
        tutorialText.getStyleClass().add("list-row-subtitle");
        tutorialText.setWrapText(true);
        tutorialText.setMaxHeight(52);

        tutorialBox.getChildren().addAll(tutorialTitle, tutorialText);

        cardList.getChildren().addAll(equipmentBox, tutorialBox);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button videoButton = new Button("Video");
        videoButton.getStyleClass().add("btn-outline-dark");
        videoButton.setOnAction(event -> openVideo(workout));

        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("btn-outline-soft");
        detailButton.setOnAction(event -> openDetail(workout));

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("btn-outline-soft");
        editButton.setOnAction(event -> openEdit(workout));

        Button deleteButton = new Button("Hapus");
        deleteButton.getStyleClass().add("btn-outline-danger");
        deleteButton.setOnAction(event -> handleDelete(workout));

        if (workout.getVideoUrl() == null || workout.getVideoUrl().isBlank()) {
            videoButton.setDisable(true);
        }

        actions.getChildren().addAll(videoButton, detailButton, editButton, deleteButton);

        card.getChildren().addAll(header, cardList, actions);

        return card;
    }

    @FXML
    private void handleTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/workout/AddWorkout.fxml"));
            Parent root = loader.load();

            AddWorkoutController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Program Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form tambah workout tidak bisa dibuka.");
        }
    }

    private void openDetail(Workout workout) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/workout/DetailWorkout.fxml"));
            Parent root = loader.load();

            DetailWorkoutController controller = loader.getController();
            Workout fullWorkout = service.getById(workout.getWorkoutId());
            controller.setData(fullWorkout);

            Stage stage = new Stage();
            stage.setTitle("Detail Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Detail workout tidak bisa dibuka.");
        }
    }

    private void openEdit(Workout workout) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/workout/EditWorkout.fxml"));
            Parent root = loader.load();

            EditWorkoutController controller = loader.getController();
            controller.setData(workout, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Workout");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Form edit workout tidak bisa dibuka.");
        }
    }

    private void openVideo(Workout workout) {
        if (workout.getVideoUrl() == null || workout.getVideoUrl().isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Video Kosong", "Video tidak tersedia.");
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(workout.getVideoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak bisa membuka video.");
        }
    }

    private void handleDelete(Workout workout) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Workout");
        confirm.setHeaderText("Hapus program \"" + workout.getTitle() + "\"?");
        confirm.setContentText("Data workout dan step latihan akan dihapus.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = service.delete(workout.getWorkoutId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Workout berhasil dihapus.");
                refreshData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Workout gagal dihapus.");
            }
        }
    }

    private void updateTotalLabel(int total) {
        if (totalDataLabel != null) {
            totalDataLabel.setText(total + " program");
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