package admin.workout;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DetailWorkoutController {

    @FXML private Label  lblJudul;
    @FXML private Label  lblMeta;
    @FXML private Label  lblBadge;
    @FXML private Label  lblDeskripsi;
    @FXML private Label  lblGambar;
    @FXML private VBox   stepsContainer;
    @FXML private Button btnVideo;

    private Workout workout;

    public void setData(Workout w) {
        this.workout = w;

        lblJudul.setText(w.getTitle());
        lblMeta.setText(w.getCategory() + "  •  " + w.getSets() + " Set  •  " + w.getReps());
        lblBadge.setText(w.getCategory());
        lblDeskripsi.setText(w.getDescription() != null ? w.getDescription() : "-");
        lblGambar.setText(w.getImagePath() != null ? "[ " + w.getImagePath() + " ]" : "[ Tidak ada gambar ]");

        buildSteps(w);
    }

    private void buildSteps(Workout w) {
        stepsContainer.getChildren().clear();

        if (w.getSteps() == null || w.getSteps().isEmpty()) {
            stepsContainer.getChildren().add(new Label("Belum ada langkah latihan."));
            return;
        }

        for (WorkoutStep step : w.getSteps()) {
            VBox stepCard = new VBox(6);
            stepCard.getStyleClass().add("step-card");
            stepCard.setPadding(new Insets(14));

            // Nomor + instruksi
            HBox row = new HBox(12);
            row.setAlignment(Pos.TOP_LEFT);

            Label num = new Label(String.valueOf(step.getStepOrder()));
            num.getStyleClass().add("step-num");
            num.setMinWidth(32);
            num.setMinHeight(32);

            VBox info = new VBox(4);
            Label stepTitle = new Label("Step " + step.getStepOrder());
            stepTitle.getStyleClass().add("step-title");
            Label instr = new Label(step.getInstruction());
            instr.getStyleClass().add("card-benefit-text");
            instr.setWrapText(true);
            info.getChildren().addAll(stepTitle, instr);

            row.getChildren().addAll(num, info);

            // Duration badge
            if (step.getDuration() != null && !step.getDuration().isBlank()) {
                Label dur = new Label(step.getDuration());
                dur.getStyleClass().add("duration-badge");
            stepCard.getChildren().addAll(row, dur);
            } else {
                stepCard.getChildren().add(row);
            }

            stepsContainer.getChildren().add(stepCard);
        }
    }

    @FXML
    public void handleVideo() {
        if (workout == null || workout.getVideoUrl() == null || workout.getVideoUrl().isBlank()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("Video tidak tersedia.");
            a.showAndWait();
            return;
        }
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(workout.getVideoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}