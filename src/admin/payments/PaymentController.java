package admin.payments;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PaymentController {

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    private TableColumn<Payment, String> colInvoice;
    @FXML
    private TableColumn<Payment, String> colNama;
    @FXML
    private TableColumn<Payment, String> colPaket;
    @FXML
    private TableColumn<Payment, String> colNominal;
    @FXML
    private TableColumn<Payment, String> colStatus;
    @FXML
    private TableColumn<Payment, String> colTanggal;
    @FXML
    private TableColumn<Payment, String> colBukti;
    @FXML
    private TableColumn<Payment, Void> colAksi;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalDataLabel;
    @FXML
    private Label statusInfo;

    private final PaymentsService service = new PaymentsService();

    private final ObservableList<Payment> paymentList = FXCollections.observableArrayList();
    private FilteredList<Payment> filteredList;

    @FXML
    public void initialize() {
        setupTable();
        loadPayments();

        filteredList = new FilteredList<>(paymentList, payment -> true);
        paymentTable.setItems(filteredList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    private void setupTable() {
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colInvoice.setPrefWidth(120);
        colNama.setPrefWidth(200);
        colPaket.setPrefWidth(110);
        colNominal.setPrefWidth(115);
        colStatus.setPrefWidth(110);
        colTanggal.setPrefWidth(125);
        colBukti.setPrefWidth(90);
        colAksi.setPrefWidth(160);

        colInvoice.setMinWidth(95);
        colNama.setMinWidth(160);
        colPaket.setMinWidth(90);
        colNominal.setMinWidth(95);
        colStatus.setMinWidth(95);
        colTanggal.setMinWidth(105);
        colBukti.setMinWidth(75);
        colAksi.setMinWidth(145);

        colAksi.setText("Aksi");
        colAksi.setResizable(false);

        colInvoice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInvoice()));
        colPaket.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getPackageName())));
        colNominal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAmountFormatted()));
        colTanggal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentDateFormatted()));

        colInvoice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String invoice, boolean empty) {
                super.updateItem(invoice, empty);

                if (empty || invoice == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label label = new Label(invoice);
                label.getStyleClass().add("invoice-text");

                setText(null);
                setGraphic(label);
            }
        });

        colNama.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getMemberName())));

        colNama.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);

                if (empty || name == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Payment payment = getTableView().getItems().get(getIndex());

                Label nameLabel = new Label(payment.getMemberName());
                nameLabel.getStyleClass().add("member-name");

                Label emailLabel = new Label(payment.getEmail());
                emailLabel.getStyleClass().add("member-email");

                javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(4, nameLabel, emailLabel);

                setText(null);
                setGraphic(box);
            }
        });

        colStatus.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getStatus())));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(capitalize(status));
                badge.getStyleClass().add("status-badge");
                badge.setMinWidth(82);
                badge.setPrefWidth(82);
                badge.setAlignment(Pos.CENTER);

                switch (status.toLowerCase()) {
                    case "verified" -> badge.getStyleClass().add("status-verified");
                    case "pending" -> badge.getStyleClass().add("status-pending");
                    case "rejected" -> badge.getStyleClass().add("status-rejected");
                    default -> badge.getStyleClass().add("status-default");
                }

                setText(null);
                setGraphic(badge);
            }
        });

        colBukti.setCellValueFactory(data -> new SimpleStringProperty(safe(data.getValue().getProofFile())));

        colBukti.setCellFactory(column -> new TableCell<>() {
            private final Button lihatButton = new Button("Lihat");

            {
                lihatButton.getStyleClass().add("table-soft-btn");

                lihatButton.setMinWidth(64);
                lihatButton.setPrefWidth(64);
                lihatButton.setMaxWidth(64);

                lihatButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    openProofFile(payment);
                });
            }

            @Override
            protected void updateItem(String proofFile, boolean empty) {
                super.updateItem(proofFile, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (proofFile == null || proofFile.isBlank() || proofFile.equalsIgnoreCase("null")) {
                    setText("-");
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(lihatButton);
                }
            }
        });

        colAksi.setCellFactory(column -> new TableCell<>() {
            private final Button verifyButton = new Button("Verify");
            private final Button rejectButton = new Button("Reject");
            private final HBox actionBox = new HBox(8, verifyButton, rejectButton);
            private Payment currentPayment;

            {
                verifyButton.getStyleClass().add("table-soft-btn");
                rejectButton.getStyleClass().add("table-danger-btn");

                verifyButton.setMinWidth(58);
                verifyButton.setPrefWidth(58);

                rejectButton.setMinWidth(58);
                rejectButton.setPrefWidth(58);

                actionBox.setSpacing(6);
                actionBox.setAlignment(Pos.CENTER);
                actionBox.setMinWidth(125);
                actionBox.setPrefWidth(125);

                verifyButton.setOnAction(event -> {
                    if (currentPayment != null) {
                        handleVerify(currentPayment);
                    }
                });

                rejectButton.setOnAction(event -> {
                    if (currentPayment != null) {
                        handleReject(currentPayment);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                currentPayment = null;

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (getTableRow() != null) {
                    currentPayment = getTableRow().getItem();
                }

                if (currentPayment != null && "pending".equalsIgnoreCase(currentPayment.getStatus())) {
                    setText(null);
                    setGraphic(actionBox);
                } else {
                    Label done = new Label("Selesai");
                    done.getStyleClass().add("text-soft");
                    done.setMinWidth(70);
                    done.setPrefWidth(70);
                    done.setAlignment(Pos.CENTER);

                    setText(null);
                    setGraphic(done);
                }
            }
        });
    }

    private void loadPayments() {
        paymentList.clear();

        List<Payment> data = service.getAllPayments();
        paymentList.addAll(data);

        updateTotalLabel(paymentList.size());
        statusInfo.setText("");
    }

    private void handleSearch(String keyword) {
        String search = keyword == null ? "" : keyword.toLowerCase().trim();

        filteredList.setPredicate(payment -> {
            if (search.isEmpty()) {
                return true;
            }

            return safe(payment.getInvoice()).toLowerCase().contains(search)
                    || safe(payment.getMemberName()).toLowerCase().contains(search)
                    || safe(payment.getEmail()).toLowerCase().contains(search)
                    || safe(payment.getPackageName()).toLowerCase().contains(search)
                    || safe(payment.getStatus()).toLowerCase().contains(search)
                    || payment.getAmountFormatted().toLowerCase().contains(search);
        });

        updateTotalLabel(filteredList.size());
    }

    @FXML
    private void handleReset() {
        searchField.clear();

        if (filteredList != null) {
            filteredList.setPredicate(payment -> true);
            updateTotalLabel(filteredList.size());
        }
    }

    private void handleVerify(Payment payment) {
        if (payment == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Payment", "Pilih pembayaran yang ingin diverifikasi.");
            return;
        }

        if (!"pending".equalsIgnoreCase(payment.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Tidak Bisa", "Pembayaran ini sudah selesai diproses.");
            return;
        }

        if (payment.getProofFile() == null || payment.getProofFile().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Tidak Bisa", "Payment belum memiliki bukti pembayaran.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Verifikasi Pembayaran");
        confirm.setHeaderText("Verifikasi " + payment.getInvoice() + "?");
        confirm.setContentText("Member: " + payment.getMemberName() + "\nNominal: " + payment.getAmountFormatted());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = service.verifyPayment(payment);

            if (success) {
                setInfo("Payment berhasil diverifikasi.", "success");
                loadPayments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal",
                        "Payment gagal diverifikasi. Pastikan bukti pembayaran tersedia.");
            }
        }
    }

    private void handleReject(Payment payment) {
        if (payment == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Payment", "Pilih pembayaran yang ingin ditolak.");
            return;
        }

        if (!"pending".equalsIgnoreCase(payment.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Tidak Bisa", "Pembayaran ini sudah selesai diproses.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Tolak Pembayaran");
        confirm.setHeaderText("Tolak " + payment.getInvoice() + "?");
        confirm.setContentText("Member: " + payment.getMemberName() + "\nNominal: " + payment.getAmountFormatted());

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = service.rejectPayment(payment);

            if (success) {
                setInfo("Payment berhasil ditolak.", "danger");
                loadPayments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Payment gagal ditolak.");
            }
        }
    }

    private void openProofFile(Payment payment) {
        System.out
                .println("openProofFile dipanggil untuk payment: " + (payment != null ? payment.getInvoice() : "null"));
        if (payment == null || payment.getProofFile() == null || payment.getProofFile().isBlank()) {
            System.out.println("Payment atau proofFile null/blank");
            showAlert(Alert.AlertType.WARNING, "Bukti Kosong", "File bukti pembayaran tidak tersedia.");
            return;
        }

        String proofFile = payment.getProofFile();
        System.out.println("Proof file dari database: " + proofFile);

        try {
            File file = findPaymentProofFile(proofFile);

            if (file == null || !file.exists()) {
                System.out.println("File tidak ditemukan");
                showAlert(
                        Alert.AlertType.WARNING,
                        "File Tidak Ditemukan",
                        "File bukti tidak ditemukan.\n\nNama file: " + proofFile
                                + "\n\nPastikan file ada di folder:\n"
                                + "gym-and-fitness-app/uploads/payments/");
                return;
            }

            System.out.println("Membuka bukti pembayaran: " + file.getAbsolutePath());

            if (openFile(file)) {
                System.out.println("File berhasil dibuka");
                return;
            }

            System.out.println("Semua metode pembukaan gagal");
            showAlert(Alert.AlertType.ERROR, "Gagal",
                    "Gagal membuka file bukti pembayaran. Periksa asosiasi file di sistem.");
        } catch (Exception e) {
            System.out.println("Exception di openProofFile: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal membuka file bukti pembayaran. Error: " + e.getMessage());
        }
    }

    private boolean openFile(File file) {

        try {

            String absolutePath = file.getAbsolutePath();

            System.out.println("Membuka file:");
            System.out.println(absolutePath);

            if (System.getProperty("os.name").toLowerCase().contains("win")) {

                Runtime.getRuntime().exec(
                        new String[] {
                                "rundll32",
                                "url.dll,FileProtocolHandler",
                                absolutePath
                        });

                return true;
            }

            if (Desktop.isDesktopSupported()) {

                Desktop.getDesktop().open(file);

                return true;
            }

            return false;

        } catch (Exception e) {

            System.out.println("ERROR OPEN FILE:");
            e.printStackTrace();

            return false;
        }
    }

    private File findPaymentProofFile(String proofFile) {

        if (proofFile == null || proofFile.isBlank()) {
            return null;
        }

        String cleanFile = proofFile.trim();

        cleanFile = cleanFile.replace("\\", "/");

        if (cleanFile.contains("/")) {
            cleanFile = cleanFile.substring(cleanFile.lastIndexOf("/") + 1);
        }

        File file = new File("uploads/payments/" + cleanFile);

        System.out.println("PATH FILE:");
        System.out.println(file.getAbsolutePath());
        System.out.println("EXISTS: " + file.exists());

        return file.exists() ? file : null;
    }

    private void updateTotalLabel(int total) {
        totalDataLabel.setText(total + " data");
    }

    private void setInfo(String message, String type) {
        statusInfo.setText(message);

        if ("success".equalsIgnoreCase(type)) {
            statusInfo.getStyleClass().removeAll("status-info-danger");
            statusInfo.getStyleClass().add("status-info-success");
        } else {
            statusInfo.getStyleClass().removeAll("status-info-success");
            statusInfo.getStyleClass().add("status-info-danger");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
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