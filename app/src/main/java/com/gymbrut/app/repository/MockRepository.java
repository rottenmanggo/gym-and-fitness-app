package com.gymbrut.app.repository;

import com.gymbrut.app.model.ExerciseLog;
import com.gymbrut.app.model.Member;
import com.gymbrut.app.model.OnboardingItem;
import com.gymbrut.app.model.PaymentTransaction;
import com.gymbrut.app.model.WorkoutModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockRepository {
    private static MockRepository instance;
    private final Member member = new Member("Alex", "alex@gymbrut.com", "GB-8829-XL", "Ultimate Noir", "30 Dec 2024");

    public static MockRepository getInstance() {
        if (instance == null) instance = new MockRepository();
        return instance;
    }

    public Member getMember() { return member; }

    public List<OnboardingItem> getOnboardingItems() {
        return Arrays.asList(
            new OnboardingItem(android.R.drawable.ic_menu_compass, "Train with Precision", "Get guided programs tailored to fat loss, hypertrophy, endurance, or strength."),
            new OnboardingItem(android.R.drawable.ic_menu_my_calendar, "Membership in Real Time", "Track package status, renew online, and monitor payment verification."),
            new OnboardingItem(android.R.drawable.ic_menu_camera, "Fast QR Check-In", "Use digital attendance to enter the gym quickly and keep visit history synced."),
            new OnboardingItem(android.R.drawable.ic_menu_sort_by_size, "See Your Progress", "Review weight trends, daily logs, and target completion charts from one dashboard.")
        );
    }

    public List<PaymentTransaction> getTransactions() {
        return Arrays.asList(
            new PaymentTransaction("Proof of Payment", "Rp 850.000", "Awaiting Admin Verification"),
            new PaymentTransaction("Monthly Renewal - Sep", "Rp 850.000", "Verified via Bank Transfer"),
            new PaymentTransaction("Monthly Renewal - Aug", "Rp 850.000", "Verified via Credit Card")
        );
    }

    public List<WorkoutModule> getWorkoutModules() {
        return Arrays.asList(
            new WorkoutModule("Arm Focus", "45 min", "Hypertrophy", "1. Barbell curl 4x12\n" +
"2. Hammer curl 4x10\n" +
"3. Tricep pushdown 4x12\n" +
"4. Skull crusher 3x10"),
            new WorkoutModule("Leg Focus", "60 min", "Strength", "1. Back squat 5x5\n" +
"2. Romanian deadlift 4x8\n" +
"3. Walking lunges 3x12\n" +
"4. Leg curl 3x15"),
            new WorkoutModule("HIIT Burner", "20 min", "Fat Loss", "1. Sprint 30 sec\n" +
"2. Rest 30 sec\n" +
"3. Burpees 12 reps\n" +
"4. Repeat 6 rounds"),
            new WorkoutModule("Upper Power", "55 min", "Strength", "1. Bench press 5x5\n" +
"2. Bent-over row 4x8\n" +
"3. OHP 4x6\n" +
"4. Pull-ups 4x8")
        );
    }

    public List<ExerciseLog> getExerciseLogs() {
        return new ArrayList<>(Arrays.asList(
            new ExerciseLog("Barbell Bench Press", "4 sets x 8 reps", "80 kg"),
            new ExerciseLog("Deadlift", "4 sets x 5 reps", "120 kg"),
            new ExerciseLog("Leg Press", "3 sets x 12 reps", "140 kg")
        ));
    }

    public List<Float> getWeeklyProgress() {
        return Arrays.asList(35f, 45f, 50f, 62f, 68f, 75f, 82f);
    }

    public List<Float> getWeightTrend() {
        return Arrays.asList(82.2f, 81.8f, 81.1f, 80.7f, 80.5f, 80.0f, 79.6f);
    }
}
