package auth;

import config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public User login(String email, String password) {
        String sql = "SELECT user_id, name, email, role, phone, password FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                // DB kamu masih plain text
                if (password.equals(dbPassword)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("phone"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}