package admin.workout;

import config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutService {

    // ================= GET ALL =================
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
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                Workout w = new Workout(
                    rs.getInt("workout_id"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("equipment"),
                    rs.getString("tutorial"),
                    rs.getString("youtube_url"),
                    rs.getInt("sets_count"),
                    rs.getString("reps_count"),
                    rs.getString("image_file")
                );

                list.add(w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= GET DETAIL =================
    public Workout getById(int id) {

        Workout workout = null;

        String sql = """
            SELECT *
            FROM workouts
            WHERE workout_id = ?
        """;

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

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
                    rs.getString("image_file")
                );

                workout.setSteps(getSteps(id, conn));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return workout;
    }

    // ================= GET STEPS =================
    private List<WorkoutStep> getSteps(int workoutId, Connection conn)
            throws SQLException {

        List<WorkoutStep> list = new ArrayList<>();

        String sql = """
            SELECT *
            FROM workout_steps
            WHERE workout_id = ?
            ORDER BY step_order ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workoutId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new WorkoutStep(
                    rs.getInt("step_id"),
                    rs.getInt("workout_id"),
                    rs.getInt("step_order"),
                    rs.getString("instruction"),
                    rs.getString("duration")
                ));
            }
        }

        return list;
    }

    // ================= INSERT =================
    public boolean insert(Workout w) {

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
            VALUES (?,?,?,?,?,?,?,?)
        """;

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, w.getCategory());
            ps.setString(2, w.getTitle());
            ps.setString(3, w.getEquipment());
            ps.setString(4, w.getDescription());
            ps.setString(5, w.getVideoUrl());
            ps.setInt(6, w.getSets());
            ps.setString(7, w.getReps());
            ps.setString(8, w.getImagePath());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(Workout w) {

        String sql = """
            UPDATE workouts
            SET
                category=?,
                title=?,
                equipment=?,
                tutorial=?,
                youtube_url=?,
                sets_count=?,
                reps_count=?,
                image_file=?
            WHERE workout_id=?
        """;

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, w.getCategory());
            ps.setString(2, w.getTitle());
            ps.setString(3, w.getEquipment());
            ps.setString(4, w.getDescription());
            ps.setString(5, w.getVideoUrl());
            ps.setInt(6, w.getSets());
            ps.setString(7, w.getReps());
            ps.setString(8, w.getImagePath());
            ps.setInt(9, w.getWorkoutId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {

        String sql = "DELETE FROM workouts WHERE workout_id=?";

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}