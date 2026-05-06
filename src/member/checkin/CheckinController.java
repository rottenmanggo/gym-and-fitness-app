package member.checkin;

import config.Database;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import shared.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CheckinController {

    @FXML
    private Label heroSubtitleLabel;
    @FXML
    private Label heroStatusLabel;
    @FXML
    private Label heroPackageLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label membershipBadge;
    @FXML
    private Label packageLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label todayStatusLabel;

    @FXML
    private TextArea notesArea;
    @FXML
    private Button checkinButton;
    @FXML
    private Label hintLabel;

    @FXML
    private VBox historyContainer;
    @FXML
    private Label totalHistoryLabel;

    private int userId = 0;
    private String memberName = "Member";

    private boolean activeMembership = false;
    private boolean alreadyCheckin = false;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    @FXML
    public void initialize() {
        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
            memberName = Session.getUser().getName();
        }

        heroSubtitleLabel.setText(
                "Halo, " + memberName
                        + ". Catat kehadiranmu hari ini agar progress latihan dan aktivitas gym kamu tersimpan dengan rapi.");

        setDefaultValues();
        loadCheckinPage();
    }

    private void setDefaultValues() {
        activeMembership = false;
        alreadyCheckin = false;

        heroStatusLabel.setText("Tidak Aktif");
        heroPackageLabel.setText("Belum ada membership aktif");

        packageLabel.setText("Belum ada membership aktif");
        endDateLabel.setText("-");
        todayStatusLabel.setText("Belum check-in");

        setBadge(membershipBadge, "Tidak Aktif", "expired");

        notesArea.setDisable(true);
        checkinButton.setDisable(true);
        checkinButton.setText("Check In Sekarang");

        hintLabel.setText("Kamu belum bisa check-in karena membership belum aktif atau sudah expired.");

        historyContainer.getChildren().setAll(createEmptyText("Belum ada riwayat check-in."));
        totalHistoryLabel.setText("0 data");
    }

    private void loadCheckinPage() {
        if (userId <= 0) {
            showMessage("Session member tidak ditemukan. Silakan login ulang.", "danger");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            loadActiveMembership(conn);
            loadTodayCheckin(conn);
            loadHistory(conn);
            refreshFormState();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memuat data check-in.", "danger");
        }
    }

    private void loadActiveMembership(Connection conn) {
        String sql = """
                    SELECT
                        m.membership_id,
                        m.start_date,
                        m.end_date,
                        m.status,
                        mp.package_name
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    AND m.status = 'aktif'
                    AND m.end_date >= CURDATE()
                    ORDER BY m.end_date DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    activeMembership = false;
                    return;
                }

                activeMembership = true;

                String packageName = rs.getString("package_name");
                LocalDate endDate = rs.getDate("end_date") == null
                        ? null
                        : rs.getDate("end_date").toLocalDate();

                String endText = endDate == null ? "-" : endDate.format(dateFormatter);

                heroStatusLabel.setText("Aktif");
                heroPackageLabel.setText(packageName);

                packageLabel.setText(packageName);
                endDateLabel.setText(endText);

                setBadge(membershipBadge, "Aktif", "aktif");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTodayCheckin(Connection conn) {
        String sql = """
                    SELECT checkin_id
                    FROM checkins
                    WHERE user_id = ?
                    AND DATE(checkin_time) = CURDATE()
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                alreadyCheckin = rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistory(Connection conn) {
        historyContainer.getChildren().clear();

        String sql = """
                    SELECT checkin_id, checkin_time, notes
                    FROM checkins
                    WHERE user_id = ?
                    ORDER BY checkin_time DESC
                    LIMIT 10
                """;

        int total = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    total++;

                    Timestamp ts = rs.getTimestamp("checkin_time");

                    String dateText = "-";
                    String timeText = "-";

                    if (ts != null) {
                        dateText = ts.toLocalDateTime().toLocalDate().format(dateFormatter);
                        timeText = ts.toLocalDateTime().toLocalTime().format(timeFormatter);
                    }

                    String notes = rs.getString("notes");

                    if (notes == null || notes.isBlank()) {
                        notes = "Check-in gym";
                    }

                    historyContainer.getChildren().add(
                            createHistoryRow(dateText, timeText, notes));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (total == 0) {
            historyContainer.getChildren().add(
                    createEmptyText("Belum ada riwayat check-in."));
        }

        totalHistoryLabel.setText(total + " data");
    }

    private void refreshFormState() {
        if (!activeMembership) {
            notesArea.setDisable(true);
            checkinButton.setDisable(true);
            checkinButton.setText("Check In Sekarang");
            todayStatusLabel.setText("Belum check-in");
            hintLabel.setText("Kamu belum bisa check-in karena membership belum aktif atau sudah expired.");
            return;
        }

        if (alreadyCheckin) {
            notesArea.setDisable(true);
            checkinButton.setDisable(true);
            checkinButton.setText("Sudah Check-in Hari Ini");
            todayStatusLabel.setText("Sudah check-in");
            hintLabel.setText("Kamu sudah check-in hari ini. Check-in hanya bisa dilakukan satu kali per hari.");
            return;
        }

        notesArea.setDisable(false);
        checkinButton.setDisable(false);
        checkinButton.setText("Check In Sekarang");
        todayStatusLabel.setText("Belum check-in");
        hintLabel.setText("");
    }

    @FXML
    private void handleCheckin() {
        if (!activeMembership) {
            showMessage(
                    "Membership kamu belum aktif atau sudah expired. Silakan selesaikan pembayaran terlebih dahulu.",
                    "danger");
            return;
        }

        if (alreadyCheckin) {
            showMessage("Kamu sudah check-in hari ini. Check-in hanya bisa dilakukan satu kali per hari.", "danger");
            return;
        }

        String notes = notesArea.getText() == null
                ? ""
                : notesArea.getText().trim();

        if (notes.isEmpty()) {
            notes = "Check-in gym";
        }

        String sql = """
                    INSERT INTO checkins (user_id, checkin_time, notes)
                    VALUES (?, NOW(), ?)
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, notes);

                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    showMessage("Check-in berhasil dicatat. Selamat latihan!", "success");
                    notesArea.clear();

                    alreadyCheckin = true;
                    loadTodayCheckin(conn);
                    loadHistory(conn);
                    refreshFormState();
                } else {
                    showMessage("Gagal melakukan check-in. Silakan coba lagi.", "danger");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal melakukan check-in. Silakan coba lagi.", "danger");
        }
    }

    private HBox createHistoryRow(String date, String time, String notes) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("payment-row");

        Label dateLabel = createCell(date, true);
        Label timeLabel = createCell(time, false);
        Label notesLabel = createCell(notes, false);

        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        HBox.setHgrow(timeLabel, Priority.ALWAYS);
        HBox.setHgrow(notesLabel, Priority.ALWAYS);

        row.getChildren().addAll(dateLabel, timeLabel, notesLabel);

        return row;
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

        label.getStyleClass().removeAll(
                "badge-active",
                "badge-pending",
                "badge-failed",
                "badge-info",
                "badge-expired");

        if (status == null) {
            label.getStyleClass().add("badge-failed");
            return;
        }

        switch (status.toLowerCase()) {
            case "aktif", "verified" -> label.getStyleClass().add("badge-active");
            case "pending" -> label.getStyleClass().add("badge-pending");
            default -> label.getStyleClass().add("badge-failed");
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
}