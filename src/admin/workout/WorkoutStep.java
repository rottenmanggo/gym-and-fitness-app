package admin.workout;

public class WorkoutStep {

    private int    stepId;
    private int    workoutId;
    private int    stepOrder;
    private String instruction;
    private String duration;

    public WorkoutStep(int stepId, int workoutId, int stepOrder,
                       String instruction, String duration) {
        this.stepId      = stepId;
        this.workoutId   = workoutId;
        this.stepOrder   = stepOrder;
        this.instruction = instruction;
        this.duration    = duration;
    }

    public int    getStepId()      { return stepId; }
    public int    getWorkoutId()   { return workoutId; }
    public int    getStepOrder()   { return stepOrder; }
    public String getInstruction() { return instruction; }
    public String getDuration()    { return duration; }
}