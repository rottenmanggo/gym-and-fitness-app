package admin.workout;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditWorkoutController {

    @FXML private TextField tfJudul;
    @FXML private TextField tfKategori;
    @FXML private TextField tfEquipment;
    @FXML private TextField tfSets;
    @FXML private TextField tfReps;
    @FXML private TextField tfVideo;
    @FXML private TextField tfGambar;
    @FXML private TextArea  taDeskripsi;

    private Workout currentWorkout;
    private WorkoutController parentController;
    private final WorkoutService service = new WorkoutService();

    public void setData(Workout w, WorkoutController ctrl) {
        this.currentWorkout   = w;
        this.parentController = ctrl;

        tfJudul.setText(w.getTitle());
        tfKategori.setText(w.getCategory());
        tfEquipment.setText(w.getEquipment() != null ? w.getEquipment() : "");
        tfSets.setText(String.valueOf(w.getSets()));
        tfReps.setText(w.getReps() != null ? w.getReps() : "");
        tfVideo.setText(w.getVideoUrl() != null ? w.getVideoUrl() : "");
        tfGambar.setText(w.getImagePath() != null ? w.getImagePath() : "");
        taDeskripsi.setText(w.getDescription() != null ? w.getDescription() : "");
    }

    @FXML
    public void handleUpdate() {
        String judul    = tfJudul.getText().trim();
        String kategori = tfKategori.getText().trim();
        String sets     = tfSets.getText().trim();
        String reps     = tfReps.getText().trim();

        if (judul.isEmpty() || kategori.isEmpty() || sets.isEmpty() || reps.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Judul, Kategori, Set, dan Reps wajib diisi!");
            return;
        }

        try {
            int setsInt = Integer.parseInt(sets);
            currentWorkout.setTitle(judul);
            currentWorkout.setCategory(kategori);
            currentWorkout.setEquipment(tfEquipment.getText().trim());
            currentWorkout.setSets(setsInt);
            currentWorkout.setReps(reps);
            currentWorkout.setVideoUrl(tfVideo.getText().trim());
            currentWorkout.setDescription(taDeskripsi.getText().trim());
            currentWorkout.setImagePath(tfGambar.getText().trim());

            boolean ok = service.update(currentWorkout);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Workout berhasil diupdate!");
                if (parentController != null) parentController.refreshData();
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal mengupdate workout.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Jumlah Set harus berupa angka.");
        }
    }

    private void closeWindow() {
        ((Stage) tfJudul.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}