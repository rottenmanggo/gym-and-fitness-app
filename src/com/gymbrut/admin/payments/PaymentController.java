package com.gymbrut.admin.payments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.gymbrut.admin.payments.Payment.Status;

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
    private TableColumn<Payment, String> colMetode;
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
        loadDummyData();
        setupColumns();

        filteredList = new FilteredList<>(paymentList, p -> true);
        searchField.textProperty().addListener((obs, o, newVal) -> filteredList.setPredicate(p -> {
            if (newVal == null || newVal.isBlank())
                return true;
            String lower = newVal.toLowerCase();
            return p.getInvoice().toLowerCase().contains(lower)
                    || p.getNamaMember().toLowerCase().contains(lower)
                    || p.getMetode().toLowerCase().contains(lower);
        }));

        paymentTable.setItems(filteredList);
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

        // Metode
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metode"));

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

                Label badge = new Label(item.name());
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
}