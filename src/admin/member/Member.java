package admin.member;

public class Member {

    private int id;
    private int packageId;
    private String name;
    private String email;
    private String phone;
    private String membership;
    private String joinDate;
    private String startDate;
    private String endDate;
    private String status;

    public Member(
            int id,
            int packageId,
            String name,
            String email,
            String phone,
            String membership,
            String joinDate,
            String startDate,
            String endDate,
            String status) {
        this.id = id;
        this.packageId = packageId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membership = membership;
        this.joinDate = joinDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getPackageId() {
        return packageId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMembership() {
        return membership;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}