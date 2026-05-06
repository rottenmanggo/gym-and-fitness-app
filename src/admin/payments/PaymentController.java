package admin.payments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import shared.SceneManager;
import config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.Node;
import shared.Session;
import java.io.File;
import java.awt.Desktop;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import admin.payments.Payment.Status;

public class PaymentController implements Initializable {

    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, String> colInvoice;
    @FXML
    private TableColumn<Payment, String> colNama;
    @FXML
    private TableColumn<Payment, Double> colNominal;
    @FXML
    private TableColumn<Payment, String> colPaket;
    @FXML
    private TableColumn<Payment, String> colBukti;
    @FXML
    private TableColumn<Payment, Void> colAksi;
    @FXML
    private TableColumn<Payment, Status> colStatus;
    @FXML
    private TableColumn<Payment, LocalDate> colTanggal;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusInfo;

    private final ObservableList<Payment> paymentList = FXCollections.observableArrayList();
    private FilteredList<Payment> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPaymentData();
        setupColumns();

        filteredList = new FilteredList<>(paymentList, p -> true);
        searchField.textProperty().addListener((obs, o, newVal) -> filteredList.setPredicate(p -> {
            if (newVal == null || newVal.isBlank())
                return true;
            String lower = newVal.toLowerCase();
            return p.getInvoice().toLowerCase().contains(lower)
                    || p.getNamaMember().toLowerCase().contains(lower)
                    || p.getPaket().toLowerCase().contains(lower);
        }));

