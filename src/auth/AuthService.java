package auth;

public class AuthService {

    public User login(String email, String password) {
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            return new User(1, "Admin", "admin@gmail.com", "admin");
        }

        if (email.equals("member@gmail.com") && password.equals("member123")) {
            return new User(2, "Member", "member@gmail.com", "member");
        }

        return null;
    }
}