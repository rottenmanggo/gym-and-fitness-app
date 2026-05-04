package admin.member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MemberService {

    private static final ObservableList<Member> members = FXCollections.observableArrayList();

    static {
        members.add(new Member(1, "Andi Pratama", "andi@gmail.com", "081234567890", "Gold", "Aktif"));
        members.add(new Member(2, "Siti Aminah", "siti@gmail.com", "082345678901", "Silver", "Aktif"));
        members.add(new Member(3, "Budi Santoso", "budi@gmail.com", "083456789012", "Bronze", "Pending"));
        members.add(new Member(4, "Rina Wijaya", "rina@gmail.com", "084567890123", "Gold", "Aktif"));
    }

    public ObservableList<Member> getAllMembers() {
        return members;
    }

    public void addMember(String name, String email, String phone, String membership, String status) {
        int nextId = members.size() + 1;
        members.add(new Member(nextId, name, email, phone, membership, status));
    }

    public void updateMember(Member member, String name, String email, String phone, String membership, String status) {
        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        member.setMembership(membership);
        member.setStatus(status);
    }

    public void deleteMember(Member member) {
        members.remove(member);
    }
}