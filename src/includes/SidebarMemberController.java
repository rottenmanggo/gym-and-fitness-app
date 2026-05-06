package includes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import shared.SceneManager;
import shared.Session;

public class SidebarMemberController {

    @FXML
    private Label memberNameLabel;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnMembership;
    @FXML
    private Button btnPayments;
    @FXML
    private Button btnCheckin;
    @FXML
    private Button btnWorkouts;
    @FXML
    private Button btnProfile;

    private LayoutTopController layoutController;

    @FXML
    public void initialize() {
        if (Session.isLoggedIn() && Session.getUser() != null && memberNameLabel != null) {
            memberNameLabel.setText(Session.getUser().getName());
        }
    }

    public void setLayoutController(LayoutTopController layoutController) {
        this.layoutController = layoutController;
    }

    @FXML
    private void navigateDashboard(ActionEvent event) {
        navigate(
                "dashboard",
                "/member/dashboard/MemberDashboard.fxml",
                "Dashboard Member",
                "Selamat datang di GYMBRUT.");
    }

    @FXML
    private void navigateMembership(ActionEvent event) {
        navigate(
                "membership",
                "/member/membership/MemberMembership.fxml",
                "Membership Saya",
                "Lihat detail paket, masa berlaku, status pembayaran, dan pilih paket membership.");
    }

    @FXML
    private void navigatePayments(ActionEvent event) {
        navigate(
                "payments",
                "/member/payments/MemberPayments.fxml",
                "Pembayaran Saya",
                "Upload bukti pembayaran dan pantau status verifikasi membership kamu.");
    }

    @FXML
    private void navigateCheckin(ActionEvent event) {
        navigate(
                "checkin",
                "/member/checkin/Checkin.fxml",
                "Check In Member",
                "Lakukan check-in harian untuk mencatat kehadiran gym kamu.");
    }

    @FXML
    private void navigateWorkouts(ActionEvent event) {
        navigate(
                "workouts",
                "/member/workout/MemberWorkouts.fxml",
                "Workout Member",
                "Lihat daftar workout, kategori latihan, equipment, dan tutorial singkat.");
    }

    @FXML
    private void navigateProfile(ActionEvent event) {
        navigate(
                "profile",
                "/member/profile/MemberProfile.fxml",
                "Profile Saya",
                "Kelola informasi akun, data tubuh, dan target fitness kamu.");
    }

    private void navigate(String activeId, String fxmlPath, String title, String subtitle) {
        setActiveButton(activeId);

        if (layoutController == null) {
            System.out.println("LayoutTopController belum terhubung ke SidebarMemberController.");
            return;
        }

        layoutController.navigateTo(fxmlPath, title, subtitle);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();

        SceneManager.changeScene(
                (Node) event.getSource(),
                "/auth/Login.fxml",
                "GYMBRUT - Login",
                1100,
                720);
    }

    public void setActiveButton(String activeButtonId) {
        Button[] allButtons = {
                btnDashboard,
                btnMembership,
                btnPayments,
                btnCheckin,
                btnWorkouts,
                btnProfile
        };

        for (Button button : allButtons) {
            if (button != null) {
                button.getStyleClass().removeAll("sidebar-link-active");

                if (!button.getStyleClass().contains("sidebar-link")) {
                    button.getStyleClass().add("sidebar-link");
                }
            }
        }

        Button activeButton = getButtonById(activeButtonId);

        if (activeButton != null) {
            activeButton.getStyleClass().removeAll("sidebar-link");

            if (!activeButton.getStyleClass().contains("sidebar-link-active")) {
                activeButton.getStyleClass().add("sidebar-link-active");
            }
        }
    }

    private Button getButtonById(String id) {
        return switch (id) {
            case "dashboard" -> btnDashboard;
            case "membership" -> btnMembership;
            case "payments" -> btnPayments;
            case "checkin" -> btnCheckin;
            case "workouts" -> btnWorkouts;
            case "profile" -> btnProfile;
            default -> null;
        };
    }
}