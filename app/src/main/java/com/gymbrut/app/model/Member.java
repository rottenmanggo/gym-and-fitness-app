package com.gymbrut.app.model;

public class Member {
    private final String name;
    private final String email;
    private final String memberId;
    private final String tier;
    private final String expiryDate;

    public Member(String name, String email, String memberId, String tier, String expiryDate) {
        this.name = name;
        this.email = email;
        this.memberId = memberId;
        this.tier = tier;
        this.expiryDate = expiryDate;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMemberId() { return memberId; }
    public String getTier() { return tier; }
    public String getExpiryDate() { return expiryDate; }
}
