package admin.workout;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.net.URI;

public class DetailWorkoutController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label metaLabel;
    @FXML
    private Label categoryBadge;
    @FXML
    private ImageView imgWorkout;
    @FXML
    private Label descriptionLabel;
    @FXML
    private VBox stepsContainer;
    @FXML
    private Button videoButton;

    private Workout workout;

    public void setData(Workout workout) {
        this.workout = workout;

        if (workout == null) {
            return;
        }

        titleLabel.setText(workout.getTitle());
        metaLabel.setText(workout.getMetaInfo());
        categoryBadge.setText(workout.getCategory());

        if (workout.getImagePath() != null
                && !workout.getImagePath().isBlank()) {

            try {

                Image image = new Image(
                        getClass().getResourceAsStream(
                                "/assets/image/" + workout.getImagePath()
                        )
                );

                imgWorkout.setImage(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        descriptionLabel.setText(workout.getDescriptionText());

        videoButton.setDisable(workout.getVideoUrl() == null || workout.getVideoUrl().isBlank());

        buildSteps();
    }

    private void buildSteps() {
        stepsContainer.getChildren().clear();

        if (workout.getSteps() == null || workout.getSteps().isEmpty()) {
            Label empty = new Label("Belum ada instruksi detail.");
            empty.getStyleClass().add("empty-subtitle");
            stepsContainer.getChildren().add(empty);
            return;
        }

        for (WorkoutStep step : workout.getSteps()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.TOP_LEFT);
            row.getStyleClass().add("step-row");

            Label number = new Label(String.valueOf(step.getStepOrder()));
            number.getStyleClass().add("step-number");

            VBox info = new VBox(5);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label title = new Label("Step " + step.getStepOrder());
            title.getStyleClass().add("step-title");

            Label instruction = new Label(step.getInstruction());
            instruction.getStyleClass().add("step-instruction");
            instruction.setWrapText(true);

            info.getChildren().addAll(title, instruction);

            if (step.getDuration() != null && !step.getDuration().isBlank()) {
                Label duration = new Label(step.getDuration());
                duration.getStyleClass().add("duration-badge");
                info.getChildren().add(duration);
            }

            row.getChildren().addAll(number, info);
            stepsContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleVideo() {
        if (workout == null || workout.getVideoUrl() == null || workout.getVideoUrl().isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Video Kosong", "Video tidak tersedia.");
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(workout.getVideoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak bisa membuka video.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
