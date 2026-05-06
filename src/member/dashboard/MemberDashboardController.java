package member.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import shared.Session;

/**
 * MemberDashboardController - Controller untuk konten Dashboard Member.
 *
 * Hanya menangani logika data dashboard member.
 * Navigasi dan logout ditangani oleh SidebarMemberController
 * melalui LayoutTopController (single-shell architecture).
 */
public class MemberDashboardController {

    @FXML
    private Label greetingLabel;

    @FXML
    public void initialize() {
        // Set greeting dari session
        if (Session.isLoggedIn() && Session.getUser() != null) {
            greetingLabel.setText("Halo, " + Session.getUser().getName() + " 👋");
        }
    }
}