package admin.workout;

import config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutService {

    public List<Workout> getAll() {
        List<Workout> list = new ArrayList<>();

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
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Workout(
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

        return list;
    }

    public Workout getById(int id) {
        Workout workout = null;

        String sql = """
                    SELECT *
                    FROM workouts
                    WHERE workout_id = ?
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    workout = new Workout(
                            rs.getInt("workout_id"),
                            rs.getString("category"),
                            rs.getString("title"),
                            rs.getString("equipment"),
                            rs.getString("tutorial"),
                            rs.getString("youtube_url"),
                            rs.getInt("sets_count"),
                            rs.getString("reps_count"),
                            rs.getString("image_file"));

                    workout.setSteps(getSteps(id, conn));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return workout;
    }

    private List<WorkoutStep> getSteps(int workoutId, Connection conn) throws SQLException {
        List<WorkoutStep> list = new ArrayList<>();

        String sql = """
                    SELECT *
                    FROM workout_steps
                    WHERE workout_id = ?
                    ORDER BY step_order ASC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workoutId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new WorkoutStep(
                            rs.getInt("step_id"),
                            rs.getInt("workout_id"),
                            rs.getInt("step_order"),
                            rs.getString("instruction"),
                            rs.getString("duration")));
                }
            }
        }

        return list;
    }

    public boolean insert(Workout workout) {
        String sql = """
                    INSERT INTO workouts
                    (
                        category,
                        title,
                        equipment,
                        tutorial,
                        youtube_url,
                        sets_count,
                        reps_count,
                        image_file
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null)
                return false;

            stmt.setString(1, workout.getCategory());
            stmt.setString(2, workout.getTitle());
            stmt.setString(3, workout.getEquipment());
            stmt.setString(4, workout.getDescription());
            stmt.setString(5, workout.getVideoUrl());
            stmt.setInt(6, workout.getSets());
            stmt.setString(7, workout.getReps());
            stmt.setString(8, workout.getImagePath());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Workout workout) {
        String sql = """
                    UPDATE workouts
                    SET
                        category = ?,
                        title = ?,
                        equipment = ?,
                        tutorial = ?,
                        youtube_url = ?,
                        sets_count = ?,
                        reps_count = ?,
                        image_file = ?
                    WHERE workout_id = ?
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null)
                return false;

            stmt.setString(1, workout.getCategory());
            stmt.setString(2, workout.getTitle());
            stmt.setString(3, workout.getEquipment());
            stmt.setString(4, workout.getDescription());
            stmt.setString(5, workout.getVideoUrl());
            stmt.setInt(6, workout.getSets());
            stmt.setString(7, workout.getReps());
            stmt.setString(8, workout.getImagePath());
            stmt.setInt(9, workout.getWorkoutId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int workoutId) {
        String deleteStepsSql = """
                    DELETE FROM workout_steps
                    WHERE workout_id = ?
                """;

        String deleteWorkoutSql = """
                    DELETE FROM workouts
                    WHERE workout_id = ?
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(deleteStepsSql)) {
                    stmt.setInt(1, workoutId);
                    stmt.executeUpdate();
                }

                int affected;

                try (PreparedStatement stmt = conn.prepareStatement(deleteWorkoutSql)) {
                    stmt.setInt(1, workoutId);
                    affected = stmt.executeUpdate();
                }

                conn.commit();
                return affected > 0;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}