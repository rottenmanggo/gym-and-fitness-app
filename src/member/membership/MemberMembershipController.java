package member.membership;

import config.Database;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import shared.Session;

import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class MemberMembershipController {

    @FXML
    private Label heroStatusLabel;
    @FXML
    private Label heroPackageLabel;
    @FXML
    private Label messageLabel;

    @FXML
    private Label currentPackageLabel;
    @FXML
    private Label currentPriceLabel;
    @FXML
    private Label membershipStatusLabel;
    @FXML
    private Label membershipEndMetaLabel;
    @FXML
    private Label remainingDaysLabel;
    @FXML
    private Label paymentStatusLabel;
    @FXML
    private Label paymentAmountLabel;

    @FXML
    private Label membershipBadge;
    @FXML
    private Label detailPackageLabel;
    @FXML
    private Label detailStartLabel;
    @FXML
    private Label detailEndLabel;
    @FXML
    private Label detailRemainingLabel;
    @FXML
    private Label detailPriceLabel;

    @FXML
    private Label paymentBadge;
    @FXML
    private Label invoiceLabel;
    @FXML
    private Label paymentNominalLabel;
    @FXML
    private Label paymentDetailStatusLabel;
    @FXML
    private Label paymentDateLabel;
    @FXML
    private Label proofLabel;
    @FXML
    private Button goPaymentButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label historyCountLabel;
    @FXML
    private VBox historyContainer;

    @FXML
    private Label packageCountLabel;
    @FXML
    private HBox packagesContainer;

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private int userId = 0;
    private Integer currentPaymentId = null;

    @FXML
    public void initialize() {
        rupiahFormat.setMaximumFractionDigits(0);

        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
        }

        setDefaultValues();
        loadPageData();
    }

    private void setDefaultValues() {
        heroStatusLabel.setText("Belum Ada");
        heroPackageLabel.setText("Belum ada membership");

        currentPackageLabel.setText("Belum ada membership");
        currentPriceLabel.setText(rupiahFormat.format(0));

        membershipStatusLabel.setText("Belum Ada");
        membershipEndMetaLabel.setText("Berlaku sampai -");
        remainingDaysLabel.setText("-");

        paymentStatusLabel.setText("Belum Ada");
        paymentAmountLabel.setText(rupiahFormat.format(0));

        detailPackageLabel.setText("Belum ada membership");
        detailStartLabel.setText("-");
        detailEndLabel.setText("-");
        detailRemainingLabel.setText("-");
        detailPriceLabel.setText(rupiahFormat.format(0));

        invoiceLabel.setText("-");
        paymentNominalLabel.setText(rupiahFormat.format(0));
        paymentDetailStatusLabel.setText("Belum Ada");
        paymentDateLabel.setText("-");
        proofLabel.setText("Belum upload");

        descriptionLabel.setText("Kamu belum memiliki membership.");

        setBadge(membershipBadge, "Belum Ada", "expired");
        setBadge(paymentBadge, "Belum Ada", "rejected");

        goPaymentButton.setDisable(true);

        historyContainer.getChildren().setAll(createEmptyText("Belum ada riwayat membership."));
        packagesContainer.getChildren().clear();
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

            autoExpireMembership(conn);
            loadCurrentMembership(conn);
            loadMembershipHistory(conn);
            loadPackages(conn);

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memuat data membership.", "danger");
        }
    }

    private void autoExpireMembership(Connection conn) throws SQLException {
        String sql = """
                    UPDATE memberships
                    SET status = 'expired'
                    WHERE user_id = ?
                    AND end_date < CURDATE()
                    AND status = 'aktif'
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private void loadCurrentMembership(Connection conn) {
        String sql = """
                    SELECT
                        m.membership_id,
                        m.start_date,
                        m.end_date,
                        m.status,
                        mp.package_name,
                        mp.duration_days,
                        mp.price,
                        mp.description,
                        p.payment_id,
                        p.amount,
                        p.payment_date,
                        p.status AS payment_status,
                        p.proof_file
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    LEFT JOIN payments p ON p.payment_id = (
                        SELECT p2.payment_id
                        FROM payments p2
                        WHERE p2.membership_id = m.membership_id
                        ORDER BY p2.payment_date DESC, p2.payment_id DESC
                        LIMIT 1
                    )
                    WHERE m.user_id = ?
                    ORDER BY m.end_date DESC, m.membership_id DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                String packageName = safe(rs.getString("package_name"), "Belum ada membership");
                String membershipStatusRaw = safe(rs.getString("status"), "expired");
                String membershipStatusText = capitalize(membershipStatusRaw);

                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();

                String startText = startDate == null ? "-" : startDate.format(dateFormatter);
                String endText = endDate == null ? "-" : endDate.format(dateFormatter);

                double price = rs.getDouble("price");
                String priceText = rupiahFormat.format(price);
                String description = safe(rs.getString("description"), "Belum ada deskripsi paket.");

                String remainingText = getRemainingText(membershipStatusRaw, endDate);

                String paymentStatusRaw = safe(rs.getString("payment_status"), "pending");
                String paymentStatusText = capitalize(paymentStatusRaw);
                double amount = rs.getDouble("amount");
                String amountText = rupiahFormat.format(amount);

                int paymentId = rs.getInt("payment_id");
                currentPaymentId = rs.wasNull() ? null : paymentId;

                Timestamp paymentDate = rs.getTimestamp("payment_date");
                String paymentDateText = paymentDate == null
                        ? "-"
                        : paymentDate.toLocalDateTime().toLocalDate().format(dateFormatter);

                String proofFile = rs.getString("proof_file");

                heroStatusLabel.setText(membershipStatusText);
                heroPackageLabel.setText(packageName);

                currentPackageLabel.setText(packageName);
                currentPriceLabel.setText(priceText);

                membershipStatusLabel.setText(membershipStatusText);
                membershipEndMetaLabel.setText("Berlaku sampai " + endText);
                remainingDaysLabel.setText(remainingText);

                paymentStatusLabel.setText(paymentStatusText);
                paymentAmountLabel.setText(amountText);

                detailPackageLabel.setText(packageName);
                detailStartLabel.setText(startText);
                detailEndLabel.setText(endText);
                detailRemainingLabel.setText(remainingText);
                detailPriceLabel.setText(priceText);

                invoiceLabel.setText(currentPaymentId == null ? "-" : "INV-" + currentPaymentId);
                paymentNominalLabel.setText(amountText);
                paymentDetailStatusLabel.setText(paymentStatusText);
                paymentDateLabel.setText(paymentDateText);
                proofLabel.setText(proofFile == null || proofFile.isBlank() ? "Belum upload" : "Sudah upload");

                descriptionLabel.setText(description);

                setBadge(membershipBadge, membershipStatusText, membershipStatusRaw);
                setBadge(paymentBadge, paymentStatusText, paymentStatusRaw);

                goPaymentButton.setDisable(currentPaymentId == null || paymentStatusRaw.equalsIgnoreCase("verified"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMembershipHistory(Connection conn) {
        historyContainer.getChildren().clear();

        String sql = """
                    SELECT
                        m.membership_id,
                        m.start_date,
                        m.end_date,
                        m.status,
                        mp.package_name,
                        mp.price,
                        p.payment_id,
                        p.status AS payment_status
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    LEFT JOIN payments p ON p.payment_id = (
                        SELECT p2.payment_id
                        FROM payments p2
                        WHERE p2.membership_id = m.membership_id
                        ORDER BY p2.payment_date DESC, p2.payment_id DESC
                        LIMIT 1
                    )
                    WHERE m.user_id = ?
                    ORDER BY m.start_date DESC, m.membership_id DESC
                """;

        int total = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    total++;

                    String packageName = safe(rs.getString("package_name"), "-");
                    LocalDate startDate = rs.getDate("start_date") == null ? null
                            : rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();

                    String startText = startDate == null ? "-" : startDate.format(dateFormatter);
                    String endText = endDate == null ? "-" : endDate.format(dateFormatter);
                    String priceText = rupiahFormat.format(rs.getDouble("price"));

                    String membershipStatus = safe(rs.getString("status"), "expired");
                    String paymentStatus = safe(rs.getString("payment_status"), "pending");

                    historyContainer.getChildren().add(
                            createHistoryRow(packageName, startText, endText, priceText, membershipStatus,
                                    paymentStatus));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (total == 0) {
            historyContainer.getChildren().add(createEmptyText("Belum ada riwayat membership."));
        }

        historyCountLabel.setText(total + " data");
    }

    private void loadPackages(Connection conn) {
        packagesContainer.getChildren().clear();

        String sql = """
                    SELECT package_id, package_name, duration_days, price, description
                    FROM membership_packages
                    ORDER BY price ASC
                """;

        int total = 0;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                total++;

                int packageId = rs.getInt("package_id");
                String name = safe(rs.getString("package_name"), "-");
                int duration = rs.getInt("duration_days");
                double price = rs.getDouble("price");
                String desc = safe(rs.getString("description"), "Tidak ada deskripsi.");

                packagesContainer.getChildren().add(
                        createPackageCard(packageId, name, duration, price, desc));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (total == 0) {
            packagesContainer.getChildren().add(createEmptyText("Belum ada paket membership tersedia."));
        }

        packageCountLabel.setText(total + " paket");
    }

    private VBox createPackageCard(int packageId, String name, int duration, double price, String desc) {
        VBox card = new VBox(10);
        card.getStyleClass().add("membership-package-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label durationLabel = new Label(duration + " hari");
        durationLabel.getStyleClass().add("stat-label");

        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        nameLabel.getStyleClass().add("package-card-title");

        Label priceLabel = new Label(rupiahFormat.format(price));
        priceLabel.getStyleClass().add("package-card-price");

        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.getStyleClass().add("text-soft");

        Button chooseButton = new Button("Pilih Paket");
        chooseButton.setMaxWidth(Double.MAX_VALUE);
        chooseButton.getStyleClass().add("primary-btn");
        chooseButton.setOnAction(event -> choosePackage(packageId));

        card.getChildren().addAll(durationLabel, nameLabel, priceLabel, descLabel, chooseButton);
        return card;
    }

    private HBox createHistoryRow(
            String packageName,
            String start,
            String end,
            String price,
            String membershipStatus,
            String paymentStatus) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("payment-row");

        Label packageLabel = createCell(packageName, true);
        Label startLabel = createCell(start, false);
        Label endLabel = createCell(end, false);
        Label priceLabel = createCell(price, false);

        Label membershipBadge = new Label(capitalize(membershipStatus));
        membershipBadge.getStyleClass().add("badge-soft");
        addMembershipBadgeClass(membershipBadge, membershipStatus);

        Label paymentBadge = new Label(capitalize(paymentStatus));
        paymentBadge.getStyleClass().add("badge-soft");
        addPaymentBadgeClass(paymentBadge, paymentStatus);

        HBox.setHgrow(packageLabel, Priority.ALWAYS);
        HBox.setHgrow(startLabel, Priority.ALWAYS);
        HBox.setHgrow(endLabel, Priority.ALWAYS);
        HBox.setHgrow(priceLabel, Priority.ALWAYS);
        HBox.setHgrow(membershipBadge, Priority.ALWAYS);
        HBox.setHgrow(paymentBadge, Priority.ALWAYS);

        row.getChildren().addAll(packageLabel, startLabel, endLabel, priceLabel, membershipBadge, paymentBadge);

        return row;
    }

    private void choosePackage(int packageId) {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            if (hasActiveOrPendingMembership(conn)) {
                showMessage(
                        "Kamu masih punya membership aktif atau pending. Selesaikan dulu sebelum memilih paket baru.",
                        "danger");
                return;
            }

            String packageSql = """
                        SELECT package_id, price, duration_days
                        FROM membership_packages
                        WHERE package_id = ?
                        LIMIT 1
                    """;

            int durationDays;
            double amount;

            try (PreparedStatement stmt = conn.prepareStatement(packageSql)) {
                stmt.setInt(1, packageId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        showMessage("Paket tidak ditemukan.", "danger");
                        return;
                    }

                    durationDays = rs.getInt("duration_days");
                    amount = rs.getDouble("price");
                }
            }

            conn.setAutoCommit(false);

            try {
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = startDate.plusDays(durationDays);

                String insertMembershipSql = """
                            INSERT INTO memberships (user_id, package_id, start_date, end_date, status)
                            VALUES (?, ?, ?, ?, 'pending')
                        """;

                int membershipId;

                try (PreparedStatement stmt = conn.prepareStatement(insertMembershipSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, packageId);
                    stmt.setDate(3, Date.valueOf(startDate));
                    stmt.setDate(4, Date.valueOf(endDate));
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            showMessage("Gagal membuat membership.", "danger");
                            return;
                        }

                        membershipId = keys.getInt(1);
                    }
                }

                String insertPaymentSql = """
                            INSERT INTO payments (membership_id, amount, payment_date, proof_file, status, verified_by)
                            VALUES (?, ?, NOW(), NULL, 'pending', NULL)
                        """;

                int paymentId;

                try (PreparedStatement stmt = conn.prepareStatement(insertPaymentSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, membershipId);
                    stmt.setDouble(2, amount);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        paymentId = keys.next() ? keys.getInt(1) : 0;
                    }
                }

                conn.commit();

                showMessage("Paket berhasil dipilih. Silakan upload bukti pembayaran di menu Payments. Invoice: INV-"
                        + paymentId, "success");

                setDefaultValues();
                loadPageData();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memilih paket membership.", "danger");
        }
    }

    private boolean hasActiveOrPendingMembership(Connection conn) throws SQLException {
        String sql = """
                    SELECT membership_id
                    FROM memberships
                    WHERE user_id = ?
                    AND status IN ('aktif', 'pending')
                    AND end_date >= CURDATE()
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @FXML
    private void handleGoPayment() {
        if (currentPaymentId == null) {
            showAlert(Alert.AlertType.INFORMATION, "Payment", "Belum ada invoice pembayaran.");
            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Upload Bukti Pembayaran",
                "Silakan buka menu Payments untuk upload bukti pembayaran invoice INV-" + currentPaymentId + ".");
    }

    private String getRemainingText(String status, LocalDate endDate) {
        if (status == null) {
            return "-";
        }

        if (status.equalsIgnoreCase("pending")) {
            return "Menunggu pembayaran/verifikasi";
        }

        if (endDate == null || status.equalsIgnoreCase("expired")) {
            return "Sudah berakhir";
        }

        LocalDate today = LocalDate.now();

        if (endDate.isBefore(today)) {
            return "Sudah berakhir";
        }

        long days = ChronoUnit.DAYS.between(today, endDate);
        return days + " hari lagi";
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

        if (status == null) {
            label.getStyleClass().add("badge-failed");
            return;
        }

        if (status.equalsIgnoreCase("aktif") || status.equalsIgnoreCase("verified")) {
            label.getStyleClass().add("badge-active");
        } else if (status.equalsIgnoreCase("pending")) {
            label.getStyleClass().add("badge-pending");
        } else {
            label.getStyleClass().add("badge-failed");
        }
    }

    private void addMembershipBadgeClass(Label label, String status) {
        if ("aktif".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-active");
        } else if ("pending".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-pending");
        } else {
            label.getStyleClass().add("badge-failed");
        }
    }

    private void addPaymentBadgeClass(Label label, String status) {
        if ("verified".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-active");
        } else if ("pending".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-pending");
        } else {
            label.getStyleClass().add("badge-failed");
        }
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

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}