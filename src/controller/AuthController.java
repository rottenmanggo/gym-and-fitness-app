package controller;

import model.User;
import service.AuthService;

public class AuthController {
    private final AuthService authService = new AuthService();

    public User login(String email, String password, boolean adminMode) {
        return authService.login(email, password, adminMode);
    }

    public boolean registerMember(String name, String email, String password, String phone) {
        return authService.registerMember(name, email, password, phone);
    }
}
