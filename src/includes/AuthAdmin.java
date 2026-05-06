package includes;

import shared.Session;

/**
 * AuthAdmin - Helper untuk verifikasi akses admin.
 * Cek apakah user yang sedang login memiliki role admin.
 */
public class AuthAdmin {

    /**
     * Memeriksa apakah user saat ini adalah admin yang valid.
     * @return true jika user login dan memiliki role admin
     */
    public static boolean check() {
        return Session.isLoggedIn()
                && Session.getUser() != null
                && Session.getUser().isAdmin();
    }
}
