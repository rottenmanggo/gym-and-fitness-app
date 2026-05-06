package admin.membership;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMembershipController {

    @FXML private TextField tfNama;
    @FXML private TextField tfDurasi;
    @FXML private TextField tfHarga;
    @FXML private TextArea  taDeskripsi;

    private MembershipController parentController;
    private final MembershipService service = new MembershipService();

    public void setParentController(MembershipController ctrl) {
        this.parentController = ctrl;
    }

    @FXML
    public void handleSimpan() {
        String nama    = tfNama.getText().trim();
        String durasi  = tfDurasi.getText().trim();
        String harga   = tfHarga.getText().trim();
        String deskripsi = taDeskripsi.getText().trim();

        if (nama.isEmpty() || durasi.isEmpty() || harga.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nama, Durasi, dan Harga wajib diisi!");
            return;
        }

        try {
            int    durasiInt  = Integer.parseInt(durasi);
            double hargaDouble = Double.parseDouble(harga);

            Membership m = new Membership(0, nama, durasiInt, hargaDouble, deskripsi);
            boolean ok = service.insert(m);

            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Paket berhasil ditambahkan!");
                if (parentController != null) parentController.refreshData();
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal menyimpan paket ke database.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Durasi harus angka bulat, Harga harus angka.");
        }
    }

    @FXML
    public void handleKembali() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfNama.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}