        paymentTable.setItems(filteredList);
    }

    private void loadPaymentData() {
        paymentList.clear();

        try {
            Connection conn = Database.getConnection();

            String query = """
                    SELECT 
                        p.payment_id,
                        p.amount,
                        p.payment_date,
                        p.status,
                        p.proof_file,
                        u.name,
                        pkg.package_name AS package_name
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN users u ON m.user_id = u.user_id
                    JOIN membership_packages pkg ON m.package_id = pkg.package_id
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String invoice = "INV-" + rs.getInt("payment_id");
                String nama = rs.getString("name");
                double nominal = rs.getDouble("amount");

                String paket = rs.getString("package_name");
                String bukti = rs.getString("proof_file");

                String dbStatus = rs.getString("status");

                Payment.Status status;

                switch (dbStatus.toLowerCase()) {
                    case "verified":
                        status = Payment.Status.PAID;
                        break;

                    case "pending":
                        status = Payment.Status.PENDING;
                        break;

                    default:
                        status = Payment.Status.FAILED;
                        break;
                }

                LocalDate tanggal = rs.getTimestamp("payment_date")
                        .toLocalDateTime()
                        .toLocalDate();

                paymentList.add(new Payment(
                    invoice,
                    nama,
                    nominal,
                    paket,
                    status,
                    tanggal,
                    bukti));
            }

        } catch (Exception e) {
            System.out.println("ERROR LOAD PAYMENT:");
            e.printStackTrace();
        }
    }

    // ─── Dummy Data ──────────────────────────────────────────────
    private void loadDummyData() {
        paymentList.addAll(
                new Payment("INV-24081", "Andi Saputra", 650000, "Transfer Bank", Status.PAID,
                        LocalDate.of(2026, 4, 21), "M002"),
                new Payment("INV-24082", "Rina Permata", 500000, "QRIS", Status.PAID, LocalDate.of(2026, 4, 21),
                        "M003"),
                new Payment("INV-24083", "Dimas Pratama", 250000, "Cash", Status.PENDING, LocalDate.of(2026, 4, 20),
                        "M001"),
                new Payment("INV-24084", "Salsa Putri", 650000, "Transfer Bank", Status.FAILED,
                        LocalDate.of(2026, 4, 20), "M002"),
                new Payment("INV-24085", "Fikri Ramadhan", 250000, "Debit Card", Status.PAID, LocalDate.of(2026, 4, 19),
                        "M001"),
                new Payment("INV-24086", "Hana Kusuma", 180000, "QRIS", Status.PENDING, LocalDate.of(2026, 4, 18),
                        "M004"),
                new Payment("INV-24087", "Budi Santoso", 500000, "Cash", Status.FAILED, LocalDate.of(2026, 4, 17),
                        "M003"));
    }

    // ─── Setup Kolom ─────────────────────────────────────────────
    private void setupColumns() {
        // Invoice — bold
        colInvoice.setCellValueFactory(new PropertyValueFactory<>("invoice"));
        colInvoice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty ? "" : "-fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

                
            }
        });

        // Nama Member
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaMember"));

        // Nominal — format Rp
        colNominal.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        colNominal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("Rp %,.0f", item));
            }
        });

        // Paket
        colPaket.setCellValueFactory(new PropertyValueFactory<>("paket"));

        // Status — badge berwarna
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Status item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                String statusText = switch (item) {
                    case PAID -> "Verified";
                    case PENDING -> "Pending";
                    case FAILED -> "Rejected";
                };

                Label badge = new Label(statusText);
                badge.setAlignment(Pos.CENTER);
                badge.setMinWidth(72);
                badge.setStyle(
                        "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12 4 12;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                switch (item) {
                                    case PAID -> "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;";
                                    case PENDING -> "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;";
                                    case FAILED -> "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;";
                                });

                HBox wrapper = new HBox(badge);
                wrapper.setAlignment(Pos.CENTER_LEFT);
                setGraphic(wrapper);
                setText(null);
            }
        });

        // Tanggal — format dd MMM yyyy
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colTanggal.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(fmt));
            }
        });

        // Bukti
        colBukti.setCellValueFactory(new PropertyValueFactory<>("proofFile"));
        colBukti.setCellFactory(col -> new TableCell<Payment, String>() {

            private final Button btnLihat = new Button("Lihat");

            {
                btnLihat.getStyleClass().add("btn-lihat");

                btnLihat.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());

                    if (payment.getProofFile() == null || payment.getProofFile().isBlank()) {
                        showWarn("File bukti tidak tersedia.");
                        return;
                    }

                    try {

                        File file = new File(payment.getProofFile());

                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            showWarn("File bukti tidak ditemukan.");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showWarn("Gagal membuka file bukti.");
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Payment payment = getTableView().getItems().get(getIndex());

                    if (payment.getProofFile() == null || payment.getProofFile().equalsIgnoreCase("null")) {
                        setGraphic(new Label("-"));
                    } else {
                        setGraphic(btnLihat);
                    }
                }
            }
        });

        colAksi.setCellFactory(col -> new TableCell<Payment, Void>() {

            private final Button btnVerify = new Button("Verify");
            private final Button btnReject = new Button("Reject");

            private final HBox pane = new HBox(10, btnVerify, btnReject);

            {
                btnVerify.getStyleClass().add("btn-verify-row");
                btnReject.getStyleClass().add("btn-reject-row");

                pane.setAlignment(Pos.CENTER_LEFT);

                btnVerify.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());

                    payment.setStatus(Status.PAID);
                    paymentTable.refresh();

                    setInfo("✓ " + payment.getInvoice() + " berhasil diverifikasi.", "#16a34a");
                });

                btnReject.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());

                    payment.setStatus(Status.FAILED);
                    paymentTable.refresh();

                    setInfo("✗ " + payment.getInvoice() + " berhasil ditolak.", "#dc2626");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Payment payment = getTableView().getItems().get(getIndex());

                if (payment.getStatus() == Status.PENDING) {
                    setGraphic(pane);
                } else {
                    setGraphic(new Label("Selesai"));
                }
            }
        });
    }

    // ─── Action Handlers ─────────────────────────────────────────

    @FXML
    public void handleApprove() {
        approvePayment();
    }

    @FXML
    public void handleReject() {
        rejectPayment();
    }

    @FXML
    public void handleExport() {
        showInfo("Export", "Fitur export akan mengunduh data pembayaran sebagai CSV.");
    }

    public void approvePayment() {
        Payment sel = paymentTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarn("Pilih transaksi yang ingin di-approve.");
            return;
        }
        if (sel.getStatus() == Status.PAID) {
            showWarn("Transaksi ini sudah berstatus PAID.");
            return;
        }

        confirm("Approve " + sel.getInvoice() + "?",
                "Member: " + sel.getNamaMember() + "\nNominal: " + sel.getNominalFormatted(),
                () -> {
                    sel.setStatus(Status.PAID);
                    paymentTable.refresh();
                    setInfo("✓ " + sel.getInvoice() + " berhasil di-approve.", "#065f46");
                });
    }

    public void rejectPayment() {
        Payment sel = paymentTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarn("Pilih transaksi yang ingin di-reject.");
            return;
        }
        if (sel.getStatus() == Status.FAILED) {
            showWarn("Transaksi ini sudah berstatus FAILED.");
            return;
        }

        confirm("Reject " + sel.getInvoice() + "?",
                "Member: " + sel.getNamaMember() + "\nNominal: " + sel.getNominalFormatted(),
                () -> {
                    sel.setStatus(Status.FAILED);
                    paymentTable.refresh();
                    setInfo("✗ " + sel.getInvoice() + " berhasil di-reject.", "#991b1b");
                });
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private void confirm(String header, String content, Runnable onOk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK)
                onOk.run();
        });
    }

    private void showWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void setInfo(String text, String color) {
        statusInfo.setText(text);
        statusInfo.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    @FXML
    private void openDashboard(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/dashboard/Dashboard.fxml",
                "Dashboard",
                1280,
                760);
    }

    @FXML
    private void openMembers(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/admin/member/Member.fxml",
                "Members",
                1280,
                760);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/auth/Login.fxml",
                "Login",
                1100,
                720);
    }
    
}

