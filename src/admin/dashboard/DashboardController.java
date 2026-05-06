package admin.dashboard;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * DashboardController - Controller untuk konten Dashboard Admin.
 *
 * Hanya menangani logika data dashboard (stats, chart, metrics).
 * Navigasi sidebar dan logout ditangani oleh SidebarAdminController
 * melalui LayoutTopController (single-shell architecture).
 */
public class DashboardController {

    @FXML
    private Label incomeLabel;

    @FXML
    private Label totalMemberLabel;

    @FXML
    private Label activeMemberLabel;

    @FXML
    private Label pendingPaymentLabel;

    @FXML
    private Label totalWorkoutLabel;

    @FXML
    private LineChart<String, Number> memberGrowthChart;

    @FXML
    private VBox metricList;

    @FXML
    public void initialize() {
        loadStats();
        loadChart();
        loadMetrics();
    }

    private void loadStats() {
        incomeLabel.setText("Rp 8.500.000");
        totalMemberLabel.setText("120");
        activeMemberLabel.setText("86");
        pendingPaymentLabel.setText("7");
        totalWorkoutLabel.setText("15");
    }

    private void loadChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("Jan", 12));
        series.getData().add(new XYChart.Data<>("Feb", 18));
        series.getData().add(new XYChart.Data<>("Mar", 15));
        series.getData().add(new XYChart.Data<>("Apr", 24));
        series.getData().add(new XYChart.Data<>("Mei", 31));
        series.getData().add(new XYChart.Data<>("Jun", 38));

        memberGrowthChart.getData().clear();
        memberGrowthChart.getData().add(series);
    }

    private void loadMetrics() {
        metricList.getChildren().clear();

        metricList.getChildren().add(createMetricRow("Check-in hari ini", "24 orang"));
        metricList.getChildren().add(createMetricRow("Paket populer", "Gold"));
        metricList.getChildren().add(createMetricRow("Membership hampir habis", "5 akun"));
        metricList.getChildren().add(createMetricRow("Pembayaran pending", "7 transaksi"));
    }

    private HBox createMetricRow(String title, String value) {
        HBox row = new HBox();
        row.getStyleClass().add("metric-row");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");

        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }
}