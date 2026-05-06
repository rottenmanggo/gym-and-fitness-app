package includes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import shared.SceneManager;
import shared.Session;

/**
 * BottomNavMemberController - Controller untuk bottom navigation member.
 * Navigasi mobile-like di bagian bawah halaman member.
 */
public class BottomNavMemberController {

    @FXML
    private Button btnNavDashboard;
    @FXML
    private Button btnNavWorkout;
    @FXML
    private Button btnNavCheckin;
    @FXML
    private Button btnNavNotif;
    @FXML
    private Button btnNavProfile;

    /**
     * Navigasi ke Dashboard Member.
     */
    @FXML
    private void navigateDashboard(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/member/dashboard/MemberDashboard.fxml",
                "GYMBRUT - Dashboard Member",
                1280, 760);
    }

    /**
     * Navigasi ke halaman Workout / Jadwal Latihan.
     */
    @FXML
    private void navigateWorkout(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/member/workout/MemberWorkout.fxml",
                "GYMBRUT - Jadwal Latihan",
                1280, 760);
    }

    /**
     * Navigasi ke halaman Check-in.
     */
    @FXML
    private void navigateCheckin(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/member/checkin/MemberCheckin.fxml",
                "GYMBRUT - Check-in",
                1280, 760);
    }

    /**
     * Navigasi ke halaman Notifikasi.
     */
    @FXML
    private void navigateNotifications(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/member/notifications/MemberNotifications.fxml",
                "GYMBRUT - Notifikasi",
                1280, 760);
    }

    /**
     * Navigasi ke halaman Profil Member.
     */
    @FXML
    private void navigateProfile(ActionEvent event) {
        SceneManager.changeScene(
                (Node) event.getSource(),
                "/member/profile/MemberProfile.fxml",
                "GYMBRUT - Profil",
                1280, 760);
    }

    /**
     * Set tombol aktif berdasarkan halaman saat ini.
     * @param activeId ID tombol yang aktif
     */
    public void setActiveButton(String activeId) {
        Button[] allButtons = {btnNavDashboard, btnNavWorkout, btnNavCheckin,
                               btnNavNotif, btnNavProfile};

        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("bottom-nav-btn-active");
                if (!btn.getStyleClass().contains("bottom-nav-btn")) {
                    btn.getStyleClass().add("bottom-nav-btn");
                }
            }
        }

        Button activeBtn = getButtonById(activeId);
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("bottom-nav-btn");
            if (!activeBtn.getStyleClass().contains("bottom-nav-btn-active")) {
                activeBtn.getStyleClass().add("bottom-nav-btn-active");
            }
        }
    }

    private Button getButtonById(String id) {
        switch (id) {
            case "dashboard":     return btnNavDashboard;
            case "workout":       return btnNavWorkout;
            case "checkin":       return btnNavCheckin;
            case "notifications": return btnNavNotif;
            case "profile":       return btnNavProfile;
            default:              return null;
        }
    }
}
