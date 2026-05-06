package admin.workout;

import config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutService {

    // ─── Ambil semua workout ──────────────────────────────────────
    public List<Workout> getAll() {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT * FROM workouts ORDER BY workout_id";
        try (Connection conn = Database.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─── Ambil satu workout by ID (dengan steps) ─────────────────
    public Workout getById(int id) {
        Workout w = null;
        String sql = "SELECT * FROM workouts WHERE workout_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                w = mapRow(rs);
                w.setSteps(getSteps(id, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return w;
    }

    private List<WorkoutStep> getSteps(int workoutId, Connection conn) throws SQLException {
        List<WorkoutStep> steps = new ArrayList<>();
        String sql = "SELECT * FROM workout_steps WHERE workout_id = ? ORDER BY step_order";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                steps.add(new WorkoutStep(
                    rs.getInt("step_id"),
                    rs.getInt("workout_id"),
                    rs.getInt("step_order"),
                    rs.getString("instruction"),
                    rs.getString("duration")
                ));
            }
        }
        return steps;
    }

    // ─── Insert workout baru ──────────────────────────────────────
    public boolean insert(Workout w) {
        String sql = "INSERT INTO workouts (category, title, equipment, description, video_url, sets, reps, image_path) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, w.getCategory());
            ps.setString(2, w.getTitle());
            ps.setString(3, w.getEquipment());
            ps.setString(4, w.getDescription());
            ps.setString(5, w.getVideoUrl());
            ps.setInt   (6, w.getSets());
            ps.setString(7, w.getReps());
            ps.setString(8, w.getImagePath());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Update workout ───────────────────────────────────────────
    public boolean update(Workout w) {
        String sql = "UPDATE workouts SET category=?, title=?, equipment=?, description=?, video_url=?, sets=?, reps=?, image_path=? WHERE workout_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, w.getCategory());
            ps.setString(2, w.getTitle());
            ps.setString(3, w.getEquipment());
            ps.setString(4, w.getDescription());
            ps.setString(5, w.getVideoUrl());
            ps.setInt   (6, w.getSets());
            ps.setString(7, w.getReps());
            ps.setString(8, w.getImagePath());
            ps.setInt   (9, w.getWorkoutId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Hapus workout ────────────────────────────────────────────
    public boolean delete(int id) {
        String sql = "DELETE FROM workouts WHERE workout_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Workout mapRow(ResultSet rs) throws SQLException {
        return new Workout(
            rs.getInt("workout_id"),
            rs.getString("category"),
            rs.getString("title"),
            rs.getString("equipment"),
            rs.getString("description"),
            rs.getString("video_url"),
            rs.getInt("sets"),
            rs.getString("reps"),
            rs.getString("image_path")
        );
    }
}