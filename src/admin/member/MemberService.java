package admin.member;

import config.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MemberService {

    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();

        String sql = """
                    SELECT
                        u.user_id AS id,
                        u.name,
                        u.email,
                        u.phone,
                        u.created_at,
                        COALESCE(m.package_id, 0) AS package_id,
                        COALESCE(mp.package_name, 'Belum pilih paket') AS membership,
                        m.start_date,
                        m.end_date,
                        COALESCE(m.status, 'pending') AS status
                    FROM users u
                    LEFT JOIN memberships m
                        ON m.membership_id = (
                            SELECT m2.membership_id
                            FROM memberships m2
                            WHERE m2.user_id = u.user_id
                            ORDER BY m2.start_date DESC, m2.membership_id DESC
                            LIMIT 1
                        )
                    LEFT JOIN membership_packages mp
                        ON m.package_id = mp.package_id
                    WHERE u.role = 'member'
                    ORDER BY u.created_at DESC, u.user_id DESC
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Date createdAt = rs.getDate("created_at");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");

                String rawStartDate = startDate != null ? startDate.toString() : null;
                String rawEndDate = endDate != null ? endDate.toString() : null;

                String joinDate;
                if (startDate != null) {
                    joinDate = formatDate(startDate);
                } else if (createdAt != null) {
                    joinDate = formatDate(createdAt);
                } else {
                    joinDate = "-";
                }

                members.add(new Member(
                        rs.getInt("id"),
                        rs.getInt("package_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("membership"),
                        joinDate,
                        rawStartDate,
                        rawEndDate,
                        rs.getString("status")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return members;
    }

    public ObservableList<String> getPackageNames() {
        ObservableList<String> packages = FXCollections.observableArrayList();

        String sql = """
                    SELECT package_name
                    FROM membership_packages
                    ORDER BY price ASC
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                packages.add(rs.getString("package_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packages;
    }

    public boolean addMember(
            String name,
            String email,
            String phone,
            String password,
            String membership,
            String startDate) {
        String checkEmailSql = """
                    SELECT user_id FROM users WHERE email = ? LIMIT 1
                """;

        String packageSql = """
                    SELECT package_id, duration_days, price
                    FROM membership_packages
                    WHERE package_name = ?
                    LIMIT 1
                """;

        String insertUserSql = """
                    INSERT INTO users (name, email, phone, password, role, created_at)
                    VALUES (?, ?, ?, ?, 'member', NOW())
                """;

        String insertMembershipSql = """
                    INSERT INTO memberships (user_id, package_id, start_date, end_date, status)
                    VALUES (?, ?, ?, DATE_ADD(?, INTERVAL ? DAY), 'pending')
                """;

        String insertPaymentSql = """
                    INSERT INTO payments (membership_id, amount, status, payment_date)
                    VALUES (?, ?, 'pending', NOW())
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                    checkStmt.setString(1, email);

                    try (ResultSet checkRs = checkStmt.executeQuery()) {
                        if (checkRs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                int packageId;
                int durationDays;
                double price;

                try (PreparedStatement packageStmt = conn.prepareStatement(packageSql)) {
                    packageStmt.setString(1, membership);

                    try (ResultSet packageRs = packageStmt.executeQuery()) {
                        if (!packageRs.next()) {
                            conn.rollback();
                            return false;
                        }

                        packageId = packageRs.getInt("package_id");
                        durationDays = packageRs.getInt("duration_days");
                        price = packageRs.getDouble("price");
                    }
                }

                int userId;

                try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    userStmt.setString(1, name);
                    userStmt.setString(2, email);
                    userStmt.setString(3, phone);
                    userStmt.setString(4, password);
                    userStmt.executeUpdate();

                    try (ResultSet keys = userStmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }

                        userId = keys.getInt(1);
                    }
                }

                int membershipId;

                try (PreparedStatement membershipStmt = conn.prepareStatement(insertMembershipSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    membershipStmt.setInt(1, userId);
                    membershipStmt.setInt(2, packageId);
                    membershipStmt.setString(3, startDate);
                    membershipStmt.setString(4, startDate);
                    membershipStmt.setInt(5, durationDays);
                    membershipStmt.executeUpdate();

                    try (ResultSet keys = membershipStmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }

                        membershipId = keys.getInt(1);
                    }
                }

                try (PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentSql)) {
                    paymentStmt.setInt(1, membershipId);
                    paymentStmt.setDouble(2, price);
                    paymentStmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMember(
            Member selectedMember,
            String name,
            String email,
            String phone,
            String password,
            String membership,
            String startDate,
            String status) {
        String checkEmailSql = """
                    SELECT user_id
                    FROM users
                    WHERE email = ? AND user_id != ?
                    LIMIT 1
                """;

        String packageSql = """
                    SELECT package_id, duration_days
                    FROM membership_packages
                    WHERE package_name = ?
                    LIMIT 1
                """;

        String updateUserWithoutPasswordSql = """
                    UPDATE users
                    SET name = ?, email = ?, phone = ?
                    WHERE user_id = ? AND role = 'member'
                """;

        String updateUserWithPasswordSql = """
                    UPDATE users
                    SET name = ?, email = ?, phone = ?, password = ?
                    WHERE user_id = ? AND role = 'member'
                """;

        String updateMembershipSql = """
                    UPDATE memberships
                    SET package_id = ?,
                        start_date = ?,
                        end_date = DATE_ADD(?, INTERVAL ? DAY),
                        status = ?
                    WHERE membership_id = (
                        SELECT membership_id FROM (
                            SELECT membership_id
                            FROM memberships
                            WHERE user_id = ?
                            ORDER BY start_date DESC, membership_id DESC
                            LIMIT 1
                        ) latest
                    )
                """;

        String insertMembershipSql = """
                    INSERT INTO memberships (user_id, package_id, start_date, end_date, status)
                    VALUES (?, ?, ?, DATE_ADD(?, INTERVAL ? DAY), ?)
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                    checkStmt.setString(1, email);
                    checkStmt.setInt(2, selectedMember.getId());

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                int packageId;
                int durationDays;

                try (PreparedStatement packageStmt = conn.prepareStatement(packageSql)) {
                    packageStmt.setString(1, membership);

                    try (ResultSet rs = packageStmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        packageId = rs.getInt("package_id");
                        durationDays = rs.getInt("duration_days");
                    }
                }

                if (password == null || password.isBlank()) {
                    try (PreparedStatement userStmt = conn.prepareStatement(updateUserWithoutPasswordSql)) {
                        userStmt.setString(1, name);
                        userStmt.setString(2, email);
                        userStmt.setString(3, phone);
                        userStmt.setInt(4, selectedMember.getId());
                        userStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement userStmt = conn.prepareStatement(updateUserWithPasswordSql)) {
                        userStmt.setString(1, name);
                        userStmt.setString(2, email);
                        userStmt.setString(3, phone);
                        userStmt.setString(4, password);
                        userStmt.setInt(5, selectedMember.getId());
                        userStmt.executeUpdate();
                    }
                }

                try (PreparedStatement membershipStmt = conn.prepareStatement(updateMembershipSql)) {
                    membershipStmt.setInt(1, packageId);
                    membershipStmt.setString(2, startDate);
                    membershipStmt.setString(3, startDate);
                    membershipStmt.setInt(4, durationDays);
                    membershipStmt.setString(5, status);
                    membershipStmt.setInt(6, selectedMember.getId());

                    int affected = membershipStmt.executeUpdate();

                    if (affected == 0) {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertMembershipSql)) {
                            insertStmt.setInt(1, selectedMember.getId());
                            insertStmt.setInt(2, packageId);
                            insertStmt.setString(3, startDate);
                            insertStmt.setString(4, startDate);
                            insertStmt.setInt(5, durationDays);
                            insertStmt.setString(6, status);
                            insertStmt.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMember(Member member) {
        String deletePaymentsSql = """
                    DELETE FROM payments
                    WHERE membership_id IN (
                        SELECT membership_id FROM memberships WHERE user_id = ?
                    )
                """;

        String deleteMembershipSql = """
                    DELETE FROM memberships WHERE user_id = ?
                """;

        String deleteCheckinsSql = """
                    DELETE FROM checkins WHERE user_id = ?
                """;

        String deleteProgressSql = """
                    DELETE FROM progress_logs WHERE user_id = ?
                """;

        String deleteNotificationsSql = """
                    DELETE FROM notifications WHERE user_id = ?
                """;

        String deleteUserSql = """
                    DELETE FROM users WHERE user_id = ? AND role = 'member'
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(deletePaymentsSql)) {
                    stmt.setInt(1, member.getId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteMembershipSql)) {
                    stmt.setInt(1, member.getId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteCheckinsSql)) {
                    stmt.setInt(1, member.getId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteProgressSql)) {
                    stmt.setInt(1, member.getId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteNotificationsSql)) {
                    stmt.setInt(1, member.getId());
                    stmt.executeUpdate();
                }

                int affectedRows;

                try (PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
                    stmt.setInt(1, member.getId());
                    affectedRows = stmt.executeUpdate();
                }

                conn.commit();
                return affectedRows > 0;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String formatDate(Date date) {
        if (date == null)
            return "-";
        LocalDate localDate = date.toLocalDate();
        return localDate.format(displayFormatter);
    }
}