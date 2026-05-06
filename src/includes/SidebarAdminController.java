package includes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import shared.SceneManager;
import shared.Session;

/**
 * SidebarAdminController - Controller navigasi sidebar Admin.
 *
 * Navigasi halaman dilakukan via LayoutTopController.navigateTo()
 * sehingga hanya konten yang di-swap, sidebar TIDAK di-reload.
 * Logout tetap menggunakan SceneManager (full scene change ke Login).
 */
public class SidebarAdminController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnMembers;
    @FXML
    private Button btnMemberships;
    @FXML
    private Button btnPayments;
    @FXML
    private Button btnReports;
    @FXML
    private Button btnWorkouts;

    /** Referensi ke LayoutTopController, di-set setelah sidebar di-load */
    private LayoutTopController layoutController;

    @FXML
    public void initialize() {
        // Set nama admin dari session
        if (Session.isLoggedIn() && Session.getUser() != null) {
            adminNameLabel.setText(Session.getUser().getName());
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
    //  NAVIGASI — swap konten via LayoutTopController (tanpa reload sidebar)
    // ===================================================================

    @FXML
    private void navigateDashboard(ActionEvent event) {
        setActiveButton("dashboard");
        layoutController.navigateTo(
                "/admin/dashboard/Dashboard.fxml",
                "Dashboard Admin",
                "Ringkasan operasional GYMBRUT hari ini.");
    }

    @FXML
    private void navigateMembers(ActionEvent event) {
        setActiveButton("members");
        layoutController.navigateTo(
                "/admin/member/Member.fxml",
                "Data Members",
                "Kelola data member GYMBRUT.");
    }

    @FXML
    private void navigateMemberships(ActionEvent event) {
        setActiveButton("memberships");
        layoutController.navigateTo(
                "/admin/membership/Membership.fxml",
                "Data Memberships",
                "Kelola paket membership GYMBRUT.");
    }

    @FXML
    private void navigatePayments(ActionEvent event) {
        setActiveButton("payments");
        layoutController.navigateTo(
                "/admin/payments/Payment.fxml",
                "Payments",
                "Kelola pembayaran member.");
    }

    @FXML
    private void navigateReports(ActionEvent event) {
        setActiveButton("reports");
        layoutController.navigateTo(
                "/admin/reports/Reports.fxml",
                "Reports",
                "Laporan operasional GYMBRUT.");
    }

    @FXML
    private void navigateWorkouts(ActionEvent event) {
        setActiveButton("workouts");
        layoutController.navigateTo(
                "/admin/workout/Workout.fxml",
                "Workouts",
                "Kelola program workout.");
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

    /**
     * Set tombol aktif berdasarkan ID navigasi.
     * @param activeButtonId ID tombol (dashboard, members, dll)
     */
    public void setActiveButton(String activeButtonId) {
        Button[] allButtons = {btnDashboard, btnMembers, btnMemberships,
                               btnPayments, btnReports, btnWorkouts};

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
            case "dashboard":   return btnDashboard;
            case "members":     return btnMembers;
            case "memberships": return btnMemberships;
            case "payments":    return btnPayments;
            case "reports":     return btnReports;
            case "workouts":    return btnWorkouts;
            default:            return null;
        }
    }
}
