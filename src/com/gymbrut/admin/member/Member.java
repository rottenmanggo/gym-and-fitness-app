package com.gymbrut.admin.member;

public class Member {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String membership;
    private String status;

    public Member(int id, String name, String email, String phone, String membership, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membership = membership;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}