package com.gymbrut.app.model;

public class WorkoutModule {
    private final String name;
    private final String duration;
    private final String goal;
    private final String steps;

    public WorkoutModule(String name, String duration, String goal, String steps) {
        this.name = name;
        this.duration = duration;
        this.goal = goal;
        this.steps = steps;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getGoal() { return goal; }
    public String getSteps() { return steps; }
}
