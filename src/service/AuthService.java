package service;

import dao.UserDAO;
import model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password, boolean adminMode) {
        int roleId = adminMode ? 1 : 2;
        return userDAO.login(email, password, roleId);
    }

    public boolean registerMember(String name, String email, String password, String phone) {
        User user = new User();
        user.setRoleId(2);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        return userDAO.register(user);
    }
}
