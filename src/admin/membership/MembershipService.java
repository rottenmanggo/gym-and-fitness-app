package admin.membership;

import config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipService {

    public List<Membership> getAll() {
        List<Membership> list = new ArrayList<>();

        String sql = """
                    SELECT package_id, package_name, duration_days, price, description
                    FROM membership_packages
                    ORDER BY package_id DESC
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Membership(
                        rs.getInt("package_id"),
                        rs.getString("package_name"),
                        rs.getInt("duration_days"),
                        rs.getDouble("price"),
                        rs.getString("description")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(Membership membership) {
        String sql = """
                    INSERT INTO membership_packages (package_name, duration_days, price, description)
                    VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, membership.getPackageName());
            stmt.setInt(2, membership.getDurationDays());
            stmt.setDouble(3, membership.getPrice());
            stmt.setString(4, membership.getDescription());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Membership membership) {
        String sql = """
                    UPDATE membership_packages
                    SET package_name = ?, duration_days = ?, price = ?, description = ?
                    WHERE package_id = ?
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, membership.getPackageName());
            stmt.setInt(2, membership.getDurationDays());
            stmt.setDouble(3, membership.getPrice());
            stmt.setString(4, membership.getDescription());
            stmt.setInt(5, membership.getPackageId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPackageUsed(int packageId) {
        String sql = """
                    SELECT membership_id
                    FROM memberships
                    WHERE package_id = ?
                    LIMIT 1
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, packageId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean delete(int packageId) {
        if (isPackageUsed(packageId)) {
            return false;
        }

        String sql = """
                    DELETE FROM membership_packages
                    WHERE package_id = ?
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, packageId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}