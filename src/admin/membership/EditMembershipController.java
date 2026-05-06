package admin.membership;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditMembershipController {

    @FXML private TextField tfNama;
    @FXML private TextField tfDurasi;
    @FXML private TextField tfHarga;
    @FXML private TextArea  taDeskripsi;

    private Membership currentMembership;
    private MembershipController parentController;
    private final MembershipService service = new MembershipService();

    public void setData(Membership m, MembershipController ctrl) {
        this.currentMembership  = m;
        this.parentController   = ctrl;

        tfNama.setText(m.getPackageName());
        tfDurasi.setText(String.valueOf(m.getDurationDays()));
        tfHarga.setText(String.format("%.2f", m.getPrice()));
        taDeskripsi.setText(m.getDescription() != null ? m.getDescription() : "");
    }

    @FXML
    public void handleUpdate() {
        String nama     = tfNama.getText().trim();
        String durasi   = tfDurasi.getText().trim();
        String harga    = tfHarga.getText().trim();
        String deskripsi = taDeskripsi.getText().trim();

        if (nama.isEmpty() || durasi.isEmpty() || harga.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nama, Durasi, dan Harga wajib diisi!");
            return;
        }

        try {
            int    durasiInt   = Integer.parseInt(durasi);
            double hargaDouble = Double.parseDouble(harga);

            currentMembership.setPackageName(nama);
            currentMembership.setDurationDays(durasiInt);
            currentMembership.setPrice(hargaDouble);
            currentMembership.setDescription(deskripsi);

            boolean ok = service.update(currentMembership);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Paket berhasil diupdate!");
                if (parentController != null) parentController.refreshData();
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal mengupdate paket.");
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