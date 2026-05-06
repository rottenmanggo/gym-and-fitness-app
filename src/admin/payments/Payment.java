package admin.payments;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Payment {

    private int paymentId;
    private int membershipId;
    private int userId;
    private int durationDays;

    private String invoice;
    private String memberName;
    private String email;
    private String packageName;
    private double amount;
    private String status;
    private LocalDate paymentDate;
    private String proofFile;

    public Payment(
            int paymentId,
            int membershipId,
            int userId,
            int durationDays,
            String memberName,
            String email,
            String packageName,
            double amount,
            String status,
            LocalDate paymentDate,
            String proofFile) {
        this.paymentId = paymentId;
        this.membershipId = membershipId;
        this.userId = userId;
        this.durationDays = durationDays;
        this.invoice = String.format("INV-%05d", paymentId);
        this.memberName = memberName;
        this.email = email;
        this.packageName = packageName;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.proofFile = proofFile;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public int getUserId() {
        return userId;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getPackageName() {
        return packageName;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getProofFile() {
        return proofFile;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmountFormatted() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        return format.format(amount);
    }

    public String getPaymentDateFormatted() {
        if (paymentDate == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return paymentDate.format(formatter);
    }

    public String getStatusLabel() {
        if (status == null) {
            return "-";
        }

        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
}