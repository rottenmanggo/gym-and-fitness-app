package member.checkin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class CheckinController {

    @FXML private Label statusLabel;

    private LocalDate lastCheckin;

    @FXML
    public void initialize() {
        updateStatus();
    }

    @FXML
    private void handleCheckin() {
        LocalDate today = LocalDate.now();

        if (today.equals(lastCheckin)) {
            showAlert("Kamu sudah check-in hari ini!");
        } else {
            lastCheckin = today;
            showAlert("Check-in berhasil!");
            updateStatus();
        }
    }

    private void updateStatus() {
        if (LocalDate.now().equals(lastCheckin)) {
            statusLabel.setText("Sudah check-in hari ini");
        } else {
            statusLabel.setText("Belum check-in hari ini");
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
