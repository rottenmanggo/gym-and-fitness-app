package admin.member;

import java.util.ArrayList;
import java.util.List;

public class MemberService {

    private static List<Member> members = new ArrayList<>();
    private static int idCounter = 1;

    static {
        members.add(new Member(generateId(), "Budi", "budi@email.com", "0812345678", "Bulanan", "2026-01-01", "Aktif"));
        members.add(
                new Member(generateId(), "Siti", "siti@email.com", "0812345679", "Tahunan", "2026-02-01", "Pending"));
    }

    public static void addMember(Member member) {
        members.add(member);
    }

    public static List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    public static void deleteMember(Member member) {
        members.remove(member);
    }

    public static int generateId() {
        return idCounter++;
    }
}