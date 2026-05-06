package includes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import shared.SceneManager;
import shared.Session;

/**
 * SidebarMemberController - Controller navigasi sidebar Member.
 *
 * Navigasi halaman dilakukan via LayoutTopController.navigateTo()
 * sehingga hanya konten yang di-swap, sidebar TIDAK di-reload.
 * Logout tetap menggunakan SceneManager (full scene change ke Login).
 */
public class SidebarMemberController {

    @FXML
    private Label memberNameLabel;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnWorkout;
    @FXML
    private Button btnMembership;
    @FXML
    private Button btnCheckin;
    @FXML
    private Button btnPayments;
    @FXML
    private Button btnNotifications;
    @FXML
    private Button btnProfile;

    /** Referensi ke LayoutTopController, di-set setelah sidebar di-load */
    private LayoutTopController layoutController;

    @FXML
    public void initialize() {
        // Set nama member dari session
        if (Session.isLoggedIn() && Session.getUser() != null) {
            memberNameLabel.setText(Session.getUser().getName());
        }
    }

    /**
     * Dipanggil oleh LayoutTopController setelah sidebar di-load.
     * @param controller Referensi ke LayoutTopController
     */
    public void setLayoutController(LayoutTopController controller) {
        this.layoutController = controller;
    }

    // ===================================================================
    //  NAVIGASI — swap konten via LayoutTopController
    // ===================================================================

    @FXML
    private void navigateDashboard(ActionEvent event) {
        setActiveButton("dashboard");
        layoutController.navigateTo(
                "/member/dashboard/MemberDashboard.fxml",
                "Dashboard Member",
                "Selamat datang di GYMBRUT.");
    }

    @FXML
    private void navigateWorkout(ActionEvent event) {
        setActiveButton("workout");
        layoutController.navigateTo(
                "/member/workout/MemberWorkout.fxml",
                "Jadwal Latihan",
                "Lihat dan atur jadwal latihan kamu.");
    }

    @FXML
    private void navigateMembership(ActionEvent event) {
        setActiveButton("membership");
        layoutController.navigateTo(
                "/member/membership/MemberMembership.fxml",
                "Membership",
                "Detail paket membership kamu.");
    }

    @FXML
    private void navigateCheckin(ActionEvent event) {
        setActiveButton("checkin");
        layoutController.navigateTo(
                "/member/checkin/MemberCheckin.fxml",
                "Check-in",
                "Catat kehadiran di gym.");
    }

    @FXML
    private void navigatePayments(ActionEvent event) {
        setActiveButton("payments");
        layoutController.navigateTo(
                "/member/payments/MemberPayments.fxml",
                "Pembayaran",
                "Riwayat dan status pembayaran.");
    }

    @FXML
    private void navigateNotifications(ActionEvent event) {
        setActiveButton("notifications");
        layoutController.navigateTo(
                "/member/notifications/MemberNotifications.fxml",
                "Notifikasi",
                "Pemberitahuan terbaru.");
    }

    @FXML
    private void navigateProfile(ActionEvent event) {
        setActiveButton("profile");
        layoutController.navigateTo(
                "/member/profile/MemberProfile.fxml",
                "Profil",
                "Pengaturan akun dan profil.");
    }

    // ===================================================================
    //  LOGOUT — full scene change kembali ke Login
    // ===================================================================

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/auth/Login.fxml",
                "GYMBRUT - Login",
                1100, 720);
    }

    // ===================================================================
    //  ACTIVE BUTTON STATE
    // ===================================================================

    public void setActiveButton(String activeButtonId) {
        Button[] allButtons = {btnDashboard, btnWorkout, btnMembership,
                               btnCheckin, btnPayments, btnNotifications, btnProfile};

        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-link-active");
                if (!btn.getStyleClass().contains("sidebar-link")) {
                    btn.getStyleClass().add("sidebar-link");
                }
            }
        }

        Button activeBtn = getButtonById(activeButtonId);
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("sidebar-link");
            if (!activeBtn.getStyleClass().contains("sidebar-link-active")) {
                activeBtn.getStyleClass().add("sidebar-link-active");
            }
        }
    }

    private Button getButtonById(String id) {
        switch (id) {
            case "dashboard":      return btnDashboard;
            case "workout":        return btnWorkout;
            case "membership":     return btnMembership;
            case "checkin":        return btnCheckin;
            case "payments":       return btnPayments;
            case "notifications":  return btnNotifications;
            case "profile":        return btnProfile;
            default:               return null;
        }
    }
}
