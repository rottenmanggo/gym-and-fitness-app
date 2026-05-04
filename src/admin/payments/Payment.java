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
    private final StringProperty metode;
    private final ObjectProperty<Status> status;
    private final ObjectProperty<LocalDate> tanggal;
    private final StringProperty membershipId;

    public Payment(String invoice, String namaMember, double nominal, String metode,
            Status status, LocalDate tanggal, String membershipId) {
        this.invoice = new SimpleStringProperty(invoice);
        this.namaMember = new SimpleStringProperty(namaMember);
        this.nominal = new SimpleDoubleProperty(nominal);
        this.metode = new SimpleStringProperty(metode);
        this.status = new SimpleObjectProperty<>(status);
        this.tanggal = new SimpleObjectProperty<>(tanggal);
        this.membershipId = new SimpleStringProperty(membershipId);
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

    public StringProperty metodeProperty() {
        return metode;
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public ObjectProperty<LocalDate> tanggalProperty() {
        return tanggal;
    }

    public StringProperty membershipIdProperty() {
        return membershipId;
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

    public String getMetode() {
        return metode.get();
    }

    public Status getStatus() {
        return status.get();
    }

    public LocalDate getTanggal() {
        return tanggal.get();
    }

    public String getMembershipId() {
        return membershipId.get();
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