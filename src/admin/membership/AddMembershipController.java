package admin.membership;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMembershipController {

    @FXML
    private TextField tfNama;
    @FXML
    private TextField tfDurasi;
    @FXML
    private TextField tfHarga;
    @FXML
    private TextArea taDeskripsi;

    private MembershipController parentController;
    private final MembershipService service = new MembershipService();

    public void setParentController(MembershipController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSimpan() {
        String nama = tfNama.getText() == null ? "" : tfNama.getText().trim();
        String durasi = tfDurasi.getText() == null ? "" : tfDurasi.getText().trim();
        String harga = tfHarga.getText() == null ? "" : tfHarga.getText().trim();
        String deskripsi = taDeskripsi.getText() == null ? "" : taDeskripsi.getText().trim();

        if (nama.isEmpty() || durasi.isEmpty() || harga.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nama paket, durasi, dan harga wajib diisi.");
            return;
        }

        try {
            int durasiInt = Integer.parseInt(durasi);
            double hargaDouble = Double.parseDouble(harga);

            if (durasiInt <= 0 || hargaDouble <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Durasi dan harga harus lebih dari 0.");
                return;
            }

            Membership membership = new Membership(0, nama, durasiInt, hargaDouble, deskripsi);
            boolean success = service.insert(membership);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Paket membership berhasil ditambahkan.");

                if (parentController != null) {
                    parentController.refreshData();
                }

                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Paket membership gagal ditambahkan.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Durasi dan harga harus berupa angka.");
        }
    }

    @FXML
    private void handleKembali() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfNama.getScene().getWindow();
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