package admin.auth;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private static final List<User> users = new ArrayList<>();

    static {
        users.add(new User("Admin", "admin@gym.com", "admin123", "ADMIN"));
        users.add(new User("Member", "member@gym.com", "member123", "MEMBER"));
    }

    public static User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static boolean register(String name, String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }

        users.add(new User(name, email, password, "MEMBER"));
        return true;
    }
}