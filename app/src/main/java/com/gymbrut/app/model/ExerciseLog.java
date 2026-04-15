package com.gymbrut.app.model;

public class ExerciseLog {
    private final String exercise;
    private final String setsReps;
    private final String weight;

    public ExerciseLog(String exercise, String setsReps, String weight) {
        this.exercise = exercise;
        this.setsReps = setsReps;
        this.weight = weight;
    }

    public String getExercise() { return exercise; }
    public String getSetsReps() { return setsReps; }
    public String getWeight() { return weight; }
}
