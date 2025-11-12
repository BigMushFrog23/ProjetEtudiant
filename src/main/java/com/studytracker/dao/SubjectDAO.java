package com.studytracker.dao;

import com.studytracker.database.DatabaseManager;
import com.studytracker.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject entity.
 */
public class SubjectDAO {
    private final Connection connection;

    public SubjectDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new subject
     */
    public Subject create(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (user_id, name, description, color) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subject.getUserId());
            stmt.setString(2, subject.getName());
            stmt.setString(3, subject.getDescription());
            stmt.setString(4, subject.getColor());

            stmt.executeUpdate();

            // Get the last inserted ID using SQLite's last_insert_rowid()
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    subject.setId(rs.getInt(1));
                    return subject;
                }
            }
        }
        return null;
    }

    /**
     * Find subject by ID
     */
    public Subject findById(int id) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSubject(rs);
            }
        }
        return null;
    }

    /**
     * Find all subjects for a user
     */
    public List<Subject> findByUserId(int userId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE user_id = ? ORDER BY name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }

    /**
     * Get all subjects
     */
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }

    /**
     * Update subject
     */
    public void update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, description = ?, color = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setString(3, subject.getColor());
            stmt.setInt(4, subject.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Delete subject (will cascade to chapters and exams)
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Get total number of subjects for a user
     */
    public int countByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Subject object
     */
    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setUserId(rs.getInt("user_id"));
        subject.setName(rs.getString("name"));
        subject.setDescription(rs.getString("description"));
        subject.setColor(rs.getString("color"));
        return subject;
    }
}
