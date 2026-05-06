package admin.workout;

import java.util.ArrayList;
import java.util.List;

public class Workout {

    private int    workoutId;
    private String category;
    private String title;
    private String equipment;
    private String description;
    private String videoUrl;
    private int    sets;
    private String reps;
    private String imagePath;
    private List<WorkoutStep> steps = new ArrayList<>();

    public Workout(int workoutId, String category, String title, String equipment,
                   String description, String videoUrl, int sets, String reps, String imagePath) {
        this.workoutId   = workoutId;
        this.category    = category;
        this.title       = title;
        this.equipment   = equipment;
        this.description = description;
        this.videoUrl    = videoUrl;
        this.sets        = sets;
        this.reps        = reps;
        this.imagePath   = imagePath;
    }

    // Getters
    public int    getWorkoutId()   { return workoutId; }
    public String getCategory()    { return category; }
    public String getTitle()       { return title; }
    public String getEquipment()   { return equipment; }
    public String getDescription() { return description; }
    public String getVideoUrl()    { return videoUrl; }
    public int    getSets()        { return sets; }
    public String getReps()        { return reps; }
    public String getImagePath()   { return imagePath; }
    public List<WorkoutStep> getSteps() { return steps; }

    // Setters
    public void setCategory(String v)    { this.category    = v; }
    public void setTitle(String v)       { this.title       = v; }
    public void setEquipment(String v)   { this.equipment   = v; }
    public void setDescription(String v) { this.description = v; }
    public void setVideoUrl(String v)    { this.videoUrl    = v; }
    public void setSets(int v)           { this.sets        = v; }
    public void setReps(String v)        { this.reps        = v; }
    public void setImagePath(String v)   { this.imagePath   = v; }
    public void setSteps(List<WorkoutStep> v) { this.steps  = v; }

    // Display helper
    public String getMetaInfo() {
        return category + "  •  " + sets + " Set  •  " + reps + " Reps";
    }
}