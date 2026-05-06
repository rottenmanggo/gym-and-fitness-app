package admin.workout;

import java.util.ArrayList;
import java.util.List;

public class Workout {

    private int workoutId;
    private String category;
    private String title;
    private String equipment;
    private String description;
    private String videoUrl;
    private int sets;
    private String reps;
    private String imagePath;
    private List<WorkoutStep> steps = new ArrayList<>();

    public Workout(
            int workoutId,
            String category,
            String title,
            String equipment,
            String description,
            String videoUrl,
            int sets,
            String reps,
            String imagePath) {
        this.workoutId = workoutId;
        this.category = category;
        this.title = title;
        this.equipment = equipment;
        this.description = description;
        this.videoUrl = videoUrl;
        this.sets = sets;
        this.reps = reps;
        this.imagePath = imagePath;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getSets() {
        return sets;
    }

    public String getReps() {
        return reps;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<WorkoutStep> getSteps() {
        return steps;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setSteps(List<WorkoutStep> steps) {
        this.steps = steps;
    }

    public String getMetaInfo() {
        return safe(category) + " • " + sets + " Set • " + safe(reps);
    }

    public String getEquipmentText() {
        return equipment == null || equipment.isBlank() ? "-" : equipment;
    }

    public String getDescriptionText() {
        return description == null || description.isBlank() ? "Belum ada tutorial." : description;
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}