package admin.workout;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddWorkoutController {

    @FXML private TextField tfJudul;
    @FXML private TextField tfKategori;
    @FXML private TextField tfEquipment;
    @FXML private TextField tfSets;
    @FXML private TextField tfReps;
    @FXML private TextField tfVideo;
    @FXML private TextField tfGambar;
    @FXML private TextArea  taDeskripsi;

    private WorkoutController parentController;
    private final WorkoutService service = new WorkoutService();

    public void setParentController(WorkoutController ctrl) {
        this.parentController = ctrl;
    }

    @FXML
    public void handleSimpan() {
        String judul    = tfJudul.getText().trim();
        String kategori = tfKategori.getText().trim();
        String equipment = tfEquipment.getText().trim();
        String sets     = tfSets.getText().trim();
        String reps     = tfReps.getText().trim();
        String video    = tfVideo.getText().trim();
        String gambar   = tfGambar.getText().trim();
        String deskripsi = taDeskripsi.getText().trim();

        if (judul.isEmpty() || kategori.isEmpty() || sets.isEmpty() || reps.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Judul, Kategori, Set, dan Reps wajib diisi!");
            return;
        }

        try {
            int setsInt = Integer.parseInt(sets);
            Workout w = new Workout(0, kategori, judul, equipment, deskripsi,
                                    video, setsInt, reps,
                                    gambar.isEmpty() ? null : gambar);
            boolean ok = service.insert(w);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Program workout berhasil ditambahkan!");
                if (parentController != null) parentController.refreshData();
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal menyimpan ke database.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Jumlah Set harus berupa angka.");
        }
    }

    @FXML
    public void handleKembali() { closeWindow(); }

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