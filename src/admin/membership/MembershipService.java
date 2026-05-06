package admin.membership;

import config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipService {

    // ─── Ambil semua paket ───────────────────────────────────────
    public List<Membership> getAll() {
        List<Membership> list = new ArrayList<>();
        String sql = "SELECT * FROM membership_packages ORDER BY package_id";
        try (Connection conn = Database.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Membership(
                    rs.getInt("package_id"),
                    rs.getString("package_name"),
                    rs.getInt("duration_days"),
                    rs.getDouble("price"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─── Tambah paket baru ───────────────────────────────────────
    public boolean insert(Membership m) {
        String sql = "INSERT INTO membership_packages (package_name, duration_days, price, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getPackageName());
            ps.setInt   (2, m.getDurationDays());
            ps.setDouble(3, m.getPrice());
            ps.setString(4, m.getDescription());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Update paket ────────────────────────────────────────────
    public boolean update(Membership m) {
        String sql = "UPDATE membership_packages SET package_name=?, duration_days=?, price=?, description=? WHERE package_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getPackageName());
            ps.setInt   (2, m.getDurationDays());
            ps.setDouble(3, m.getPrice());
            ps.setString(4, m.getDescription());
            ps.setInt   (5, m.getPackageId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Hapus paket ─────────────────────────────────────────────
    public boolean delete(int packageId) {
        String sql = "DELETE FROM membership_packages WHERE package_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, packageId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}