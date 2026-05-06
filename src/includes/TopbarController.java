package includes;

import auth.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import shared.Session;

/**
 * TopbarController - Controller untuk komponen topbar global.
 * Menampilkan data user dari session, fitur pencarian,
 * dan notifikasi. Isi data ditarik dari Session saat initialize.
 */
public class TopbarController {

    @FXML
    private Label pageTitle;

    @FXML
    private Label pageSubtitle;

    @FXML
    private TextField searchField;

    @FXML
    private Button notifButton;

    @FXML
    private Label userAvatar;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    public void initialize() {
        // Isi data user dari session
        loadUserData();
    }

    /**
     * Memuat data user dari Session ke komponen topbar.
     * Jika belum login, tampilkan default "Guest".
     */
    private void loadUserData() {
        if (Session.isLoggedIn() && Session.getUser() != null) {
            User user = Session.getUser();

            // Set nama user
            usernameLabel.setText(user.getName());

            // Set role
            String role = user.getRole();
            if (role != null) {
                roleLabel.setText(role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());
            }

            // Set avatar initial (huruf pertama dari nama)
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                userAvatar.setText(name.substring(0, 1).toUpperCase());
            }
        } else {
            usernameLabel.setText("Guest");
            roleLabel.setText("Belum login");
            userAvatar.setText("G");
        }
    }

    /**
     * Set judul halaman pada topbar.
     * @param title Judul halaman
     */
    public void setTitle(String title) {
        if (pageTitle != null) {
            pageTitle.setText(title);
        }
    }

    /**
     * Set subtitle halaman pada topbar.
     * @param subtitle Subtitle halaman
     */
    public void setSubtitle(String subtitle) {
        if (pageSubtitle != null) {
            pageSubtitle.setText(subtitle);
        }
    }

    /**
     * Set title dan subtitle sekaligus.
     * @param title    Judul halaman
     * @param subtitle Subtitle halaman
     */
    public void setPageInfo(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
    }

    /**
     * Handler untuk tombol notifikasi.
     * Bisa di-extend untuk menampilkan dropdown notifikasi.
     */
    @FXML
    private void handleNotification(ActionEvent event) {
        System.out.println("Notification button clicked");
        // TODO: Implementasi popup/dropdown notifikasi
    }

    /**
     * Mendapatkan teks pencarian yang diinput user.
     * @return String teks pencarian
     */
    public String getSearchText() {
        return searchField != null ? searchField.getText().trim() : "";
    }

    /**
     * Set listener untuk event pencarian.
     * @param listener Runnable yang dipanggil saat search berubah
     */
    public void setOnSearch(Runnable listener) {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                listener.run();
            });
        }
    }

    /**
     * Update jumlah notifikasi pada badge/button.
     * @param count Jumlah notifikasi
     */
    public void setNotificationCount(int count) {
        if (notifButton != null) {
            if (count > 0) {
                notifButton.setText("🔔 " + count);
            } else {
                notifButton.setText("🔔");
            }
        }
    }
}
