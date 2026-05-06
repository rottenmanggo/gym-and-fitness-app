package admin.membership;

public class Membership {

    private int    packageId;
    private String packageName;
    private int    durationDays;
    private double price;
    private String description;

    public Membership(int packageId, String packageName, int durationDays,
                      double price, String description) {
        this.packageId    = packageId;
        this.packageName  = packageName;
        this.durationDays = durationDays;
        this.price        = price;
        this.description  = description;
    }

    // Getters
    public int    getPackageId()    { return packageId; }
    public String getPackageName()  { return packageName; }
    public int    getDurationDays() { return durationDays; }
    public double getPrice()        { return price; }
    public String getDescription()  { return description; }

    // Setters
    public void setPackageName(String v)  { this.packageName  = v; }
    public void setDurationDays(int v)    { this.durationDays = v; }
    public void setPrice(double v)        { this.price        = v; }
    public void setDescription(String v)  { this.description  = v; }

    // Format helper
    public String getPriceFormatted() {
        return String.format("Rp %,.0f", price);
    }

    // Status selalu Active karena ini tabel paket, bukan langganan member
    public String getStatus() { return "Active"; }
}