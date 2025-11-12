package com.studytracker.dao;

import com.studytracker.database.DatabaseManager;
import com.studytracker.model.StudySession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for StudySession entity.
 */
public class StudySessionDAO {
    private final Connection connection;

    public StudySessionDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new study session
     */
    public StudySession create(StudySession session) throws SQLException {
        String sql = "INSERT INTO study_sessions (chapter_id, user_id, hours_studied, session_date, notes, xp_earned) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, session.getChapterId());
            stmt.setInt(2, session.getUserId());
            stmt.setDouble(3, session.getHoursStudied());
            stmt.setString(4, session.getSessionDate().toString());
            stmt.setString(5, session.getNotes());
            stmt.setInt(6, session.getXpEarned());

            stmt.executeUpdate();

            // Get the last inserted ID using SQLite's last_insert_rowid()
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    session.setId(rs.getInt(1));
                    return session;
                }
            }
        }
        return null;
    }

    /**
     * Find study session by ID
     */
    public StudySession findById(int id) throws SQLException {
        String sql = "SELECT * FROM study_sessions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudySession(rs);
            }
        }
        return null;
    }

    /**
     * Find all study sessions for a user
     */
    public List<StudySession> findByUserId(int userId) throws SQLException {
        List<StudySession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions WHERE user_id = ? ORDER BY session_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(mapResultSetToStudySession(rs));
            }
        }
        return sessions;
    }

    /**
     * Find all study sessions for a chapter
     */
    public List<StudySession> findByChapterId(int chapterId) throws SQLException {
        List<StudySession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions WHERE chapter_id = ? ORDER BY session_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(mapResultSetToStudySession(rs));
            }
        }
        return sessions;
    }

    /**
     * Get all study sessions
     */
    public List<StudySession> findAll() throws SQLException {
        List<StudySession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions ORDER BY session_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapResultSetToStudySession(rs));
            }
        }
        return sessions;
    }

    /**
     * Update study session
     */
    public void update(StudySession session) throws SQLException {
        String sql = "UPDATE study_sessions SET hours_studied = ?, notes = ?, xp_earned = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, session.getHoursStudied());
            stmt.setString(2, session.getNotes());
            stmt.setInt(3, session.getXpEarned());
            stmt.setInt(4, session.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Delete study session
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM study_sessions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Get total hours studied for a user
     */
    public double getTotalHoursByUserId(int userId) throws SQLException {
        String sql = "SELECT SUM(hours_studied) FROM study_sessions WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /**
     * Get total hours studied for a chapter
     */
    public double getTotalHoursByChapterId(int chapterId) throws SQLException {
        String sql = "SELECT SUM(hours_studied) FROM study_sessions WHERE chapter_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /**
     * Map ResultSet to StudySession object
     */
    private StudySession mapResultSetToStudySession(ResultSet rs) throws SQLException {
        StudySession session = new StudySession();
        session.setId(rs.getInt("id"));
        session.setChapterId(rs.getInt("chapter_id"));
        session.setUserId(rs.getInt("user_id"));
        session.setHoursStudied(rs.getDouble("hours_studied"));
        session.setSessionDate(LocalDateTime.parse(rs.getString("session_date")));
        session.setNotes(rs.getString("notes"));
        session.setXpEarned(rs.getInt("xp_earned"));
        return session;
    }
}
