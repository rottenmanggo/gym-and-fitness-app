package includes;

import auth.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import shared.Session;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.layout.VBox;

/**
 * TopbarController - Controller untuk komponen topbar global.
 * Menampilkan data user dari session, fitur pencarian,
 * notifikasi, dan shortcut profile.
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
    private Label notifBadge;

    @FXML
    private HBox profileButton;

    @FXML
    private Label userAvatar;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    private LayoutTopController layoutController;

    private int unreadNotificationCount = 3;

    private ContextMenu notificationMenu;

    public void setLayoutController(LayoutTopController layoutController) {
        this.layoutController = layoutController;
    }

    @FXML
    public void initialize() {
        loadUserData();
        setNotificationCount(unreadNotificationCount);
    }

    /**
     * Memuat data user dari Session ke komponen topbar.
     * Jika belum login, tampilkan default "Guest".
     */
    private void loadUserData() {
        if (Session.isLoggedIn() && Session.getUser() != null) {
            User user = Session.getUser();

            String name = user.getName();
            if (name != null && !name.isBlank()) {
                usernameLabel.setText(name);
                userAvatar.setText(name.substring(0, 1).toUpperCase());
            } else {
                usernameLabel.setText("User");
                userAvatar.setText("U");
            }

            String role = user.getRole();
            if (role != null && !role.isBlank()) {
                roleLabel.setText(role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());
            } else {
                roleLabel.setText("Member");
            }
        } else {
            usernameLabel.setText("Guest");
            roleLabel.setText("Belum login");
            userAvatar.setText("G");
        }
    }

    /**
     * Set judul halaman pada topbar.
     *
     * @param title Judul halaman
     */
    public void setTitle(String title) {
        if (pageTitle != null) {
            pageTitle.setText(title);
        }
    }

    /**
     * Set subtitle halaman pada topbar.
     *
     * @param subtitle Subtitle halaman
     */
    public void setSubtitle(String subtitle) {
        if (pageSubtitle != null) {
            pageSubtitle.setText(subtitle);
        }
    }

    /**
     * Set title dan subtitle sekaligus.
     *
     * @param title    Judul halaman
     * @param subtitle Subtitle halaman
     */
    public void setPageInfo(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
    }

    /**
     * Handler tombol notifikasi.
     * Untuk sementara menampilkan alert sederhana.
     * Nanti bisa diganti dropdown custom.
     */
    @FXML
    private void handleNotification(ActionEvent event) {
        unreadNotificationCount = 0;
        setNotificationCount(unreadNotificationCount);

        if (notificationMenu == null) {
            notificationMenu = createNotificationMenu();
        }

        if (notificationMenu.isShowing()) {
            notificationMenu.hide();
        } else {
            notificationMenu.show(notifButton, javafx.geometry.Side.BOTTOM, -270, 10);
        }
    }

    private ContextMenu createNotificationMenu() {
        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("notification-menu");

        VBox panel = new VBox(12);
        panel.getStyleClass().add("notification-panel");

        Label title = new Label("Notifikasi");
        title.getStyleClass().add("notification-title");

        panel.getChildren().add(title);

        if (Session.isLoggedIn() && Session.getUser() != null && Session.getUser().isAdmin()) {
            panel.getChildren().addAll(
                    createNotificationItem(
                            "💳 Pembayaran baru",
                            "Ada pembayaran member yang perlu diverifikasi.",
                            "Baru saja"),
                    createNotificationItem(
                            "👥 Data member",
                            "Ada member dengan status pending.",
                            "Hari ini"),
                    createNotificationItem(
                            "📊 Laporan",
                            "Cek ringkasan pembayaran terbaru.",
                            "Hari ini"));
        } else {
            panel.getChildren().addAll(
                    createNotificationItem(
                            "✅ Pembayaran diverifikasi",
                            "Pembayaran membership kamu sudah diverifikasi.",
                            "Hari ini"),
                    createNotificationItem(
                            "🏋 Workout tersedia",
                            "Ada workout baru yang bisa kamu ikuti.",
                            "Hari ini"),
                    createNotificationItem(
                            "📅 Membership aktif",
                            "Membership kamu masih aktif.",
                            "Hari ini"));
        }

        CustomMenuItem wrapper = new CustomMenuItem(panel);
        wrapper.setHideOnClick(false);

        menu.getItems().add(wrapper);
        return menu;
    }

    private VBox createNotificationItem(String title, String message, String time) {
        VBox item = new VBox(3);
        item.getStyleClass().add("notification-item");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("notification-item-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("notification-item-message");
        messageLabel.setWrapText(true);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("notification-item-time");

        item.getChildren().addAll(titleLabel, messageLabel, timeLabel);

        return item;
    }

    /**
     * Klik box profile kanan atas.
     * Admin diarahkan ke profile admin.
     * Member diarahkan ke profile member.
     */
    @FXML
    private void handleProfileClick(MouseEvent event) {
        if (layoutController == null || !Session.isLoggedIn() || Session.getUser() == null) {
            return;
        }

        if (Session.getUser().isAdmin()) {
            System.out.println("Profile admin belum tersedia.");
            return;
        }

        layoutController.navigateTo(
                "/member/profile/MemberProfile.fxml",
                "Profile Saya",
                "Kelola informasi akun, data tubuh, dan target fitness kamu.");
    }

    /**
     * Mendapatkan teks pencarian yang diinput user.
     *
     * @return String teks pencarian
     */
    public String getSearchText() {
        return searchField != null ? searchField.getText().trim() : "";
    }

    /**
     * Set listener untuk event pencarian.
     *
     * @param listener Runnable yang dipanggil saat search berubah
     */
    public void setOnSearch(Runnable listener) {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> listener.run());
        }
    }

    /**
     * Update jumlah notifikasi pada badge merah.
     *
     * @param count Jumlah notifikasi belum dibuka.
     */
    public void setNotificationCount(int count) {
        if (notifButton != null) {
            notifButton.setText("🔔");
        }

        if (notifBadge != null) {
            if (count > 0) {
                notifBadge.setText(String.valueOf(count));
                notifBadge.setVisible(true);
                notifBadge.setManaged(true);
            } else {
                notifBadge.setVisible(false);
                notifBadge.setManaged(false);
            }
        }
    }
}