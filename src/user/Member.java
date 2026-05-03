package user;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String membership;
    private String joinDate;
    private String status;

    public Member(int id, String name, String email, String phone, String membership, String joinDate, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membership = membership;
        this.joinDate = joinDate;
        this.status = status;
    }

    public int getId() {
        return id;
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

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}