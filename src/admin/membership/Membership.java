package admin.membership;

import java.text.NumberFormat;
import java.util.Locale;

public class Membership {

    private int packageId;
    private String packageName;
    private int durationDays;
    private double price;
    private String description;

    public Membership(int packageId, String packageName, int durationDays, double price, String description) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.durationDays = durationDays;
        this.price = price;
        this.description = description;
    }

    public int getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceFormatted() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        return format.format(price);
    }

    public String getDescriptionText() {
        if (description == null || description.isBlank()) {
            return "Belum ada deskripsi paket.";
        }

        return description;
    }
}