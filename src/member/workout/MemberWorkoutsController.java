package member.workout;

import config.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.sql.*;

import admin.workout.Workout;
import admin.workout.WorkoutStep;
import admin.workout.DetailWorkoutController;

public class MemberWorkoutsController {

    @FXML
    private Label heroStatusLabel;
    @FXML
    private Label heroPackageLabel;
    @FXML
    private Label membershipBadge;
    @FXML
    private Label membershipNoteLabel;

    @FXML
    private Label totalWorkoutLabel;
    @FXML
    private Label totalCategoryLabel;
    @FXML
    private Label filteredWorkoutLabel;
    @FXML
    private Label workoutCountLabel;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private GridPane workoutGrid;

    private int userId = 0;

    private final ObservableList<WorkoutItem> allWorkouts = FXCollections.observableArrayList();
    private final ObservableList<WorkoutItem> filteredWorkouts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
        }

        setupGrid();
        setDefaultValues();
        loadPageData();
    }

    private void setupGrid() {
        workoutGrid.getColumnConstraints().clear();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        workoutGrid.getColumnConstraints().addAll(col1, col2);
    }

    private void setDefaultValues() {
        heroStatusLabel.setText("Tidak Aktif");
        heroPackageLabel.setText("Belum ada membership aktif");

        setBadge(membershipBadge, "Tidak Aktif", "expired");
        membershipNoteLabel.setVisible(true);
        membershipNoteLabel.setManaged(true);

        totalWorkoutLabel.setText("0");
        totalCategoryLabel.setText("0");
        filteredWorkoutLabel.setText("0");
        workoutCountLabel.setText("0 workout");

        categoryCombo.setItems(FXCollections.observableArrayList("Semua"));
        categoryCombo.getSelectionModel().selectFirst();
    }

    private void loadPageData() {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database", "Koneksi database gagal.");
                return;
            }

            loadMembershipStatus(conn);
            loadCategories(conn);
            loadStats(conn);
            loadWorkouts(conn);
            applyFilter();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat data workout.");
        }
    }

    private void loadMembershipStatus(Connection conn) {
        String sql = """
                    SELECT
                        m.status,
                        m.end_date,
                        mp.package_name
                    FROM memberships m
                    JOIN membership_packages mp ON m.package_id = mp.package_id
                    WHERE m.user_id = ?
                    AND m.status = 'aktif'
                    AND m.end_date >= CURDATE()
                    ORDER BY m.end_date DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    heroStatusLabel.setText("Tidak Aktif");
                    heroPackageLabel.setText("Belum ada membership aktif");
                    setBadge(membershipBadge, "Tidak Aktif", "expired");
                    membershipNoteLabel.setVisible(true);
                    membershipNoteLabel.setManaged(true);
                    return;
                }

                heroStatusLabel.setText("Aktif");
                heroPackageLabel.setText(rs.getString("package_name"));
                setBadge(membershipBadge, "Aktif", "aktif");

                membershipNoteLabel.setVisible(false);
                membershipNoteLabel.setManaged(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCategories(Connection conn) {
        ObservableList<String> categories = FXCollections.observableArrayList("Semua");

        String sql = """
                    SELECT DISTINCT category
                    FROM workouts
                    ORDER BY category ASC
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        categoryCombo.setItems(categories);
        categoryCombo.getSelectionModel().selectFirst();
    }

    private void loadStats(Connection conn) {
        totalWorkoutLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) AS total FROM workouts")));
        totalCategoryLabel
                .setText(String.valueOf(getCount(conn, "SELECT COUNT(DISTINCT category) AS total FROM workouts")));
    }

    private int getCount(Connection conn, String sql) {
        try (
                PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void loadWorkouts(Connection conn) {
        allWorkouts.clear();

        String sql = """
                    SELECT
                        workout_id,
                        category,
                        title,
                        equipment,
                        tutorial,
                        youtube_url,
                        sets_count,
                        reps_count,
                        image_file
                    FROM workouts
                    ORDER BY workout_id DESC
                """;

        try (
                PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allWorkouts.add(new WorkoutItem(
                        rs.getInt("workout_id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("equipment"),
                        rs.getString("tutorial"),
                        rs.getString("youtube_url"),
                        rs.getInt("sets_count"),
                        rs.getString("reps_count"),
                        rs.getString("image_file")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilter() {
        applyFilter();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        categoryCombo.getSelectionModel().select("Semua");
        applyFilter();
    }

    private void applyFilter() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().toLowerCase().trim();

        String category = categoryCombo.getValue() == null
                ? "Semua"
                : categoryCombo.getValue();

        filteredWorkouts.clear();

        for (WorkoutItem workout : allWorkouts) {
            boolean categoryMatch = category.equals("Semua")
                    || safe(workout.category).equalsIgnoreCase(category);

            boolean searchMatch = keyword.isEmpty()
                    || safe(workout.title).toLowerCase().contains(keyword)
                    || safe(workout.category).toLowerCase().contains(keyword)
                    || safe(workout.equipment).toLowerCase().contains(keyword)
                    || safe(workout.tutorial).toLowerCase().contains(keyword)
                    || safe(workout.repsCount).toLowerCase().contains(keyword);

            if (categoryMatch && searchMatch) {
                filteredWorkouts.add(workout);
            }
        }

        renderWorkouts();
    }

    private void renderWorkouts() {
        workoutGrid.getChildren().clear();

        if (filteredWorkouts.isEmpty()) {
            VBox emptyCard = new VBox(8);
            emptyCard.getStyleClass().add("premium-card");

            Label title = new Label("Tidak ada workout yang cocok dengan filter.");
            title.getStyleClass().add("text-soft");

            emptyCard.getChildren().add(title);

            workoutGrid.add(emptyCard, 0, 0, 2, 1);

            filteredWorkoutLabel.setText("0");
            workoutCountLabel.setText("0 workout");
            return;
        }

        int col = 0;
        int row = 0;

        for (WorkoutItem workout : filteredWorkouts) {
            VBox card = createWorkoutCard(workout);

            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);

            workoutGrid.add(card, col, row);

            col++;

            if (col > 1) {
                col = 0;
                row++;
            }
        }

        filteredWorkoutLabel.setText(String.valueOf(filteredWorkouts.size()));
        workoutCountLabel.setText(filteredWorkouts.size() + " workout");
    }

    private VBox createWorkoutCard(WorkoutItem workout) {
        VBox card = new VBox(16);
        card.getStyleClass().add("member-workout-card");
        card.setMaxWidth(Double.MAX_VALUE);

        StackPane imageBox = createWorkoutImageBox(workout);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label title = new Label(safe(workout.title));
        title.setWrapText(true);
        title.getStyleClass().add("section-title");

        Label equipment = new Label(
                workout.equipment == null || workout.equipment.isBlank()
                ? "Tanpa equipment khusus"
                : workout.equipment);
        equipment.setWrapText(true);
        equipment.getStyleClass().add("section-subtitle");

        titleBox.getChildren().addAll(title, equipment);

        Label categoryBadge = new Label(safe(workout.category));
        categoryBadge.getStyleClass().add("badge-soft");
        addWorkoutBadgeClass(categoryBadge, workout.category);

        header.getChildren().addAll(titleBox, categoryBadge);

        VBox metricList = new VBox(0);
        metricList.getStyleClass().add("metric-list");

        metricList.getChildren().addAll(
                createMetricRow("Set", workout.setsCount == 0 ? "-" : String.valueOf(workout.setsCount)),
                createMetricRow("Repetisi / Durasi",
                        workout.repsCount == null || workout.repsCount.isBlank() ? "-" : workout.repsCount));

        Label tutorial = new Label(
                workout.tutorial == null || workout.tutorial.isBlank()
                ? "Belum ada tutorial untuk workout ini."
                : workout.tutorial);
        tutorial.setWrapText(true);
        tutorial.getStyleClass().add("text-soft");

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("btn-outline-soft-small");
        detailButton.setOnAction(event -> showDetail(workout));

        Button tutorialButton = new Button("Tutorial");
        tutorialButton.getStyleClass().add("primary-btn-small");
        tutorialButton.setOnAction(event -> openYoutube(workout.youtubeUrl));

        if (workout.youtubeUrl == null || workout.youtubeUrl.isBlank()) {
            tutorialButton.setDisable(true);
        }

        actions.getChildren().addAll(detailButton, tutorialButton);

        card.getChildren().addAll(imageBox, header, metricList, tutorial, actions);

        return card;
    }

    private StackPane createWorkoutImageBox(WorkoutItem workout) {
        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("workout-image-placeholder");

        String imageFile = workout.imageFile;

        if (imageFile != null && !imageFile.isBlank()) {
            try {
                String cleanImageFile = imageFile.trim();

                URL imageUrl = getClass().getResource("/assets/image/" + cleanImageFile);

                if (imageUrl != null) {
                    ImageView imageView = new ImageView(new Image(imageUrl.toExternalForm()));
                    imageView.setFitWidth(360);
                    imageView.setFitHeight(175);
                    imageView.setPreserveRatio(false);
                    imageView.setSmooth(true);

                    imageBox.getChildren().add(imageView);
                    return imageBox;
                } else {
                    System.out.println("Gambar workout tidak ditemukan: /assets/image/" + cleanImageFile);
                }
            } catch (Exception e) {
                System.out.println("Gagal load gambar workout: " + imageFile);
                e.printStackTrace();
            }
        }

        VBox fallback = new VBox(6);
        fallback.setAlignment(Pos.CENTER);

        Label imageIcon = new Label("⚡");
        imageIcon.getStyleClass().add("workout-image-icon");

        Label imageText = new Label("Workout");
        imageText.getStyleClass().add("workout-image-text");

        fallback.getChildren().addAll(imageIcon, imageText);
        imageBox.getChildren().add(fallback);

        return imageBox;
    }

    private HBox createMetricRow(String label, String value) {
        HBox row = new HBox();
        row.getStyleClass().add("metric-row");

        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("metric-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("metric-value");

        row.getChildren().addAll(labelNode, spacer, valueNode);

        return row;
    }

    private void showDetail(WorkoutItem item) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/admin/workout/DetailWorkout.fxml"
                    )
            );

            Parent root = loader.load();

            DetailWorkoutController controller
                    = loader.getController();

            // convert WorkoutItem -> Workout
            Workout workout = new Workout(
                    item.workoutId,
                    item.category,
                    item.title,
                    item.equipment,
                    item.tutorial,
                    item.youtubeUrl,
                    item.setsCount,
                    item.repsCount,
                    item.imageFile
            );

            // load step dari database
            workout.setSteps(loadWorkoutSteps(item.workoutId));

            controller.setData(workout);

            Stage stage = new Stage();

            stage.setTitle("Detail Workout");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<WorkoutStep> loadWorkoutSteps(int workoutId) {

        List<WorkoutStep> steps = new ArrayList<>();

        String sql = """
        SELECT *
        FROM workout_steps
        WHERE workout_id = ?
        ORDER BY step_order ASC
    """;

        try (
                Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workoutId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                steps.add(new WorkoutStep(
                        rs.getInt("step_id"),
                        rs.getInt("workout_id"),
                        rs.getInt("step_order"),
                        rs.getString("instruction"),
                        rs.getString("duration")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return steps;
    }

    private void openYoutube(String url) {
        if (url == null || url.isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Tutorial", "Link tutorial belum tersedia.");
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak bisa membuka link tutorial.");
        }
    }

    private void setBadge(Label label, String text, String status) {
        label.setText(text);
        label.getStyleClass().removeAll("badge-active", "badge-pending", "badge-failed", "badge-info");

        if ("aktif".equalsIgnoreCase(status)) {
            label.getStyleClass().add("badge-active");
        } else {
            label.getStyleClass().add("badge-failed");
        }
    }

    private void addWorkoutBadgeClass(Label label, String category) {
        label.getStyleClass().removeAll("badge-active", "badge-pending", "badge-failed", "badge-info");

        if ("Fat Loss".equalsIgnoreCase(category)) {
            label.getStyleClass().add("badge-failed");
        } else if ("Bulking".equalsIgnoreCase(category)) {
            label.getStyleClass().add("badge-active");
        } else if ("Strength".equalsIgnoreCase(category)) {
            label.getStyleClass().add("badge-pending");
        } else {
            label.getStyleClass().add("badge-info");
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class WorkoutItem {

        int workoutId;
        String category;
        String title;
        String equipment;
        String tutorial;
        String youtubeUrl;
        int setsCount;
        String repsCount;
        String imageFile;

        WorkoutItem(
                int workoutId,
                String category,
                String title,
                String equipment,
                String tutorial,
                String youtubeUrl,
                int setsCount,
                String repsCount,
                String imageFile) {
            this.workoutId = workoutId;
            this.category = category;
            this.title = title;
            this.equipment = equipment;
            this.tutorial = tutorial;
            this.youtubeUrl = youtubeUrl;
            this.setsCount = setsCount;
            this.repsCount = repsCount;
            this.imageFile = imageFile;
        }
    }
}
