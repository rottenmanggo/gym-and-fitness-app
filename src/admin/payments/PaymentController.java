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
import java.nio.file.Files;
import java.nio.file.Path;
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
            private Payment currentPayment;

            {
                lihatButton.getStyleClass().add("table-soft-btn");
                lihatButton.setOnAction(event -> {
                    System.out.println("Tombol Lihat diklik, currentPayment: "
                            + (currentPayment != null ? currentPayment.getInvoice() : "null"));
                    if (currentPayment != null) {
                        openProofFile(currentPayment);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Error",
                                "Data pembayaran tidak tersedia. Silakan refresh halaman.");
                    }
                });
            }

            @Override
            protected void updateItem(String proofFile, boolean empty) {
                super.updateItem(proofFile, empty);
                System.out.println("updateItem colBukti dipanggil - empty: " + empty + ", proofFile: " + proofFile);
                currentPayment = null;

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (proofFile == null || proofFile.isBlank() || proofFile.equalsIgnoreCase("null")) {
                    Label dash = new Label("-");
                    dash.getStyleClass().add("text-soft");
                    setText(null);
                    setGraphic(dash);
                } else {
                    if (getTableRow() != null) {
                        currentPayment = getTableRow().getItem();
                        System.out.println("Set currentPayment untuk bukti: "
                                + (currentPayment != null ? currentPayment.getInvoice() : "null"));
                    }
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

                actionBox.setAlignment(Pos.CENTER_LEFT);

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
        System.out.println("Mencoba membuka file: " + file.getAbsolutePath());
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    System.out.println("Menggunakan Desktop.open()");
                    desktop.open(file);
                    return true;
                }
            }

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                String filePath = file.getAbsolutePath();
                try {
                    System.out.println("Menggunakan cmd start: " + filePath);
                    new ProcessBuilder("cmd", "/c", "start", "", filePath).start();
                    return true;
                } catch (IOException e) {
                    System.out.println("cmd start gagal: " + e.getMessage());
                }

                try {
                    System.out.println("Menggunakan explorer.exe: " + filePath);
                    new ProcessBuilder("explorer.exe", filePath).start();
                    return true;
                } catch (IOException e) {
                    System.out.println("explorer.exe gagal: " + e.getMessage());
                }

                try {
                    System.out.println("Menggunakan rundll32: " + filePath);
                    new ProcessBuilder("cmd", "/c", "rundll32", "url.dll,FileProtocolHandler", filePath).start();
                    return true;
                } catch (IOException e) {
                    System.out.println("rundll32 gagal: " + e.getMessage());
                }
            }

            System.out.println("Semua metode pembukaan file gagal");
            return false;
        } catch (UnsupportedOperationException e) {
            System.out.println("Desktop tidak didukung: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private File findPaymentProofFile(String proofFile) {
        String cleanFile = proofFile.trim();
        System.out.println("Mencari file bukti: " + cleanFile);

        // Kalau database menyimpan path, ambil nama filenya saja.
        cleanFile = cleanFile.replace("\\", "/");
        if (cleanFile.contains("/")) {
            cleanFile = cleanFile.substring(cleanFile.lastIndexOf("/") + 1);
        }

        System.out.println("Nama file bersih: " + cleanFile);

        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path parentDir = currentDir.getParent();

        System.out.println("Current dir: " + currentDir);
        System.out.println("Parent dir: " + parentDir);

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
            System.out.println("Cek bukti pembayaran: " + file.getAbsolutePath() + " - exists: " + Files.exists(path));

            if (Files.exists(path)) {
                System.out.println("File ditemukan: " + file.getAbsolutePath());
                return file;
            }
        }

        System.out.println("File tidak ditemukan di semua lokasi");
        return null;
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