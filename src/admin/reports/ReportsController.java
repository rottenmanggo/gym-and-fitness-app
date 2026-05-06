package admin.reports;

import config.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ReportsController {

    @FXML
    private Label lblTodayIncome;
    @FXML
    private Label lblMonthIncome;
    @FXML
    private Label lblNewMembers;
    @FXML
    private Label lblBestPackage;
    @FXML
    private Label lblHeroBestPackage;

    @FXML
    private LineChart<String, Number> incomeReportChart;
    @FXML
    private BarChart<String, Number> memberReportChart;

    @FXML
    private TableView<MonthlyReport> reportTable;
    @FXML
    private TableColumn<MonthlyReport, String> colMonth;
    @FXML
    private TableColumn<MonthlyReport, String> colRevenue;
    @FXML
    private TableColumn<MonthlyReport, String> colNewMembers;
    @FXML
    private TableColumn<MonthlyReport, String> colBestPackage;

    @FXML
    private Label totalDataLabel;
    @FXML
    private TextField searchField;

    private final ObservableList<MonthlyReport> allReports = FXCollections.observableArrayList();

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        rupiahFormat.setMaximumFractionDigits(0);

        setupTable();
        loadSummaryCards();
        loadIncomeChart();
        loadMemberChart();
        loadMonthlyReports();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
    }

    private Connection getConnection() {
        return Database.getConnection();
    }

    private void setupTable() {
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMonth.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMonthName()));
        colRevenue.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRevenueFormatted()));
        colNewMembers.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNewMembers() + " orang"));
        colBestPackage.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBestPackage()));

        colMonth.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String month, boolean empty) {
                super.updateItem(month, empty);

                if (empty || month == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label label = new Label(month);
                label.getStyleClass().add("month-text");

                setText(null);
                setGraphic(label);
            }
        });

        reportTable.setItems(allReports);
    }

    private void loadSummaryCards() {
        try (Connection conn = getConnection()) {
            if (conn == null)
                return;

            double todayIncome = getDouble(conn, """
                        SELECT COALESCE(SUM(amount), 0) AS total
                        FROM payments
                        WHERE status = 'verified'
                        AND DATE(payment_date) = CURRENT_DATE()
                    """);

            double monthIncome = getDouble(conn, """
                        SELECT COALESCE(SUM(amount), 0) AS total
                        FROM payments
                        WHERE status = 'verified'
                        AND MONTH(payment_date) = MONTH(CURRENT_DATE())
                        AND YEAR(payment_date) = YEAR(CURRENT_DATE())
                    """);

            int newMembersMonth = getInt(conn, """
                        SELECT COUNT(*) AS total
                        FROM users
                        WHERE role = 'member'
                        AND MONTH(created_at) = MONTH(CURRENT_DATE())
                        AND YEAR(created_at) = YEAR(CURRENT_DATE())
                    """);

            String bestPackage = getString(conn, """
                        SELECT mp.package_name
                        FROM memberships m
                        JOIN membership_packages mp ON m.package_id = mp.package_id
                        GROUP BY mp.package_id, mp.package_name
                        ORDER BY COUNT(*) DESC
                        LIMIT 1
                    """, "package_name", "Belum ada");

            lblTodayIncome.setText(rupiahFormat.format(todayIncome));
            lblMonthIncome.setText(rupiahFormat.format(monthIncome));
            lblNewMembers.setText(String.valueOf(newMembersMonth));
            lblBestPackage.setText(bestPackage);
            lblHeroBestPackage.setText(bestPackage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIncomeChart() {
        incomeReportChart.getData().clear();
        incomeReportChart.setLegendVisible(false);
        incomeReportChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan");

        String sql = """
                    SELECT
                        DATE_FORMAT(payment_date, '%b') AS month_name,
                        COALESCE(SUM(amount), 0) AS total
                    FROM payments
                    WHERE status = 'verified'
                    GROUP BY YEAR(payment_date), MONTH(payment_date)
                    ORDER BY YEAR(payment_date), MONTH(payment_date)
                    LIMIT 6
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getString("month_name"),
                                rs.getDouble("total")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Jan", 0));
            series.getData().add(new XYChart.Data<>("Feb", 0));
            series.getData().add(new XYChart.Data<>("Mar", 0));
            series.getData().add(new XYChart.Data<>("Apr", 0));
        }

        incomeReportChart.getData().add(series);
    }

    private void loadMemberChart() {
        memberReportChart.getData().clear();
        memberReportChart.setLegendVisible(false);
        memberReportChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Member Baru");

        String sql = """
                    SELECT
                        DATE_FORMAT(created_at, '%b') AS month_name,
                        COUNT(*) AS total
                    FROM users
                    WHERE role = 'member'
                    GROUP BY YEAR(created_at), MONTH(created_at)
                    ORDER BY YEAR(created_at), MONTH(created_at)
                    LIMIT 6
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getString("month_name"),
                                rs.getInt("total")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Jan", 0));
            series.getData().add(new XYChart.Data<>("Feb", 0));
            series.getData().add(new XYChart.Data<>("Mar", 0));
            series.getData().add(new XYChart.Data<>("Apr", 0));
        }

        memberReportChart.getData().add(series);
    }

    private void loadMonthlyReports() {
        allReports.clear();

        String sql = """
                    SELECT
                        DATE_FORMAT(u.created_at, '%M %Y') AS month_name,
                        COUNT(DISTINCT u.user_id) AS new_members,
                        COALESCE(SUM(CASE WHEN p.status = 'verified' THEN p.amount ELSE 0 END), 0) AS revenue,
                        COALESCE((
                            SELECT mp2.package_name
                            FROM memberships m2
                            JOIN membership_packages mp2 ON m2.package_id = mp2.package_id
                            WHERE YEAR(m2.start_date) = YEAR(u.created_at)
                            AND MONTH(m2.start_date) = MONTH(u.created_at)
                            GROUP BY mp2.package_id, mp2.package_name
                            ORDER BY COUNT(*) DESC
                            LIMIT 1
                        ), '-') AS best_package
                    FROM users u
                    LEFT JOIN memberships m ON u.user_id = m.user_id
                    LEFT JOIN payments p ON m.membership_id = p.membership_id
                    WHERE u.role = 'member'
                    GROUP BY YEAR(u.created_at), MONTH(u.created_at)
                    ORDER BY YEAR(u.created_at) DESC, MONTH(u.created_at) DESC
                    LIMIT 6
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allReports.add(new MonthlyReport(
                        rs.getString("month_name"),
                        rs.getDouble("revenue"),
                        rs.getInt("new_members"),
                        rs.getString("best_package")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        reportTable.setItems(allReports);
        updateTotalLabel(allReports.size());
    }

    private void handleSearch() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            reportTable.setItems(allReports);
            updateTotalLabel(allReports.size());
            return;
        }

        ObservableList<MonthlyReport> filtered = allReports
                .filtered(report -> report.getMonthName().toLowerCase().contains(keyword)
                        || report.getBestPackage().toLowerCase().contains(keyword)
                        || report.getRevenueFormatted().toLowerCase().contains(keyword)
                        || String.valueOf(report.getNewMembers()).contains(keyword));

        reportTable.setItems(filtered);
        updateTotalLabel(filtered.size());
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        reportTable.setItems(allReports);
        updateTotalLabel(allReports.size());
    }

    @FXML
    private void handleDownloadReport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Laporan");
            fileChooser.setInitialFileName("laporan_gymbrut.csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV File", "*.csv"));

            File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

            if (file == null) {
                return;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Bulan,Pendapatan,Member Baru,Paket Terlaris\n");

                for (MonthlyReport report : reportTable.getItems()) {
                    writer.write(
                            escapeCsv(report.getMonthName()) + ","
                                    + report.getRevenue() + ","
                                    + report.getNewMembers() + ","
                                    + escapeCsv(report.getBestPackage()) + "\n");
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Laporan berhasil disimpan.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Laporan gagal disimpan.");
        }
    }

    private double getDouble(Connection conn, String sql) {
        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getInt(Connection conn, String sql) {
        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String getString(Connection conn, String sql, String column, String defaultValue) {
        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String value = rs.getString(column);
                return value == null || value.isBlank() ? defaultValue : value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    private void updateTotalLabel(int total) {
        totalDataLabel.setText(total + " data");
    }

    private String escapeCsv(String value) {
        if (value == null)
            return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public class MonthlyReport {
        private final String monthName;
        private final double revenue;
        private final int newMembers;
        private final String bestPackage;

        public MonthlyReport(String monthName, double revenue, int newMembers, String bestPackage) {
            this.monthName = monthName;
            this.revenue = revenue;
            this.newMembers = newMembers;
            this.bestPackage = bestPackage == null ? "-" : bestPackage;
        }

        public String getMonthName() {
            return monthName;
        }

        public double getRevenue() {
            return revenue;
        }

        public String getRevenueFormatted() {
            return rupiahFormat.format(revenue);
        }

        public int getNewMembers() {
            return newMembers;
        }

        public String getBestPackage() {
            return bestPackage;
        }
    }
}