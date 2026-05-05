package com.gymbrut.admin.membership;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Membership {

    private final StringProperty id;
    private final StringProperty nama;
    private final IntegerProperty durasi;
    private final DoubleProperty harga;
    private final StringProperty benefit;
    private final StringProperty status;
    private final ObjectProperty<LocalDate> tanggalMulai;
    private final ObjectProperty<LocalDate> tanggalAkhir;

    public Membership(String id, String nama, int durasi, double harga,
            String benefit, LocalDate tanggalMulai) {
        this.id = new SimpleStringProperty(id);
        this.nama = new SimpleStringProperty(nama);
        this.durasi = new SimpleIntegerProperty(durasi);
        this.harga = new SimpleDoubleProperty(harga);
        this.benefit = new SimpleStringProperty(benefit);
        this.tanggalMulai = new SimpleObjectProperty<>(tanggalMulai);
        this.tanggalAkhir = new SimpleObjectProperty<>(tanggalMulai.plusDays(durasi));
        this.status = new SimpleStringProperty(hitungStatus(tanggalMulai.plusDays(durasi)));
    }

    private String hitungStatus(LocalDate akhir) {
        return LocalDate.now().isAfter(akhir) ? "Inactive" : "Active";
    }

    public void refreshStatus() {
        this.status.set(hitungStatus(this.tanggalAkhir.get()));
    }

    // --- Property getters ---
    public StringProperty idProperty() {
        return id;
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public IntegerProperty durasiProperty() {
        return durasi;
    }

    public DoubleProperty hargaProperty() {
        return harga;
    }

    public StringProperty benefitProperty() {
        return benefit;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public ObjectProperty<LocalDate> tanggalMulaiProperty() {
        return tanggalMulai;
    }

    public ObjectProperty<LocalDate> tanggalAkhirProperty() {
        return tanggalAkhir;
    }

    // --- Plain getters ---
    public String getId() {
        return id.get();
    }

    public String getNama() {
        return nama.get();
    }

    public int getDurasi() {
        return durasi.get();
    }

    public double getHarga() {
        return harga.get();
    }

    public String getBenefit() {
        return benefit.get();
    }

    public String getStatus() {
        return status.get();
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai.get();
    }

    public LocalDate getTanggalAkhir() {
        return tanggalAkhir.get();
    }

    // --- Setters ---
    public void setNama(String value) {
        nama.set(value);
    }

    public void setDurasi(int value) {
        durasi.set(value);
    }

    public void setHarga(double value) {
        harga.set(value);
    }

    public void setBenefit(String value) {
        benefit.set(value);
    }

    // --- Format helpers ---
    public String getHargaFormatted() {
        return String.format("Rp %,.0f", harga.get());
    }

    public String getTanggalAkhirFormatted() {
        return tanggalAkhir.get().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
}