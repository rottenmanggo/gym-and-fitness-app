package admin.payments;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Payment {

    public enum Status {
        PAID, PENDING, FAILED
    }

    private final StringProperty invoice;
    private final StringProperty namaMember;
    private final DoubleProperty nominal;
    private final StringProperty paket;
    private final ObjectProperty<Status> status;
    private final ObjectProperty<LocalDate> tanggal;
    private final StringProperty proofFile;  


    public Payment(String invoice, String namaMember, double nominal, String paket,
            Status status, LocalDate tanggal, String proofFile) {
        this.invoice = new SimpleStringProperty(invoice);
        this.namaMember = new SimpleStringProperty(namaMember);
        this.nominal = new SimpleDoubleProperty(nominal);
        this.paket = new SimpleStringProperty(paket);
        this.status = new SimpleObjectProperty<>(status);
        this.tanggal = new SimpleObjectProperty<>(tanggal);
        this.proofFile = new SimpleStringProperty(proofFile);
    }

    // --- Property getters ---
    public StringProperty invoiceProperty() {
        return invoice;
    }

    public StringProperty namaMemberProperty() {
        return namaMember;
    }

    public DoubleProperty nominalProperty() {
        return nominal;
    }

    public StringProperty paketProperty() {
        return paket;
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public ObjectProperty<LocalDate> tanggalProperty() {
        return tanggal;
    }

    public StringProperty proofFileProperty() {
        return proofFile;
    }

    // --- Plain getters ---
    public String getInvoice() {
        return invoice.get();
    }

    public String getNamaMember() {
        return namaMember.get();
    }

    public double getNominal() {
        return nominal.get();
    }

    public String getPaket() {
        return paket.get();
    }

    public Status getStatus() {
        return status.get();
    }

    public LocalDate getTanggal() {
        return tanggal.get();
    }

    public String getProofFile() {
        return proofFile.get();
    }

    // --- Setter ---
    public void setStatus(Status v) {
        status.set(v);
    }

    // --- Format helpers ---
    public String getNominalFormatted() {
        return String.format("Rp %,.0f", nominal.get());
    }

    public String getTanggalFormatted() {
        return tanggal.get().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
}