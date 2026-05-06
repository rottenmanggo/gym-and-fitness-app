package member.dashboard;

import config.Database;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import shared.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MemberDashboardController {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label heroMembershipStatusLabel;
    @FXML
    private Label heroMembershipPackageLabel;

    @FXML
    private Label membershipPackageLabel;
    @FXML
    private Label membershipMetaLabel;
    @FXML
    private Label checkinMonthLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label progressMetaLabel;
    @FXML
    private Label lastPaymentAmountLabel;
    @FXML
    private Label lastPaymentMetaLabel;

    @FXML
    private Label membershipStatusBadge;
    @FXML
    private Label summaryPackageLabel;
    @FXML
    private Label summaryStatusLabel;
    @FXML
    private Label summaryEndDateLabel;
    @FXML
    private Label summaryPriceLabel;

    @FXML
    private VBox workoutListContainer;

    @FXML
    private Label paymentStatusBadge;
    @FXML
    private VBox paymentRowsContainer;

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final DateTimeFormatter displayDate = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private int userId = 0;

    @FXML
    public void initialize() {
        rupiahFormat.setMaximumFractionDigits(0);

        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
            greetingLabel.setText("Halo, " + Session.getUser().getName() + " 👋");
        } else {
            greetingLabel.setText("Halo, Member 👋");
        }

        setDefaultValues();
        loadDashboardData();
    }

    private void setDefaultValues() {
        heroMembershipStatusLabel.setText("Pending");
        heroMembershipPackageLabel.setText("Belum Ada");

        membershipPackageLabel.setText("Belum Ada");
        membershipMetaLabel.setText("Status: Pending • sampai -");

        checkinMonthLabel.setText("0x");
        weightLabel.setText("0,0 kg");
        progressMetaLabel.setText("Belum ada progress");

        lastPaymentAmountLabel.setText(rupiahFormat.format(0));
        lastPaymentMetaLabel.setText("Pending pada -");

        summaryPackageLabel.setText("Belum Ada");
        summaryStatusLabel.setText("Pending");
        summaryEndDateLabel.setText("-");
        summaryPriceLabel.setText(rupiahFormat.format(0));

        setBadge(membershipStatusBadge, "Pending", "pending");
        setBadge(paymentStatusBadge, "Pending", "pending");

        workoutListContainer.getChildren().setAll(
                createEmptyText("Belum ada data workout."));

        paymentRowsContainer.getChildren().setAll(
                createEmptyText("Belum ada pembayaran."));
    }

    private void loadDashboardData() {
        if (userId <= 0) {
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                System.out.println("Database connection null.");
                return;
            }

            loadMembership(conn);
            loadCheckinMonth(conn);
            loadProgress(conn);
            loadLastPayment(conn);
            loadWorkouts(conn);
            loadRecentPayments(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMembership(Connection conn) {
        String sql = """
                    SELECT
                        m.membership_id,
                        m.status,
                        m.end_date,
                        mp.package_name,
                        mp.price
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    ORDER BY m.end_date DESC
                    LIMIT 1
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                String packageName = safe(rs.getString("package_name"), "Belum Ada");
                String statusRaw = safe(rs.getString("status"), "pending");
                String statusLabel = capitalize(statusRaw);
                LocalDate endDate = rs.getDate("end_date") == null
                        ? null
                        : rs.getDate("end_date").toLocalDate();
                double price = rs.getDouble("price");

                String endText = endDate == null ? "-" : endDate.format(displayDate);
                String priceText = rupiahFormat.format(price);

                heroMembershipStatusLabel.setText(statusLabel);
                heroMembershipPackageLabel.setText(packageName);

                membershipPackageLabel.setText(packageName);
                membershipMetaLabel.setText("Status: " + statusLabel + " • sampai " + endText);

                summaryPackageLabel.setText(packageName);
                summaryStatusLabel.setText(statusLabel);
                summaryEndDateLabel.setText(endText);
                summaryPriceLabel.setText(priceText);

                setBadge(membershipStatusBadge, statusLabel, statusRaw);
            }

        } catch (Exception e) {
            System.out.println("Gagal load membership.");
            e.printStackTrace();
        }
    }

    private void loadCheckinMonth(Connection conn) {
        String sql = """
                    SELECT COUNT(*) AS total
                    FROM checkins
                    WHERE user_id = ?
                    AND MONTH(checkin_time) = MONTH(CURDATE())
                    AND YEAR(checkin_time) = YEAR(CURDATE())
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    checkinMonthLabel.setText(rs.getInt("total") + "x");
                }
            }

        } catch (Exception e) {
            System.out.println("Gagal load check-in bulan ini.");
            e.printStackTrace();
        }
    }

    private void loadProgress(Connection conn) {
        String sql = """
                    SELECT weight_kg, muscle_mass, weekly_progress, log_date
                    FROM progress_logs
                    WHERE user_id = ?
                    ORDER BY log_date DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                double weight = rs.getDouble("weight_kg");
                String weeklyProgress = safe(rs.getString("weekly_progress"), "Belum ada progress");

                weightLabel.setText(String.format(Locale.US, "%.1f kg", weight));
                progressMetaLabel.setText(weeklyProgress);
            }

        } catch (Exception e) {
            System.out.println("Gagal load progress.");
            e.printStackTrace();
        }
    }

    private void loadLastPayment(Connection conn) {
        String sql = """
                    SELECT
                        p.payment_id,
                        p.amount,
                        p.status,
                        p.payment_date
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    WHERE m.user_id = ?
                    ORDER BY p.payment_date DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return;
                }

                double amount = rs.getDouble("amount");
                String statusRaw = safe(rs.getString("status"), "pending");
                String statusLabel = capitalize(statusRaw);
                Timestamp ts = rs.getTimestamp("payment_date");

                String dateText = "-";

                if (ts != null) {
                    dateText = ts.toLocalDateTime().toLocalDate().format(displayDate);
                }

                lastPaymentAmountLabel.setText(rupiahFormat.format(amount));
                lastPaymentMetaLabel.setText(statusLabel + " pada " + dateText);

                setBadge(paymentStatusBadge, statusLabel, statusRaw);
            }

        } catch (Exception e) {
            System.out.println("Gagal load pembayaran terakhir.");
            e.printStackTrace();
        }
    }

    private void loadWorkouts(Connection conn) {
        workoutListContainer.getChildren().clear();

        String sql = """
                    SELECT title, category, sets_count, reps_count
                    FROM workouts
                    ORDER BY workout_id DESC
                    LIMIT 3
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                String title = safe(rs.getString("title"), "-");
                String category = safe(rs.getString("category"), "-");
                int sets = rs.getInt("sets_count");
                String reps = safe(rs.getString("reps_count"), "-");

                workoutListContainer.getChildren().add(
                        createWorkoutRow(title, category, sets, reps));
            }

            if (!hasData) {
                workoutListContainer.getChildren().add(
                        createEmptyText("Belum ada data workout."));
            }

        } catch (Exception e) {
            System.out.println("Gagal load workout.");
            e.printStackTrace();
        }
    }

    private void loadRecentPayments(Connection conn) {
        paymentRowsContainer.getChildren().clear();

        String sql = """
                    SELECT
                        p.payment_id,
                        p.amount,
                        p.status,
                        p.payment_date,
                        mp.package_name
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    ORDER BY p.payment_date DESC
                    LIMIT 5
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;

                    int paymentId = rs.getInt("payment_id");
                    String packageName = safe(rs.getString("package_name"), "-");
                    double amount = rs.getDouble("amount");
                    String status = safe(rs.getString("status"), "pending");
                    Timestamp paymentTs = rs.getTimestamp("payment_date");

                    String dateText = "-";

                    if (paymentTs != null) {
                        dateText = paymentTs.toLocalDateTime().toLocalDate().format(displayDate);
                    }

                    paymentRowsContainer.getChildren().add(
                            createPaymentRow(
                                    "INV-" + paymentId,
                                    packageName,
                                    rupiahFormat.format(amount),
                                    status,
                                    dateText));
                }

                if (!hasData) {
                    paymentRowsContainer.getChildren().add(
                            createEmptyText("Belum ada pembayaran."));
                }
            }

        } catch (Exception e) {
            System.out.println("Gagal load riwayat pembayaran.");
            e.printStackTrace();
        }
    }

    private HBox createWorkoutRow(String title, String category, int sets, String reps) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("list-row");

        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label titleLabel = new Label(title + " — " + category);
        titleLabel.getStyleClass().add("list-row-title");

        Label subtitleLabel = new Label(sets + " set • " + reps + " reps");
        subtitleLabel.getStyleClass().add("list-row-subtitle");

        textBox.getChildren().addAll(titleLabel, subtitleLabel);

        Label badge = new Label("Workout");
        badge.getStyleClass().addAll("badge-soft", "badge-info");

        row.getChildren().addAll(textBox, badge);

        return row;
    }

    private HBox createPaymentRow(
            String invoice,
            String packageName,
            String amount,
            String status,
            String dateText) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("payment-row");

        Label invoiceLabel = createPaymentCell(invoice, true);
        Label packageLabel = createPaymentCell(packageName, false);
        Label amountLabel = createPaymentCell(amount, false);
        Label dateLabel = createPaymentCell(dateText, false);

        Label statusBadge = new Label(capitalize(status));
        statusBadge.getStyleClass().add("badge-soft");

        if (status.equalsIgnoreCase("verified")) {
            statusBadge.getStyleClass().add("badge-active");
        } else if (status.equalsIgnoreCase("rejected")) {
            statusBadge.getStyleClass().add("badge-failed");
        } else {
            statusBadge.getStyleClass().add("badge-pending");
        }

        HBox.setHgrow(invoiceLabel, Priority.ALWAYS);
        HBox.setHgrow(packageLabel, Priority.ALWAYS);
        HBox.setHgrow(amountLabel, Priority.ALWAYS);
        HBox.setHgrow(statusBadge, Priority.ALWAYS);
        HBox.setHgrow(dateLabel, Priority.ALWAYS);

        row.getChildren().addAll(invoiceLabel, packageLabel, amountLabel, statusBadge, dateLabel);

        return row;
    }

    private Label createPaymentCell(String text, boolean strong) {
        Label label = new Label(text);
        label.getStyleClass().add(strong ? "payment-td-strong" : "payment-td");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private Label createEmptyText(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-soft");
        return label;
    }

    private void setBadge(Label label, String text, String status) {
        label.setText(text);
        label.getStyleClass().removeAll(
                "badge-active",
                "badge-pending",
                "badge-failed",
                "badge-info");

        if (status == null) {
            label.getStyleClass().add("badge-pending");
            return;
        }

        switch (status.toLowerCase()) {
            case "aktif", "verified" -> label.getStyleClass().add("badge-active");
            case "rejected", "expired" -> label.getStyleClass().add("badge-failed");
            default -> label.getStyleClass().add("badge-pending");
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
}