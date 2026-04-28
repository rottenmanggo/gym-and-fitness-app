package payment;

import payment.model.Membership;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class MembershipController implements Initializable {

    @FXML
    private GridPane cardGrid;
    @FXML
    private TextField searchField;

    private final ObservableList<Membership> membershipList = FXCollections.observableArrayList();
    private FilteredList<Membership> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDummyData();
        filteredList = new FilteredList<>(membershipList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(m -> {
                if (newVal == null || newVal.isBlank())
                    return true;
                String lower = newVal.toLowerCase();
                return m.getNama().toLowerCase().contains(lower)
                        || m.getBenefit().toLowerCase().contains(lower)
                        || m.getStatus().toLowerCase().contains(lower);
            });
            renderCards();
        });

        renderCards();
    }

    // ─── Dummy Data ──────────────────────────────────────────────
    private void loadDummyData() {
        membershipList.addAll(
                new Membership("M001", "Basic Monthly", 30, 250000,
                        "Akses gym reguler, locker area, konsultasi awal",
                        LocalDate.now().minusDays(5)),
                new Membership("M002", "Premium Plus", 90, 650000,
                        "Gym akses penuh, kelas grup, 2x PT session",
                        LocalDate.now().minusDays(10)),
                new Membership("M003", "Fat Loss Plan", 60, 500000,
                        "Meal guide, kelas cardio, monitoring mingguan",
                        LocalDate.now().minusDays(3)),
                new Membership("M004", "Student Package", 30, 180000,
                        "Akses gym jam tertentu, harga hemat mahasiswa",
                        LocalDate.now().minusDays(45)) // sengaja expired
        );
    }

    // ─── Render kartu ke GridPane ─────────────────────────────────
    private void renderCards() {
        cardGrid.getChildren().clear();
        int col = 0, row = 0;
        for (Membership m : filteredList) {
            m.refreshStatus();
            cardGrid.add(buildCard(m), col, row);
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
    }

    private VBox buildCard(Membership m) {
        VBox card = new VBox(12);
        card.getStyleClass().add("membership-card");
        card.setPadding(new Insets(20));

        // Header: judul + badge
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(m.getNama());
        title.getStyleClass().add("card-title");
        Label meta = new Label(m.getDurasi() + " Hari  •  " + m.getHargaFormatted());
        meta.getStyleClass().add("card-meta");
        titleBox.getChildren().addAll(title, meta);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label badge = new Label(m.getStatus());
        badge.getStyleClass().addAll("status-badge",
                "Active".equals(m.getStatus()) ? "badge-active" : "badge-inactive");

        header.getChildren().addAll(titleBox, badge);

        // Expiry
        Label expiry = new Label("🗓  Aktif hingga: " + m.getTanggalAkhirFormatted());
        expiry.getStyleClass().add("card-expiry");

        // Benefit
        Label benefitTitle = new Label("Benefit Paket");
        benefitTitle.getStyleClass().add("card-benefit-title");
        Text benefitText = new Text(m.getBenefit());
        benefitText.getStyleClass().add("card-benefit-text");
        benefitText.setWrappingWidth(320);

        // Tombol aksi
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button btnDetail = new Button("👁  Detail");
        btnDetail.getStyleClass().addAll("card-btn", "btn-detail");
        btnDetail.setOnAction(e -> showDetail(m));

        Button btnEdit = new Button("✏  Edit");
        btnEdit.getStyleClass().addAll("card-btn", "btn-edit");
        btnEdit.setOnAction(e -> editMembership(m));

        Button btnHapus = new Button("🗑  Hapus");
        btnHapus.getStyleClass().addAll("card-btn", "btn-hapus");
        btnHapus.setOnAction(e -> deleteMembership(m));

        actions.getChildren().addAll(btnDetail, btnEdit, btnHapus);

        card.getChildren().addAll(header, expiry, new Separator(),
                benefitTitle, benefitText, actions);
        return card;
    }

    // ─── CRUD ────────────────────────────────────────────────────

    @FXML
    public void handleTambahPaket() {
        addMembership();
    }

    public void addMembership() {
        Dialog<Membership> dialog = buildFormDialog(null);
        Optional<Membership> result = dialog.showAndWait();
        result.ifPresent(m -> {
            membershipList.add(m);
            renderCards();
        });
    }

    public void editMembership(Membership m) {
        Dialog<Membership> dialog = buildFormDialog(m);
        dialog.showAndWait().ifPresent(updated -> {
            m.setNama(updated.getNama());
            m.setDurasi(updated.getDurasi());
            m.setHarga(updated.getHarga());
            m.setBenefit(updated.getBenefit());
            renderCards();
        });
    }

    public void deleteMembership(Membership m) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Membership");
        confirm.setHeaderText("Hapus paket \"" + m.getNama() + "\"?");
        confirm.setContentText("Tindakan ini tidak dapat dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                membershipList.remove(m);
                renderCards();
            }
        });
    }

    private void showDetail(Membership m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detail Membership");
        alert.setHeaderText(m.getNama());
        alert.setContentText(
                "Durasi   : " + m.getDurasi() + " Hari\n" +
                        "Harga    : " + m.getHargaFormatted() + "\n" +
                        "Benefit  : " + m.getBenefit() + "\n" +
                        "Status   : " + m.getStatus() + "\n" +
                        "Mulai    : " + m.getTanggalMulai() + "\n" +
                        "Berakhir : " + m.getTanggalAkhirFormatted());
        alert.showAndWait();
    }

    // ─── Form Dialog ─────────────────────────────────────────────

    private Dialog<Membership> buildFormDialog(Membership existing) {
        Dialog<Membership> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Tambah Paket" : "Edit Paket");
        dialog.setHeaderText(existing == null
                ? "Isi data paket membership baru"
                : "Ubah data paket: " + existing.getNama());

        ButtonType saveBtn = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfNama = new TextField(existing != null ? existing.getNama() : "");
        TextField tfDurasi = new TextField(existing != null ? String.valueOf(existing.getDurasi()) : "");
        TextField tfHarga = new TextField(existing != null ? String.valueOf((int) existing.getHarga()) : "");
        TextArea taBenefit = new TextArea(existing != null ? existing.getBenefit() : "");
        taBenefit.setPrefRowCount(3);
        taBenefit.setWrapText(true);

        tfNama.setPromptText("Contoh: Basic Monthly");
        tfDurasi.setPromptText("Contoh: 30");
        tfHarga.setPromptText("Contoh: 250000");
        taBenefit.setPromptText("Contoh: Akses gym reguler, locker area");

        grid.addRow(0, new Label("Nama Paket:"), tfNama);
        grid.addRow(1, new Label("Durasi (hari):"), tfDurasi);
        grid.addRow(2, new Label("Harga (Rp):"), tfHarga);
        grid.addRow(3, new Label("Benefit:"), taBenefit);
        GridPane.setHgrow(tfNama, Priority.ALWAYS);
        GridPane.setHgrow(taBenefit, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    String nama = tfNama.getText().trim();
                    int durasi = Integer.parseInt(tfDurasi.getText().trim());
                    double harga = Double.parseDouble(tfHarga.getText().trim());
                    String benefit = taBenefit.getText().trim();

                    if (nama.isEmpty() || benefit.isEmpty()) {
                        showError("Semua field wajib diisi!");
                        return null;
                    }

                    String newId = existing != null
                            ? existing.getId()
                            : "M" + String.format("%03d", membershipList.size() + 1);

                    return new Membership(newId, nama, durasi, harga, benefit, LocalDate.now());
                } catch (NumberFormatException ex) {
                    showError("Durasi dan Harga harus berupa angka!");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}