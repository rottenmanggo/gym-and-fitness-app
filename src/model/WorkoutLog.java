package model;

import java.time.LocalDate;

public class WorkoutLog {
    private int id;
    private int userId;
    private int workoutId;
    private LocalDate logDate;
    private int setsCount;
    private int repsCount;
    private int durationMinutes;
    private double weightUsed;
    private String notes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getWorkoutId() { return workoutId; }
    public void setWorkoutId(int workoutId) { this.workoutId = workoutId; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public int getSetsCount() { return setsCount; }
    public void setSetsCount(int setsCount) { this.setsCount = setsCount; }
    public int getRepsCount() { return repsCount; }
    public void setRepsCount(int repsCount) { this.repsCount = repsCount; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getWeightUsed() { return weightUsed; }
    public void setWeightUsed(double weightUsed) { this.weightUsed = weightUsed; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
