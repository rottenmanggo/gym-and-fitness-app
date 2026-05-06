package member.payments;

public class Payment {

    private int id;
    private double amount;
    private String status;

    public Payment(int id, double amount, String status) {
        this.id = id;
        this.amount = amount;
        this.status = status;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}