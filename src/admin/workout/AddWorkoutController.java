package admin.workout;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddWorkoutController {

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

    private WorkoutController parentController;
    private final WorkoutService service = new WorkoutService();

    public void setParentController(WorkoutController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        categoryCombo.getItems().addAll(
                "Fat Loss",
                "Bulking",
                "Cardio",
                "Strength",
                "Beginner");

        categoryCombo.getSelectionModel().selectFirst();
        setsField.setText("3");
    }

    @FXML
    private void handleSimpan() {
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

        Workout workout = new Workout(
                0,
                category,
                title,
                equipment,
                tutorial,
                youtube,
                sets,
                reps,
                image.isEmpty() ? null : image);

        boolean success = service.insert(workout);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Workout berhasil ditambahkan.");

            if (parentController != null) {
                parentController.refreshData();
            }

            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Workout gagal ditambahkan.");
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