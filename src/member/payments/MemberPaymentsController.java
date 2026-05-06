package member.payments;

import config.Database;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import shared.Session;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MemberPaymentsController {

    @FXML
    private Label totalPaymentHeroLabel;
    @FXML
    private Label messageLabel;

    @FXML
    private VBox pendingPaymentCard;
    @FXML
    private VBox noPendingCard;

    @FXML
    private ImageView qrImageView;

    @FXML
    private Label pendingStatusBadge;
    @FXML
    private Label pendingPackageLabel;
    @FXML
    private Label pendingInvoiceLabel;
    @FXML
    private Label pendingAmountLabel;
    @FXML
    private Label pendingPeriodLabel;
    @FXML
    private Label selectedFileLabel;
    @FXML
    private Button uploadButton;

    @FXML
    private Label historyCountLabel;
    @FXML
    private VBox historyContainer;

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm",
            Locale.ENGLISH);

    private int userId = 0;
    private Integer pendingPaymentId = null;
    private File selectedProofFile = null;

    @FXML
    public void initialize() {
        rupiahFormat.setMaximumFractionDigits(0);

        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
        }

        loadQrImage();
        setDefaultValues();
        loadPageData();
    }

    private void loadQrImage() {
        try {
            URL imageUrl = getClass().getResource("/assets/image/qr-payment.png");

            if (imageUrl == null) {
                System.out.println("QR image tidak ditemukan: /assets/image/qr-payment.png");
                return;
            }

            qrImageView.setImage(new Image(imageUrl.toExternalForm()));
        } catch (Exception e) {
            System.out.println("Gagal load QR image.");
            e.printStackTrace();
        }
    }

    private void setDefaultValues() {
        totalPaymentHeroLabel.setText("0");

        pendingPaymentCard.setVisible(false);
        pendingPaymentCard.setManaged(false);

        noPendingCard.setVisible(false);
        noPendingCard.setManaged(false);

        pendingPaymentId = null;
        selectedProofFile = null;

        if (selectedFileLabel != null) {
            selectedFileLabel.setText("Belum pilih file");
        }

        if (uploadButton != null) {
            uploadButton.setDisable(false);
        }

        historyContainer.getChildren().setAll(createEmptyText("Belum ada riwayat pembayaran."));
        historyCountLabel.setText("0 data");
    }

    private void loadPageData() {
        if (userId <= 0) {
            showMessage("Session member tidak ditemukan. Silakan login ulang.", "danger");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            loadPendingPayment(conn);
            loadPaymentHistory(conn);

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memuat data pembayaran.", "danger");
        }
    }

    private void loadPendingPayment(Connection conn) {
        String sql = """
                    SELECT
                        p.payment_id,
                        p.membership_id,
                        p.amount,
                        p.status,
                        p.proof_file,
                        p.payment_date,
                        m.start_date,
                        m.end_date,
                        m.status AS membership_status,
                        mp.package_name,
                        mp.duration_days
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    AND m.status = 'pending'
                    AND p.status IN ('pending', 'rejected')
                    ORDER BY p.payment_id DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    pendingPaymentCard.setVisible(false);
                    pendingPaymentCard.setManaged(false);

                    noPendingCard.setVisible(true);
                    noPendingCard.setManaged(true);
                    return;
                }

                pendingPaymentId = rs.getInt("payment_id");

                String packageName = safe(rs.getString("package_name"), "-");
                double amount = rs.getDouble("amount");
                String status = safe(rs.getString("status"), "pending");

                LocalDate startDate = rs.getDate("start_date") == null
                        ? null
                        : rs.getDate("start_date").toLocalDate();

                LocalDate endDate = rs.getDate("end_date") == null
                        ? null
                        : rs.getDate("end_date").toLocalDate();

                String period = formatDate(startDate) + " - " + formatDate(endDate);

                pendingPackageLabel.setText(packageName);
                pendingInvoiceLabel.setText("#PAY-" + String.format("%04d", pendingPaymentId));
                pendingAmountLabel.setText(rupiahFormat.format(amount));
                pendingPeriodLabel.setText(period);

                String displayStatus = status;
                if ("rejected".equalsIgnoreCase(status)) {
                    displayStatus = "Upload Ulang";
                }

                setBadge(pendingStatusBadge, capitalize(displayStatus), status);

                pendingPaymentCard.setVisible(true);
                pendingPaymentCard.setManaged(true);

                noPendingCard.setVisible(false);
                noPendingCard.setManaged(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentHistory(Connection conn) {
        historyContainer.getChildren().clear();

        String sql = """
                    SELECT
                        p.payment_id,
                        p.membership_id,
                        p.amount,
                        p.payment_date,
                        p.proof_file,
                        p.status,
                        m.start_date,
                        m.end_date,
                        m.status AS membership_status,
                        mp.package_name
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    ORDER BY p.payment_date DESC, p.payment_id DESC
                """;

        int total = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    total++;

                    int paymentId = rs.getInt("payment_id");
                    String packageName = safe(rs.getString("package_name"), "-");
                    double amount = rs.getDouble("amount");
                    String status = safe(rs.getString("status"), "pending");
                    String proofFile = rs.getString("proof_file");

                    Timestamp paymentTs = rs.getTimestamp("payment_date");

                    String dateText = "-";

                    if (paymentTs != null) {
                        dateText = paymentTs.toLocalDateTime().format(dateTimeFormatter);
                    }

                    historyContainer.getChildren().add(
                            createHistoryRow(
                                    paymentId,
                                    packageName,
                                    rupiahFormat.format(amount),
                                    dateText,
                                    proofFile,
                                    status));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (total == 0) {
            historyContainer.getChildren().add(createEmptyText("Belum ada riwayat pembayaran."));
        }

        totalPaymentHeroLabel.setText(String.valueOf(total));
        historyCountLabel.setText(total + " data");
    }

    @FXML
    private void handleChooseProof() {
        if (pendingPaymentId == null) {
            showMessage("Tidak ada pembayaran pending untuk upload bukti.", "danger");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Bukti Pembayaran");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bukti Pembayaran", "*.jpg", "*.jpeg", "*.png", "*.pdf"),
                new FileChooser.ExtensionFilter("JPG / JPEG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File file = fileChooser.showOpenDialog(pendingPaymentCard.getScene().getWindow());

        if (file == null) {
            return;
        }

        long maxSize = 2L * 1024L * 1024L;

        if (file.length() > maxSize) {
            showMessage("Ukuran file maksimal 2MB.", "danger");
            return;
        }

        String ext = getExtension(file.getName());

        if (!isAllowedExtension(ext)) {
            showMessage("Format file harus JPG, JPEG, PNG, atau PDF.", "danger");
            return;
        }

        selectedProofFile = file;
        selectedFileLabel.setText(file.getName());

        showMessage("File bukti sudah dipilih. Klik Upload Bukti Pembayaran.", "success");
    }

    @FXML
    private void handleUploadProof() {
        if (pendingPaymentId == null) {
            showMessage("Tidak ada pembayaran pending untuk upload bukti.", "danger");
            return;
        }

        if (selectedProofFile == null) {
            showMessage("Silakan pilih bukti pembayaran terlebih dahulu.", "danger");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            if (!isPaymentBelongsToUser(conn, pendingPaymentId)) {
                showMessage("Data pembayaran tidak ditemukan.", "danger");
                return;
            }

            if (isPaymentVerified(conn, pendingPaymentId)) {
                showMessage("Pembayaran ini sudah diverifikasi, bukti tidak bisa diubah.", "danger");
                return;
            }

            String ext = getExtension(selectedProofFile.getName());

            if (!isAllowedExtension(ext)) {
                showMessage("Format file harus JPG, JPEG, PNG, atau PDF.", "danger");
                return;
            }

            long maxSize = 2L * 1024L * 1024L;

            if (selectedProofFile.length() > maxSize) {
                showMessage("Ukuran file maksimal 2MB.", "danger");
                return;
            }

            String newFileName = "payment_" + pendingPaymentId + "_" + System.currentTimeMillis() + "." + ext;

            Path uploadDir = Path.of("gym-and-fitness-app", "uploads", "payments");
            Files.createDirectories(uploadDir);

            Path targetPath = uploadDir.resolve(newFileName);

            Files.copy(
                    selectedProofFile.toPath(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING);

            String updateSql = """
                        UPDATE payments
                        SET
                            proof_file = ?,
                            status = 'pending',
                            payment_date = NOW()
                        WHERE payment_id = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, newFileName);
                stmt.setInt(2, pendingPaymentId);

                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    showMessage("Bukti pembayaran berhasil diupload. Silakan tunggu verifikasi admin.", "success");

                    selectedProofFile = null;
                    selectedFileLabel.setText("Belum pilih file");

                    setDefaultValues();
                    loadPageData();
                } else {
                    showMessage("Gagal menyimpan bukti pembayaran ke database.", "danger");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal upload file bukti pembayaran.", "danger");
        }
    }

    private boolean isPaymentBelongsToUser(Connection conn, int paymentId) throws SQLException {
        String sql = """
                    SELECT p.payment_id
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    WHERE p.payment_id = ?
                    AND m.user_id = ?
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isPaymentVerified(Connection conn, int paymentId) throws SQLException {
        String sql = """
                    SELECT status
                    FROM payments
                    WHERE payment_id = ?
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                return "verified".equalsIgnoreCase(rs.getString("status"));
            }
        }
    }

    private HBox createHistoryRow(
            int paymentId,
            String packageName,
            String amount,
            String date,
            String proofFile,
            String status) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("payment-row");

        Label invoiceLabel = createCell("#PAY-" + String.format("%04d", paymentId), true);
        Label packageLabel = createCell(packageName, false);
        Label amountLabel = createCell(amount, false);
        Label dateLabel = createCell(date, false);

        Button proofButton = new Button();
        proofButton.setMaxWidth(Double.MAX_VALUE);

        if (proofFile == null || proofFile.isBlank()) {
            proofButton.setText("Belum upload");
            proofButton.setDisable(true);
            proofButton.getStyleClass().add("table-disabled-btn");
        } else {
            proofButton.setText("Lihat");
            proofButton.getStyleClass().add("btn-outline-soft-small");
            proofButton.setOnAction(event -> openProofFile(proofFile));
        }

        Label statusBadge = new Label(capitalize(status));
        statusBadge.getStyleClass().add("badge-soft");

        if ("verified".equalsIgnoreCase(status)) {
            statusBadge.getStyleClass().add("badge-active");
        } else if ("rejected".equalsIgnoreCase(status)) {
            statusBadge.getStyleClass().add("badge-failed");
        } else {
            statusBadge.getStyleClass().add("badge-pending");
        }

        HBox.setHgrow(invoiceLabel, Priority.ALWAYS);
        HBox.setHgrow(packageLabel, Priority.ALWAYS);
        HBox.setHgrow(amountLabel, Priority.ALWAYS);
        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        HBox.setHgrow(proofButton, Priority.ALWAYS);
        HBox.setHgrow(statusBadge, Priority.ALWAYS);

        row.getChildren().addAll(invoiceLabel, packageLabel, amountLabel, dateLabel, proofButton, statusBadge);

        return row;
    }

    private void openProofFile(String proofFile) {
        if (proofFile == null || proofFile.isBlank()) {
            showMessage("File bukti pembayaran kosong.", "danger");
            return;
        }

        try {
            File file = findPaymentProofFile(proofFile);

            if (file == null || !file.exists()) {
                showMessage(
                        "File bukti tidak ditemukan. Pastikan file ada di gym-and-fitness-app/uploads/payments/",
                        "danger");
                return;
            }

            System.out.println("Membuka bukti pembayaran: " + file.getAbsolutePath());

            if (!Desktop.isDesktopSupported()) {
                showAlert(Alert.AlertType.ERROR, "Tidak Didukung", "Desktop open tidak didukung di perangkat ini.");
                return;
            }

            Desktop.getDesktop().open(file);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak bisa membuka file bukti pembayaran.");
        }
    }

    private File findPaymentProofFile(String proofFile) {
        String cleanFile = proofFile.trim();

        // Kalau database terlanjur menyimpan path, ambil nama filenya saja.
        cleanFile = cleanFile.replace("\\", "/");
        if (cleanFile.contains("/")) {
            cleanFile = cleanFile.substring(cleanFile.lastIndexOf("/") + 1);
        }

        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path parentDir = currentDir.getParent();

        Path[] possiblePaths = {
                currentDir.resolve("uploads/payments").resolve(cleanFile),
                currentDir.resolve("gym-and-fitness-app/uploads/payments").resolve(cleanFile),
                currentDir.resolve("src/uploads/payments").resolve(cleanFile),
                currentDir.resolve("src/gym-and-fitness-app/uploads/payments").resolve(cleanFile),
                parentDir == null ? null : parentDir.resolve("uploads/payments").resolve(cleanFile),
                parentDir == null ? null : parentDir.resolve("gym-and-fitness-app/uploads/payments").resolve(cleanFile),
                parentDir == null ? null : parentDir.resolve("src/uploads/payments").resolve(cleanFile),
                parentDir == null ? null
                        : parentDir.resolve("src/gym-and-fitness-app/uploads/payments").resolve(cleanFile)
        };

        for (Path path : possiblePaths) {
            if (path == null) {
                continue;
            }

            File file = path.toFile();
            System.out.println("Cek bukti pembayaran: " + file.getAbsolutePath());

            if (Files.exists(path)) {
                return file;
            }
        }

        return null;
    }

    private Label createCell(String text, boolean strong) {
        Label label = new Label(text);
        label.getStyleClass().add(strong ? "payment-td-strong" : "payment-td");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private Label createEmptyText(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-soft");
        label.setStyle("-fx-padding: 16;");
        return label;
    }

    private void setBadge(Label label, String text, String status) {
        label.setText(text);
        label.getStyleClass().removeAll("badge-active", "badge-pending", "badge-failed", "badge-info");

        if ("verified".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-active");
        } else if ("rejected".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-failed");
        } else {
            label.getStyleClass().add("badge-pending");
        }
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(dateFormatter);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String ext) {
        return ext.equals("jpg")
                || ext.equals("jpeg")
                || ext.equals("png")
                || ext.equals("pdf");
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        messageLabel.getStyleClass().removeAll("alert-success", "alert-danger");

        if ("success".equalsIgnoreCase(type)) {
            messageLabel.getStyleClass().add("alert-success");
        } else {
            messageLabel.getStyleClass().add("alert-danger");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}