package admin.dashboard;

import config.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {

    @FXML
    private Label lblIncome;
    @FXML
    private Label lblTotalMember;
    @FXML
    private Label lblActiveMember;
    @FXML
    private Label lblPendingPayment;
    @FXML
    private Label lblTotalWorkout;

    @FXML
    private Label lblTodayCheckin;
    @FXML
    private Label lblPopularPackage;
    @FXML
    private Label lblExpiringSoon;
    @FXML
    private Label lblPendingSummary;

    @FXML
    private ListView<String> listRecentMembers;
    @FXML
    private ListView<String> listRecentPayments;

    @FXML
    private LineChart<String, Number> memberGrowthChart;

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        System.out.println("DashboardController initialize jalan");
        setDefaultValue();

        setupRecentMembersList();
        setupRecentPaymentsList();

        loadDashboardData();
    }

    private void setDefaultValue() {
        lblIncome.setText("Rp0");
        lblTotalMember.setText("0");
        lblActiveMember.setText("0");
        lblPendingPayment.setText("0");
        lblTotalWorkout.setText("0");

        lblTodayCheckin.setText("0 orang");
        lblPopularPackage.setText("-");
        lblExpiringSoon.setText("0 akun");
        lblPendingSummary.setText("0 transaksi");

        listRecentMembers.setItems(FXCollections.observableArrayList("Belum ada data member."));
        listRecentPayments.setItems(FXCollections.observableArrayList("Belum ada data pembayaran."));
    }

    private void loadDashboardData() {
        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                System.out.println("Koneksi database null. Cek config.Database.");
                return;
            }

            loadStats(conn);
            loadIncome(conn);
            loadOperationalSummary(conn);
            loadRecentMembers(conn);
            loadRecentPayments(conn);
            loadMemberGrowthChart(conn);

        } catch (Exception e) {
            System.out.println("Gagal load dashboard:");
            e.printStackTrace();
        }
    }

    private int getCount(Connection conn, String sql) {
        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.out.println("Query gagal:");
            System.out.println(sql);
            e.printStackTrace();
        }

        return 0;
    }

    private void loadStats(Connection conn) {
        int totalMember = getCount(conn,
                "SELECT COUNT(*) AS total FROM users WHERE role = 'member'");

        int activeMember = getCount(conn,
                "SELECT COUNT(*) AS total FROM memberships WHERE status = 'aktif'");

        int pendingPayment = getCount(conn,
                "SELECT COUNT(*) AS total FROM payments WHERE status = 'pending'");

        int totalWorkout = getCount(conn,
                "SELECT COUNT(*) AS total FROM workouts");

        lblTotalMember.setText(String.valueOf(totalMember));
        lblActiveMember.setText(String.valueOf(activeMember));
        lblPendingPayment.setText(String.valueOf(pendingPayment));
        lblTotalWorkout.setText(String.valueOf(totalWorkout));
        lblPendingSummary.setText(pendingPayment + " transaksi");

        System.out.println("Total Member: " + totalMember);
        System.out.println("Member Aktif: " + activeMember);
        System.out.println("Pending Payment: " + pendingPayment);
        System.out.println("Workout Program: " + totalWorkout);
    }

    private void loadIncome(Connection conn) {
        String sql = """
                    SELECT COALESCE(SUM(amount), 0) AS total
                    FROM payments
                    WHERE status = 'verified'
                    AND MONTH(payment_date) = MONTH(CURRENT_DATE())
                    AND YEAR(payment_date) = YEAR(CURRENT_DATE())
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            double income = 0;

            if (rs.next()) {
                income = rs.getDouble("total");
            }

            lblIncome.setText(rupiahFormat.format(income));
            System.out.println("Income bulan ini: " + income);

        } catch (Exception e) {
            System.out.println("Gagal load income:");
            e.printStackTrace();
        }
    }

    private void loadOperationalSummary(Connection conn) {
        int todayCheckin = getCount(conn, """
                    SELECT COUNT(*) AS total
                    FROM checkins
                    WHERE DATE(checkin_time) = CURRENT_DATE()
                """);

        int expiringSoon = getCount(conn, """
                    SELECT COUNT(*) AS total
                    FROM memberships
                    WHERE status = 'aktif'
                    AND end_date BETWEEN CURRENT_DATE() AND DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY)
                """);

        String popularPackage = "Belum ada";

        String popularSql = """
                    SELECT mp.package_name, COUNT(*) AS total
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    GROUP BY mp.package_id, mp.package_name
                    ORDER BY total DESC
                    LIMIT 1
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(popularSql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                popularPackage = rs.getString("package_name");
            }
        } catch (Exception e) {
            System.out.println("Gagal load paket populer:");
            e.printStackTrace();
        }

        lblTodayCheckin.setText(todayCheckin + " orang");
        lblPopularPackage.setText(popularPackage);
        lblExpiringSoon.setText(expiringSoon + " akun");

        System.out.println("Check-in hari ini: " + todayCheckin);
        System.out.println("Paket populer: " + popularPackage);
        System.out.println("Membership hampir habis: " + expiringSoon);
    }

    private void loadRecentMembers(Connection conn) {
        ObservableList<String> members = FXCollections.observableArrayList();

        String sql = """
                    SELECT name, email, created_at
                    FROM users
                    WHERE role = 'member'
                    ORDER BY created_at DESC
                    LIMIT 5
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String item = rs.getString("name")
                        + " | "
                        + rs.getString("email")
                        + " | "
                        + rs.getTimestamp("created_at");

                members.add(item);
            }
        } catch (Exception e) {
            System.out.println("Gagal load member terbaru:");
            e.printStackTrace();
        }

        if (members.isEmpty()) {
            members.add("Belum ada member baru.");
        }

        listRecentMembers.setItems(members);
    }

    private void loadRecentPayments(Connection conn) {
        ObservableList<String> payments = FXCollections.observableArrayList();

        String sql = """
                    SELECT p.amount, p.status, p.payment_date, u.name
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN users u ON m.user_id = u.user_id
                    ORDER BY p.payment_date DESC
                    LIMIT 5
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String amount = rupiahFormat.format(rs.getDouble("amount"));

                String item = rs.getString("name")
                        + " | "
                        + amount
                        + " | "
                        + rs.getString("status")
                        + " | "
                        + rs.getTimestamp("payment_date");

                payments.add(item);
            }
        } catch (Exception e) {
            System.out.println("Gagal load pembayaran terbaru:");
            e.printStackTrace();
        }

        if (payments.isEmpty()) {
            payments.add("Belum ada pembayaran.");
        }

        listRecentPayments.setItems(payments);
    }

    private void loadMemberGrowthChart(Connection conn) {
        if (memberGrowthChart == null) {
            System.out.println("memberGrowthChart null. Cek fx:id di FXML.");
            return;
        }

        memberGrowthChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Member Baru");

        String sql = """
                    SELECT
                        DATE_FORMAT(created_at, '%b %Y') AS month_name,
                        COUNT(*) AS total
                    FROM users
                    WHERE role = 'member'
                    GROUP BY YEAR(created_at), MONTH(created_at)
                    ORDER BY YEAR(created_at), MONTH(created_at)
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getString("month_name"),
                                rs.getInt("total")));
            }
        } catch (Exception e) {
            System.out.println("Gagal load chart member:");
            e.printStackTrace();
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Belum ada data", 0));
        }

        memberGrowthChart.getData().add(series);
    }

    private void setupRecentMembersList() {
    listRecentMembers.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || item.isBlank()) {
                setText(null);
                setGraphic(null);
                return;
            }

            String[] parts = item.split("\\|");

            String name = parts.length > 0 ? parts[0].trim() : "-";
            String email = parts.length > 1 ? parts[1].trim() : "-";
            String createdAt = parts.length > 2 ? parts[2].trim() : "-";

            javafx.scene.control.Label lblName = new javafx.scene.control.Label(name);
            lblName.getStyleClass().add("recent-name");

            javafx.scene.control.Label lblEmail = new javafx.scene.control.Label(email);
            lblEmail.getStyleClass().add("recent-sub");

            javafx.scene.control.Label lblDate = new javafx.scene.control.Label(formatDateOnly(createdAt));
            lblDate.getStyleClass().addAll("recent-badge", "badge-date");

            javafx.scene.layout.VBox leftBox = new javafx.scene.layout.VBox(6, lblName, lblEmail);
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(12, leftBox, spacer, lblDate);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.getStyleClass().add("recent-row");

            setText(null);
            setGraphic(row);
        }
    });
}

private void setupRecentPaymentsList() {
    listRecentPayments.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || item.isBlank()) {
                setText(null);
                setGraphic(null);
                return;
            }

            String[] parts = item.split("\\|");

            String name = parts.length > 0 ? parts[0].trim() : "-";
            String amount = parts.length > 1 ? parts[1].trim() : "-";
            String status = parts.length > 2 ? parts[2].trim() : "-";

            javafx.scene.control.Label lblName = new javafx.scene.control.Label(name);
            lblName.getStyleClass().add("recent-name");

            javafx.scene.control.Label lblAmount = new javafx.scene.control.Label(amount);
            lblAmount.getStyleClass().add("recent-sub");

            javafx.scene.control.Label lblStatus = new javafx.scene.control.Label(capitalize(status));
            lblStatus.getStyleClass().add("recent-badge");

            if (status.equalsIgnoreCase("verified")) {
                lblStatus.getStyleClass().add("badge-verified");
            } else if (status.equalsIgnoreCase("pending")) {
                lblStatus.getStyleClass().add("badge-pending");
            } else {
                lblStatus.getStyleClass().add("badge-default");
            }

            javafx.scene.layout.VBox leftBox = new javafx.scene.layout.VBox(6, lblName, lblAmount);
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(12, leftBox, spacer, lblStatus);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.getStyleClass().add("recent-row");

            setText(null);
            setGraphic(row);
        }
    });
}

private String capitalize(String text) {
    if (text == null || text.isBlank()) return "-";
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
}

private String formatDateOnly(String rawDate) {
    try {
        java.time.LocalDateTime dt = java.time.LocalDateTime.parse(rawDate.replace(" ", "T"));
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH);
        return dt.format(formatter);
    } catch (Exception e) {
        return rawDate;
    }
}
}