package admin.workout;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditWorkoutController {

    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private TextField titleField;
    @FXML
    private TextField equipmentField;
    @FXML
    private TextField youtubeField;
    @FXML
    private TextField setsField;
    @FXML
    private TextField repsField;
    @FXML
    private TextField imageField;
    @FXML
    private TextArea tutorialArea;

    private Workout currentWorkout;
    private WorkoutController parentController;
    private final WorkoutService service = new WorkoutService();

    @FXML
    public void initialize() {
        categoryCombo.getItems().addAll(
                "Fat Loss",
                "Bulking",
                "Cardio",
                "Strength",
                "Beginner");
    }

    public void setData(Workout workout, WorkoutController parentController) {
        this.currentWorkout = workout;
        this.parentController = parentController;

        titleField.setText(workout.getTitle());
        categoryCombo.setValue(workout.getCategory());
        equipmentField.setText(workout.getEquipment() == null ? "" : workout.getEquipment());
        youtubeField.setText(workout.getVideoUrl() == null ? "" : workout.getVideoUrl());
        setsField.setText(String.valueOf(workout.getSets()));
        repsField.setText(workout.getReps() == null ? "" : workout.getReps());
        imageField.setText(workout.getImagePath() == null ? "" : workout.getImagePath());
        tutorialArea.setText(workout.getDescription() == null ? "" : workout.getDescription());
    }

    @FXML
    private void handleUpdate() {
        if (currentWorkout == null) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Data workout tidak ditemukan.");
            return;
        }

        String category = categoryCombo.getValue();
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String equipment = equipmentField.getText() == null ? "" : equipmentField.getText().trim();
        String youtube = youtubeField.getText() == null ? "" : youtubeField.getText().trim();
        String setsText = setsField.getText() == null ? "" : setsField.getText().trim();
        String reps = repsField.getText() == null ? "" : repsField.getText().trim();
        String image = imageField.getText() == null ? "" : imageField.getText().trim();
        String tutorial = tutorialArea.getText() == null ? "" : tutorialArea.getText().trim();

        if (category == null || title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Kategori dan judul workout wajib diisi.");
            return;
        }

        int sets = 0;

        if (!setsText.isEmpty()) {
            try {
                sets = Integer.parseInt(setsText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Format Salah", "Jumlah set harus berupa angka.");
                return;
            }
        }

        currentWorkout.setCategory(category);
        currentWorkout.setTitle(title);
        currentWorkout.setEquipment(equipment);
        currentWorkout.setVideoUrl(youtube);
        currentWorkout.setSets(sets);
        currentWorkout.setReps(reps);
        currentWorkout.setImagePath(image.isEmpty() ? null : image);
        currentWorkout.setDescription(tutorial);

        boolean success = service.update(currentWorkout);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Workout berhasil diperbarui.");

            if (parentController != null) {
                parentController.refreshData();
            }

            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Workout gagal diperbarui.");
        }
    }

    @FXML
    private void handleKembali() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}