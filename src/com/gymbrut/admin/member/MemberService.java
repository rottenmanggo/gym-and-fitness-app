package com.gymbrut.admin.member;

import com.gymbrut.config.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MemberService {

    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();

        String sql = """
                SELECT
                    u.user_id AS id,
                    u.name,
                    u.email,
                    u.phone,
                    COALESCE(mp.package_name, '-') AS membership,
                    COALESCE(m.status, 'Belum Aktif') AS status
                FROM users u
                LEFT JOIN memberships m ON u.user_id = m.user_id
                LEFT JOIN membership_packages mp ON m.package_id = mp.package_id
                WHERE u.role = 'member'
                ORDER BY u.user_id DESC
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("membership"),
                        rs.getString("status")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return members;
    }

    public boolean addMember(String name, String email, String phone, String membership, String status) {
        String insertUserSql = """
                INSERT INTO users (name, email, password, role, phone)
                VALUES (?, ?, ?, 'member', ?)
                """;

        String insertMembershipSql = """
                INSERT INTO memberships (user_id, package_id, start_date, end_date, status)
                VALUES (?,
                    (SELECT package_id FROM membership_packages WHERE package_name = ?),
                    CURDATE(),
                    DATE_ADD(CURDATE(), INTERVAL (
                        SELECT duration_days FROM membership_packages WHERE package_name = ?
                    ) DAY),
                    ?
                )
                """;

        String defaultPassword = "member123";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, name);
                userStmt.setString(2, email);
                userStmt.setString(3, defaultPassword);
                userStmt.setString(4, phone);
                userStmt.executeUpdate();

                ResultSet keys = userStmt.getGeneratedKeys();

                if (!keys.next()) {
                    conn.rollback();
                    return false;
                }

                int userId = keys.getInt(1);

                try (PreparedStatement membershipStmt = conn.prepareStatement(insertMembershipSql)) {
                    membershipStmt.setInt(1, userId);
                    membershipStmt.setString(2, membership);
                    membershipStmt.setString(3, membership);
                    membershipStmt.setString(4, status);
                    membershipStmt.executeUpdate();
                }

                conn.commit();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMember(Member selectedMember, String name, String email, String phone, String membership,
            String status) {
        String updateUserSql = """
                UPDATE users
                SET name = ?, email = ?, phone = ?
                WHERE user_id = ? AND role = 'member'
                """;

        String updateMembershipSql = """
                UPDATE memberships
                SET package_id = (SELECT package_id FROM membership_packages WHERE package_name = ?),
                    status = ?
                WHERE user_id = ?
                """;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(updateUserSql);
                    PreparedStatement membershipStmt = conn.prepareStatement(updateMembershipSql)) {

                userStmt.setString(1, name);
                userStmt.setString(2, email);
                userStmt.setString(3, phone);
                userStmt.setInt(4, selectedMember.getId());
                userStmt.executeUpdate();

                membershipStmt.setString(1, membership);
                membershipStmt.setString(2, status);
                membershipStmt.setInt(3, selectedMember.getId());
                membershipStmt.executeUpdate();

                conn.commit();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMember(Member member) {
        String deleteMembershipSql = "DELETE FROM memberships WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE user_id = ? AND role = 'member'";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement membershipStmt = conn.prepareStatement(deleteMembershipSql);
                    PreparedStatement userStmt = conn.prepareStatement(deleteUserSql)) {

                // 1. Hapus membership dulu
                membershipStmt.setInt(1, member.getId());
                membershipStmt.executeUpdate();

                // 2. Hapus user
                userStmt.setInt(1, member.getId());
                int affectedRows = userStmt.executeUpdate();

                conn.commit();
                return affectedRows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}