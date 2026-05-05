package com.gymbrut.includes;

import com.gymbrut.shared.Session;

public class AuthUser {

    public static boolean check() {
        return Session.isLoggedIn()
                && Session.getUser() != null
                && Session.getUser().isMember();
    }
}