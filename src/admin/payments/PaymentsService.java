package admin.payments;

import config.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentsService {

    private static final int ADMIN_ID = 1;

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();

        String sql = """
                    SELECT
                        p.payment_id,
                        p.membership_id,
                        p.amount,
                        p.payment_date,
                        p.proof_file,
                        p.status,
                        m.user_id,
                        m.start_date,
                        m.end_date,
                        mp.package_name,
                        mp.duration_days,
                        u.name AS member_name,
                        u.email
                    FROM payments p
                    JOIN memberships m ON p.membership_id = m.membership_id
                    JOIN users u ON m.user_id = u.user_id
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    ORDER BY p.payment_date DESC, p.payment_id DESC
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Timestamp paymentTimestamp = rs.getTimestamp("payment_date");

                LocalDate paymentDate = null;
                if (paymentTimestamp != null) {
                    paymentDate = paymentTimestamp.toLocalDateTime().toLocalDate();
                }

                payments.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("membership_id"),
                        rs.getInt("user_id"),
                        rs.getInt("duration_days"),
                        rs.getString("member_name"),
                        rs.getString("email"),
                        rs.getString("package_name"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        paymentDate,
                        rs.getString("proof_file")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payments;
    }

    public boolean verifyPayment(Payment payment) {
        if (payment == null) {
            return false;
        }

        if (payment.getProofFile() == null || payment.getProofFile().isBlank()) {
            return false;
        }

        String updatePaymentSql = """
                    UPDATE payments
                    SET
                        status = 'verified',
                        verified_by = ?,
                        payment_date = NOW()
                    WHERE payment_id = ?
                """;

        String updateMembershipSql = """
                    UPDATE memberships
                    SET
                        status = 'aktif',
                        start_date = ?,
                        end_date = ?
                    WHERE membership_id = ?
                """;

        String insertNotificationSql = """
                    INSERT INTO notifications (user_id, title, message, type)
                    VALUES (?, ?, ?, ?)
                """;

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(payment.getDurationDays());

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(updatePaymentSql)) {
                    stmt.setInt(1, ADMIN_ID);
                    stmt.setInt(2, payment.getPaymentId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(updateMembershipSql)) {
                    stmt.setDate(1, Date.valueOf(startDate));
                    stmt.setDate(2, Date.valueOf(endDate));
                    stmt.setInt(3, payment.getMembershipId());
                    stmt.executeUpdate();
                }

                String title = "Pembayaran Diverifikasi";
                String message = "Pembayaran paket "
                        + payment.getPackageName()
                        + " sudah diverifikasi admin. Membership kamu sekarang aktif sampai "
                        + endDate
                        + ".";

                try (PreparedStatement stmt = conn.prepareStatement(insertNotificationSql)) {
                    stmt.setInt(1, payment.getUserId());
                    stmt.setString(2, title);
                    stmt.setString(3, message);
                    stmt.setString(4, "payment");
                    stmt.executeUpdate();
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

    public boolean rejectPayment(Payment payment) {
        if (payment == null) {
            return false;
        }

        String updatePaymentSql = """
                    UPDATE payments
                    SET status = 'rejected'
                    WHERE payment_id = ?
                """;

        String updateMembershipSql = """
                    UPDATE memberships
                    SET status = 'pending'
                    WHERE membership_id = ?
                """;

        String insertNotificationSql = """
                    INSERT INTO notifications (user_id, title, message, type)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(updatePaymentSql)) {
                    stmt.setInt(1, payment.getPaymentId());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(updateMembershipSql)) {
                    stmt.setInt(1, payment.getMembershipId());
                    stmt.executeUpdate();
                }

                String title = "Pembayaran Ditolak";
                String message = "Pembayaran paket "
                        + payment.getPackageName()
                        + " ditolak admin. Silakan upload ulang bukti pembayaran di menu Payments.";

                try (PreparedStatement stmt = conn.prepareStatement(insertNotificationSql)) {
                    stmt.setInt(1, payment.getUserId());
                    stmt.setString(2, title);
                    stmt.setString(3, message);
                    stmt.setString(4, "payment");
                    stmt.executeUpdate();
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
}